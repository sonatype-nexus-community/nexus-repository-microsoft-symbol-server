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
## Microsoft Symbol Server Repositories

### Introduction

### Proxying Microsoft Symbol Server Repositories

You can set up a Microsoft Symbol Server proxy repository to access a remote repository location, for example to proxy the official Microsoft Symbol Server
repository at [http://msdl.microsoft.com/download/symbols](http://msdl.microsoft.com/download/symbols)

To proxy a Microsoft Symbol Server repository, you simply create a new 'microsoft symbol server (proxy)' as documented in 
[Repository Management](https://help.sonatype.com/display/NXRM3/Configuration#Configuration-RepositoryManagement) in
details. Minimal configuration steps are:

- Define 'Name' e.g. symbol-proxy
- Define URL for 'Remote storage' e.g. [http://msdl.microsoft.com/download/symbols](http://msdl.microsoft.com/download/symbols)
- Select a 'Blob store' for 'Storage'

### Configuring Visual Studio for use with Nexus Repository

To configure Visual Studio to use Nexus Repository as a Proxy for remote Microsoft Symbol Server sites.

In Visual Studio, navigate through the following menu: 

* **Tools | Options | Debugging | Symbols**

From there, add a new symbol server pointing at your proxy you setup in Nexus Repository, example: http://localhost:8081/repository/symbol-proxy

After you have done these steps, downloads of PDBs should get routed through Nexus Repository.

### Browsing Microsoft Symbol Server Repository Packages

You can browse Microsoft Symbol Server repositories in the user interface inspecting the components and assets and their details, as
described in [Browsing Repositories and Repository Groups](https://help.sonatype.com/display/NXRM3/Browsing+Repositories+and+Repository+Groups).
