#!/usr/bin/env bash

echo "--- Building vars-anno-sync (reminder: run docker login first!!)"
gradle assembleDist && \
  docker build -t mbari/vars-anno-sync . && \
  docker push mbari/vars-anno-sync
