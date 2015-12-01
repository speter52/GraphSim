#!/usr/bin/env bash

ant compile.module.inputgenerator.production

if  ant compile.module.inputgenerator.production | grep -q "BUILD SUCCESSFUL"; then
    java -cp Modules/*:out/production/InputGenerator/ Main "$@"
fi