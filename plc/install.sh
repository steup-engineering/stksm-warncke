#!/bin/sh

find . -name "*.c" -exec halcompile --install {} \;
find . -name "*.comp" -exec halcompile --install {} \;

