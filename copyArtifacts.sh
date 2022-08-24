#!/bin/bash
if [ ! -f ./pom.xml ]
then
    echo "No pom file: ./pom.xml"
    exit 1
fi
if [ ! -f ./target/jpa-base-entity.jar ]
then
    echo "No jar file: ./target/jpa-base-entity.jar"
    exit 2
fi
if [ ! -d ./artifacts ]
then
    mkdir artifacts
fi
cp target/jpa-base-entity.jar artifacts/jpa-base-entity.jar
cp pom.xml artifacts/pom.xml