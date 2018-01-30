FROM openjdk:9-jre

MAINTAINER Brian Schlining <bschlining@gmail.com>

ENV APP_HOME /opt/vars-anno-sync

RUN mkdir -p ${APP_HOME}

COPY build/distributions/vars-anno-sync-*.zip ${APP_HOME}

RUN unzip ${APP_HOME}/*.zip && rm ${APP_HOME}/*.zip

ENTRYPOINT ${APP_HOME}/bin/vars-anno-sync

