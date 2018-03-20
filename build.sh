#!/usr/bin/env bash

echo "--- Building vars-anno-sync (reminder: run docker login first!!)"
gradle clean assembleDist && \
  docker build -t mbari/vars-anno-sync . && \
  docker push mbari/vars-anno-sync
