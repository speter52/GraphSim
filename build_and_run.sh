#!/usr/bin/env bash
#Use build.xml to run ANT and build source code, then add required modules to class path and run main
#Note: Also need to give build script run permissions(chmod)

if [ "$#" -ne 1 ]; then
    echo "Too many arguments - only need input YAML file that has the graph representation."
    exit 1
fi

if [[ $1 != *".yml" ]]; then
    echo "The input has to be a YAML(*.yml) file."
    exit 1
fi

ant compile.module.seniorthesis.production

if  ant compile.module.seniorthesis.production | grep -q "BUILD SUCCESSFUL"; then
    java -cp Modules/snakeyaml-1.16.jar:out/production/SeniorThesis/ com.Main $1
fi

