#!/bin/sh

find . -name "*.c" -exec ../../../build/linuxcnc/bin/halcompile --install {} \;
find . -name "*.comp" -exec ../../../build/linuxcnc/bin/halcompile --install {} \;

