<!--

    Sonatype Nexus (TM) Open Source Version
    Copyright (c) 2019-present Sonatype, Inc.
    All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.

    This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
    which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.

    Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
    of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
    Eclipse Foundation. All other trademarks are the property of their respective owners.

-->
# Nexus Repository Microsoft Symbol Server Format

[![Maven Central](https://img.shields.io/maven-central/v/org.sonatype.nexus.plugins/nexus-repository-microsoft-symbol-server.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.sonatype.nexus.plugins%22%20AND%20a:%22nexus-repository-microsoft-symbol-server%22) [![CircleCI](https://circleci.com/gh/sonatype-nexus-community/nexus-repository-microsoft-symbol-server.svg?style=shield)](https://circleci.com/gh/sonatype-nexus-community/nexus-repository-microsoft-symbol-server) [![Join the chat at https://gitter.im/sonatype/nexus-developers](https://badges.gitter.im/sonatype/nexus-developers.svg)](https://gitter.im/sonatype/nexus-developers?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![DepShield Badge](https://depshield.sonatype.org/badges/sonatype-nexus-community/nexus-repository-microsoft-symbol-server/depshield.svg)](https://depshield.github.io)

# Table Of Contents

* [Developing](#developing)
   * [Requirements](#requirements)
   * [Building](#building)
* [Using Microsoft Symbol Server with Nexus Repository Manager 3](#using-microsoft-symbol-server-with-nexus-repository-manager-3)
* [Compatibility with Nexus Repository Manager 3 Versions](#compatibility-with-nexus-repository-manager-3-versions)
* [Features Implemented In This Plugin](#features-implemented-in-this-plugin)
* [Installing the plugin](#installing-the-plugin)
   * [Easiest Install](#easiest-install)
   * [Temporary Install](#temporary-install)
   * [(more) Permanent Install](#more-permanent-install)
   * [(most) Permament Install](#most-permanent-install)
* [The Fine Print](#the-fine-print)
* [Getting Help](#getting-help)
* [Integration Tests](#integration-tests)

## Developing

### Requirements

* [Apache Maven 3.3.3+](https://maven.apache.org/install.html)
* [Java 8+](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Network access to https://repository.sonatype.org/content/groups/sonatype-public-grid

Also, there is a good amount of information available at [Bundle Development](https://help.sonatype.com/display/NXRM3/Bundle+Development)

### Building

To build the project and generate the bundle use Maven

    mvn clean package -PbuildKar

If everything checks out, the bundle for Microsoft Symbol Server should be available in the `target` folder

#### Build with Docker

    docker build -t nexus-repository-microsoft-symbol-server .

#### Run as a Docker container

    docker run -d -p 8081:8081 --name nexus-repository-microsoft-symbol-server nexus-repository-microsoft-symbol-server 

For further information like how to persist volumes check out [the GitHub repo for our official image](https://github.com/sonatype/docker-nexus3).

After allowing some time to spin up, the application will be available from your browser at http://localhost:8081.

To read the generated admin password for your first login to the web UI, you can use the command below against the running docker container:

    docker exec -it nexus-repository-microsoft-symbol-server cat /nexus-data/admin.password && echo

For simplicity, you should check `Enable anonymous access` in the prompts following your first login.   

## Using Microsoft Symbol Server With Nexus Repository Manager 3

[We have detailed instructions on how to get started here!](docs/MICROSOFT_SYMBOL_SERVER_USER_DOCUMENTATION.md)

## Compatibility with Nexus Repository Manager 3 Versions

The table below outlines what version of Nexus Repository the plugin was built against

| Plugin Version | Nexus Repository Version |
|----------------|--------------------------|
| v0.0.1         | 3.19.0-01                |
| v0.0.8         | 3.23.0-03                |
| v0.0.9         | 3.28.1-01                |

If a new version of Nexus Repository is released and the plugin needs changes, a new release will be made, and this
table will be updated to indicate which version of Nexus Repository it will function against. This is done on a time 
available basis, as this is community supported. If you see a new version of Nexus Repository, go ahead and update the
plugin and send us a PR after testing it out!

All released versions can be found [here](https://github.com/sonatype-nexus-community/nexus-repository-microsoft-symbol-server/releases).

## Features Implemented In This Plugin 

| Feature | Implemented          |
|---------|----------------------|
| Proxy   | :heavy_check_mark: * |
| Hosted  |                      |
| Group   |                      |

`* tested VERY lightly`

## Installing the plugin

There are a range of options for installing the Microsoft Symbol server plugin. You'll need to build it first, and
then install the plugin with the options shown below:

### Easiest Install

Thanks to some upstream work in Nexus Repository (versions newer than 3.15), it's become a LOT easier to install a plugin. To install the `microsoft-symbol-server` plugin, follow these steps:

* Build the plugin with `mvn clean package -PbuildKar`
* Copy the `nexus-repository-microsoft-symbol-server-0.0.1-bundle.kar` file from your `target` folder to the `deploy` folder for your Nexus Repository installation.

Once you've done this, go ahead and either restart Nexus Repo, or go ahead and start it if it wasn't running to begin with.

You should see `microsoft symbol server (proxy)` in the available Repository Recipes to use, if all has gone according to plan :)

### Temporary Install

Installations done via the Karaf console will be wiped out with every restart of Nexus Repository. This is a
good installation path if you are just testing or doing development on the plugin.

* Enable Nexus Repo console: edit `<nexus_dir>/bin/nexus.vmoptions` and change `karaf.startLocalConsole`  to `true`.

  More details here: [Bundle Development](https://help.sonatype.com/display/NXRM3/Bundle+Development+Overview)

* Run Nexus Repo console:
  ```
  # sudo su - nexus
  $ cd <nexus_dir>/bin
  $ ./nexus run
  > bundle:install file:///tmp/nexus-repository-microsoft-symbol-server-0.0.1.jar
  > bundle:list
  ```
  (look for org.sonatype.nexus.plugins:nexus-repository-microsoft-symbol-server ID, should be the last one)
  ```
  > bundle:start <org.sonatype.nexus.plugins:nexus-repository-microsoft-symbol-server ID>
  ```

### (more) Permanent Install

For more permanent installs of the nexus-repository-microsoft-symbol-server plugin, follow these instructions:

* Copy the bundle (nexus-repository-microsoft-symbol-server-0.0.1.jar) into <nexus_dir>/deploy

This will cause the plugin to be loaded with each restart of Nexus Repository. As well, this folder is monitored
by Nexus Repository and the plugin should load within 60 seconds of being copied there if Nexus Repository
is running. You will still need to start the bundle using the karaf commands mentioned in the temporary install.

### (most) Permanent Install

If you are trying to use the Microsoft Symbol Server plugin permanently, it likely makes more sense to do the following:

* Copy the bundle into `<nexus_dir>/system/org/sonatype/nexus/plugins/nexus-repository-microsoft-symbol-server/0.0.1/nexus-repository-microsoft-symbol-server-0.0.1.jar`
* Make the following additions marked with + to `<nexus_dir>/system/org/sonatype/nexus/assemblies/nexus-core-feature/3.x.y/nexus-core-feature-3.x.y-features.xml`

   ```
         <feature prerequisite="false" dependency="false">wrap</feature>
   +     <feature prerequisite="false" dependency="false">nexus-repository-microsoft-symbol-server</feature>
   ```
   to the `<feature name="nexus-core-feature" description="org.sonatype.nexus.assemblies:nexus-core-feature" version="3.x.y.xy">` section below the last (above is an example, the exact last one may vary).
   
   And
   ```
   + <feature name="nexus-repository-microsoft-symbol-server" description="org.sonatype.nexus.plugins:nexus-repository-microsoft-symbol-server" version="0.0.1">
   +     <details>org.sonatype.nexus.plugins:nexus-repository-microsoft-symbol-server</details>
   +     <bundle>mvn:org.sonatype.nexus.plugins/nexus-repository-microsoft-symbol-server/0.0.1</bundle>
   + </feature>
    </features>
   ```
   as the last feature.
   
This will cause the plugin to be loaded and started with each startup of Nexus Repository.

## The Fine Print

It is worth noting that this is **NOT SUPPORTED** by Sonatype, and is a contribution of ours
to the open source community (read: you!)

Don't worry, using this community item does not "void your warranty". In a worst case scenario, you may be asked 
by the Sonatype Support team to remove the community item in order to determine the root cause of any issues.

Remember:

* Use this contribution at the risk tolerance that you have
* Do NOT file Sonatype support tickets related to Microsoft Symbol Server support in regard to this plugin
* DO file issues here on GitHub, so that the community can pitch in

Phew, that was easier than I thought. Last but not least of all:

Have fun creating and using this plugin and the Nexus platform, we are glad to have you here!

## Getting help

Looking to contribute to our code but need some help? There's a few ways to get information:

* Chat with us on [Gitter](https://gitter.im/sonatype/nexus-developers)
* Check out the [Nexus3](http://stackoverflow.com/questions/tagged/nexus3) tag on Stack Overflow
* Check out the [Nexus Repository User List](https://groups.google.com/a/glists.sonatype.com/forum/?hl=en#!forum/nexus-users)

## Integration Tests

There a still some rough edges around writing integration tests, which are noted below.
Please report any problems you find. 

The main tricks are:
1. Refactor the project into a “format” module, and an “it” module. 
   This allows the “format” module to be bundled up and used by the IT framework classes in the “it” module.
   In this project, the sub module: [nexus-repository-microsoft-symbol-server](nexus-repository-microsoft-symbol-server) is the "format" module.
   The sub module: [nexus-repository-microsoft-symbol-server-it](nexus-repository-microsoft-symbol-server-it) is the "it" module.
      
2. IT startup issue the first time they are run.

   I was struggling with some IT startup issues, and workaround steps that get the job done (until I find a better way):
   
    ----
    1. Install NXRM OSS: in [nexus-public](https://github.com/sonatype/nexus-public): `./mvnw clean install -DskipTests`
    2. Run a single NXRM OSS IT: (smells funny - only needed to fetch IT deps): in nexus-public: `mvn clean verify -pl :nexus-it-suite -Dit -Dtest=skip -Dit.test=RawHostedIT`
    3. Install Microsoft Symbol Server plugin: in nexus-repository-microsoft-symbol-server (parent module): `mvn clean install -DskipTests`
    4. Run Microsoft Symbol Server ITs: in nexus-repository-microsoft-symbol-server (parent module): `mvn clean verify -pl :nexus-repository-microsoft-symbol-server-it -Dit -Dtest=skip` or simply via: `mvn clean verify`
    ----
