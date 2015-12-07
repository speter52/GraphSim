#!/usr/bin/env bash

# Check if there is more than 1 argument
if [ "$#" -ne 2 ]; then
    echo "Too many arguments - only need input YAML file that has the graph representation along with maximum number of iterations."
    exit 1
fi

# Check if the 1 argument is a YAML file
if [[ $1 != *".yml" ]]; then
    echo "The first argument has to be a YAML(*.yml) file."
    exit 1
fi

# Build project and output results
ant compile.module.graphsim.production

# Build again to determine if build was successful, and if so run project
# TODO: Find a way to output the build results while also searching the output for "BUILD SUCCESSFUL". Right now
# TODO: running ANT once to build and another time to parse the output.
if  ant compile.module.graphsim.production | grep -q "BUILD SUCCESSFUL"; then
    java -cp Modules/*:out/production/GraphSim/ com.Main $1 $2
fi

