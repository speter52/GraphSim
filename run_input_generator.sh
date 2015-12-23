#!/usr/bin/env bash
#TODO: Need to move to Tools folder but right now getting strange BUILD FAILED error when
#TODO: that directory

ant inputgenerator 

if  ant inputgenerator | grep -q "BUILD SUCCESSFUL"; then
    java -cp Modules/*:bin/InputGenerator/ Main "$@"
fi
