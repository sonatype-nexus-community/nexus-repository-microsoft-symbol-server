/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2019-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.microsoftsymbolserver.internal.proxy;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.plugins.microsoftsymbolserver.internal.AssetKind;
import org.sonatype.nexus.plugins.microsoftsymbolserver.internal.util.MicrosoftSymbolServerDataAccess;
import org.sonatype.nexus.plugins.microsoftsymbolserver.internal.util.MicrosoftSymbolServerPathUtils;
import org.sonatype.nexus.repository.cache.CacheInfo;
import org.sonatype.nexus.repository.config.Configuration;
import org.sonatype.nexus.repository.proxy.ProxyFacet;
import org.sonatype.nexus.repository.proxy.ProxyFacetSupport;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.Bucket;
import org.sonatype.nexus.repository.storage.StorageFacet;
import org.sonatype.nexus.repository.storage.StorageTx;
import org.sonatype.nexus.repository.storage.TempBlob;
import org.sonatype.nexus.repository.transaction.TransactionalStoreBlob;
import org.sonatype.nexus.repository.transaction.TransactionalTouchBlob;
import org.sonatype.nexus.repository.transaction.TransactionalTouchMetadata;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.matchers.token.TokenMatcher;
import org.sonatype.nexus.transaction.UnitOfWork;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.repository.storage.AssetEntityAdapter.P_ASSET_KIND;

/**
 * Microsoft Symbol Server {@link ProxyFacet} implementation.
 *
 * @since 0.0.1
 */
@Named
public class MicrosoftSymbolServerProxyFacetImpl
    extends ProxyFacetSupport
  implements MicrosoftSymbolServerProxyFacet
{
  private MicrosoftSymbolServerPathUtils pathUtils;

  private MicrosoftSymbolServerDataAccess dataAccess;

  @Inject
  public MicrosoftSymbolServerProxyFacetImpl(final MicrosoftSymbolServerPathUtils pathUtils,
                                             final MicrosoftSymbolServerDataAccess dataAccess)
  {
    this.pathUtils = checkNotNull(pathUtils);
    this.dataAccess = checkNotNull(dataAccess);
  }

  // HACK: Workaround for known CGLIB issue, forces an Import-Package for org.sonatype.nexus.repository.config
  @Override
  protected void doValidate(final Configuration configuration) throws Exception {
    super.doValidate(configuration);
  }

  @Override
  @TransactionalTouchBlob
  public Content getAsset(final String assetPath) {
    StorageTx tx = UnitOfWork.currentTx();

    Asset asset = dataAccess.findAsset(tx, tx.findBucket(getRepository()), assetPath);
    if (asset == null) {
      return null;
    }
    return dataAccess.toContent(asset, tx.requireBlob(asset.requireBlobRef()));
  }

  @Nullable
  @Override
  protected Content getCachedContent(final Context context) throws IOException {
    AssetKind assetKind = context.getAttributes().require(AssetKind.class);
    TokenMatcher.State matcherState = pathUtils.matcherState(context);
    switch (assetKind) {
      case PDB_FILE:
        return getAsset(pathUtils.buildAssetPath(matcherState, pathUtils.name(matcherState)));
      default:
        throw new IllegalStateException("Received an invalid AssetKind of type: " + assetKind.name());
    }
  }

  @Override
  protected HttpResponse execute(final Context context, final HttpClient client, final HttpRequestBase request) throws IOException {
    // Add specific User Agent header so that it will return properly from remote
    request.addHeader("User-Agent", "Microsoft-Symbol-Server/6.3.9600.17095");
    return super.execute(context, client, request);
  }

  @Override
  protected Content store(final Context context, final Content content) throws IOException {
    AssetKind assetKind = context.getAttributes().require(AssetKind.class);
    TokenMatcher.State matcherState = pathUtils.matcherState(context);
    switch (assetKind) {
      case PDB_FILE:
        return putContent(content,
            assetKind,
            pathUtils.buildAssetPath(matcherState, pathUtils.name(matcherState)));
      default:
        throw new IllegalStateException("Received an invalid AssetKind of type: " + assetKind.name());
    }
  }

  private Content putContent(final Content content,
                             final AssetKind assetKind,
                             final String assetPath)
      throws IOException
  {
    StorageFacet storageFacet = facet(StorageFacet.class);

    try (TempBlob tempBlob = storageFacet.createTempBlob(content.openInputStream(), dataAccess.HASH_ALGORITHMS)) {
      return findOrCreateAsset(tempBlob, content, assetKind, assetPath);
    }
  }

  @TransactionalStoreBlob
  protected Content findOrCreateAsset(final TempBlob tempBlob,
                                      final Content content,
                                      final AssetKind assetKind,
                                      final String assetPath) throws IOException
  {
    StorageTx tx = UnitOfWork.currentTx();
    Bucket bucket = tx.findBucket(getRepository());

    Asset asset = dataAccess.findAsset(tx, bucket, assetPath);

    if (asset == null) {
      asset = tx.createAsset(bucket, getRepository().getFormat());
      asset.name(assetPath);
      asset.formatAttributes().set(P_ASSET_KIND, assetKind.name());
    }

    return dataAccess.saveAsset(tx, asset, tempBlob, content);
  }

  @Override
  protected void indicateVerified(final Context context, final Content content, final CacheInfo cacheInfo)
      throws IOException
  {
    setCacheInfo(content, cacheInfo);
  }

  @TransactionalTouchMetadata
  public void setCacheInfo(final Content content, final CacheInfo cacheInfo) throws IOException {
    StorageTx tx = UnitOfWork.currentTx();
    Asset asset = Content.findAsset(tx, tx.findBucket(getRepository()), content);
    if (asset == null) {
      log.debug(
          "Attempting to set cache info for non-existent Microsoft Symbol Server asset {}", content.getAttributes().require(Asset.class)
      );
      return;
    }
    log.debug("Updating cacheInfo of {} to {}", asset, cacheInfo);
    CacheInfo.applyToAsset(asset, cacheInfo);
    tx.saveAsset(asset);
  }

  @Override
  protected String getUrl(@Nonnull final Context context) {
    return context.getRequest().getPath().substring(1);
  }
}
