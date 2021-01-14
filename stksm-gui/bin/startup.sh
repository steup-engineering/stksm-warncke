#!/bin/bash

SCRIPTDIR=`dirname $0`
GUIDIR=$SCRIPTDIR/../../../gui/ksm_warnecke

cd $GUIDIR
java -Dswing.aatext=true -Dswing.plaf.metal.controlFont=SansSerif-18 -Dswing.plaf.metal.userFont=SansSerif-18 -jar dist/ksm_warnecke.jar http://localhost:8080/hal/json/ $HOME/Dokumente/ksm/proc $HOME/Dokumente/ksm/param

