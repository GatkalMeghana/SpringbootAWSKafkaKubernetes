#!/bin/bash

APP_BASE_DIR=/data/FD/IndexService
DEPLOYMENT_FILE=index-service.jar
APP_BASE_PATH=http://localhost:9046/ForrSVC/

JAVA_HOME=/usr/java/jdk1.8.0_151

if [ $# -eq 0 ] ; then
    echo 'Argument missing: profile'
    echo 'Usage : ./restart.sh [profile]'
    exit 1
fi

echo 'Stopping Index Service'
curl -X POST "${APP_BASE_PATH}/actuator/shutdown"

# wait 15 seconds
echo 'Going to sleep for 15s'
sleep 15s

echo 'Starting Index Service'

nohup $JAVA_HOME/bin/java -Xmx256m -Xss256k -Dprofile="$1" -Dapp.home=${APP_BASE_DIR} -jar ${APP_BASE_DIR}/bin/${DEPLOYMENT_FILE} > /dev/null 2>&1 &
