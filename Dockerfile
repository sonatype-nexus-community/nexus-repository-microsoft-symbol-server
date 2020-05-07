# declaration of NEXUS_VERSION must appear before first FROM command
# see: https://docs.docker.com/engine/reference/builder/#understand-how-arg-and-from-interact
ARG NEXUS_VERSION=latest

FROM maven:3-jdk-8-alpine AS build

COPY . /nexus-repository-microsoft-symbol-server/
RUN cd /nexus-repository-microsoft-symbol-server/; \
    mvn clean package -PbuildKar;

FROM sonatype/nexus3:$NEXUS_VERSION

ARG DEPLOY_DIR=/opt/sonatype/nexus/deploy/
USER root
COPY --from=build /nexus-repository-microsoft-symbol-server/nexus-repository-microsoft-symbol-server/target/nexus-repository-microsoft-symbol-server-*-bundle.kar ${DEPLOY_DIR}
USER nexus
