#!/bin/sh

if [ -z "$LINUXCNC_HOME" ]; then
  LINUXCNC_HOME="$EMC2_HOME"
fi

if [ -z "$LINUXCNC_HOME" ]; then
  echo 'ERROR: LINUXCNC_HOME not set (rip-environment not called?)'
  exit 1
fi

set -e

mvn clean install

mkdir -p "$LINUXCNC_HOME/java"
cp target/stksm-gui-*.jar "$LINUXCNC_HOME/java/stksm-friedl-gui.jar"
cp -r target/lib "$LINUXCNC_HOME/java"

mkdir -p "$LINUXCNC_HOME/bin"
cp script/launch.sh "$LINUXCNC_HOME/bin/stksm-friedl-gui"
chmod ugo+x "$LINUXCNC_HOME/bin/stksm-friedl-gui"

