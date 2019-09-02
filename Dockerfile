ARG NEXUS_VERSION=3.19.0

FROM maven:3-jdk-8-alpine AS build
ARG NEXUS_VERSION=3.19.0
ARG NEXUS_BUILD=01

COPY . /nexus-repository-microsoft-symbol-server/
RUN cd /nexus-repository-microsoft-symbol-server/; sed -i "s/3.19.0-01/${NEXUS_VERSION}-${NEXUS_BUILD}/g" pom.xml; \
    mvn clean package -PbuildKar;

FROM sonatype/nexus3:$NEXUS_VERSION
ARG NEXUS_VERSION=3.19.0
ARG NEXUS_BUILD=01
ARG MICROSOFT_SYMBOL_SERVER_VERSION=0.0.1
ARG DEPLOY_DIR=/opt/sonatype/nexus/deploy/
USER root
COPY --from=build /nexus-repository-microsoft-symbol-server/target/nexus-repository-microsoft-symbol-server-${MICROSOFT_SYMBOL_SERVER_VERSION}-bundle.kar ${DEPLOY_DIR}
USER nexus
