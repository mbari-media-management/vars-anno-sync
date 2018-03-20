#!/usr/bin/env bash

SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
APPDIR="$SCRIPTDIR/.."
java -Duser.timezone=UTC -cp "$APPDIR/conf":"$APPDIR/lib/*" org.mbari.m3.annosync.Main