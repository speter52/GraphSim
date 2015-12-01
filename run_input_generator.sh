#!/usr/bin/env bash
#TODO: Need to move to Tools folder but right now getting strange BUILD FAILED error when
#TODO: that directory

ant compile.module.inputgenerator.production

if  ant compile.module.inputgenerator.production | grep -q "BUILD SUCCESSFUL"; then
    java -cp Modules/*:out/production/InputGenerator/ Main "$@"
fi