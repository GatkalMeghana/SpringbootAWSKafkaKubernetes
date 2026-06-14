#!/bin/bash

APP_BASE_DIR=/data/FD/IndexService
DEPLOYMENT_FILE=index-service.jar

JAVA_HOME=/usr/java/jdk1.8.0_151

if [ $# -eq 0 ] ; then
    echo 'Argument missing: profile'
    echo 'Usage : ./startup.sh [profile]'
    exit 1
fi

echo 'Starting Index Service'

nohup $JAVA_HOME/bin/java -Xmx256m -Xss256k -Dprofile="$1" -Dapp.home=${APP_BASE_DIR} -jar ${APP_BASE_DIR}/bin/${DEPLOYMENT_FILE} > /dev/null 2>&1 &