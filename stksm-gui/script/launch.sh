#!/bin/bash

REST_URL="http://localhost:8080/hal/json/"

PROC_DIR="$HOME/Dokumente/ksm/proc"
PARAM_DIR="$HOME/Dokumente/ksm/param"

mkdir -p "$PROC_DIR" "$PARAM_DIR"

if [ -z "$LINUXCNC_HOME" ]; then
  LINUXCNC_HOME="$EMC2_HOME"
fi

JAVADIR="$LINUXCNC_HOME/java"

cd $JAVADIR
java \
  -Dswing.aatext=true \
  -Dswing.plaf.metal.controlFont=SansSerif-18 \
  -Dswing.plaf.metal.userFont=SansSerif-18 \
  -jar stksm-warncke-gui.jar \
  "$REST_URL" "$PROC_DIR" "$PARAM_DIR"

