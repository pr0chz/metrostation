#!/bin/bash

cat $1 | grep \"$2 | json -g -a dumpTS tag cellLocation -o json -j0 | sed s/\},/\},\\n/g
