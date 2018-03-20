FROM openjdk:9-jre

MAINTAINER Brian Schlining <bschlining@gmail.com>

ENV APP_HOME /opt/vars-anno-sync

RUN mkdir -p /opt

COPY build/distributions/vars-anno-sync-*.zip /opt

WORKDIR /opt

RUN unzip *.zip && \
  rm *.zip && \
  ln -s vars-anno-sync-*/ vars-anno-sync

CMD ${APP_HOME}/bin/main.sh
