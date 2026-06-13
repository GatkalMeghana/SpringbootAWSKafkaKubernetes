#!/bin/bash

#$1 Profile

APP_BASE_DIR=/data/FD/IndexService
DEPLOYMENT_FILE=index-service.jar
STARTUP_SCRIPT=startup.sh
RESTART_SCRIPT=restart.sh
DEPLOYMENT_FOLDER=/home/tomcat/deploy/Forrester/IndexService
APP_BASE_PATH=http://localhost:9046/ForrSVC/

echo 'Stopping Index Service'
# shutdown the application first
curl -X POST "${APP_BASE_PATH}/actuator/shutdown"

# wait 15 seconds
echo 'Going to sleep for 15s'
sleep 15s

# copy the jar file and the startup script
cp ${DEPLOYMENT_FOLDER}/${DEPLOYMENT_FILE} ${APP_BASE_DIR}/bin
cp ${DEPLOYMENT_FOLDER}/${STARTUP_SCRIPT} ${APP_BASE_DIR}/bin
cp ${DEPLOYMENT_FOLDER}/${RESTART_SCRIPT} ${APP_BASE_DIR}/bin

chmod +x ${APP_BASE_DIR}/bin/${STARTUP_SCRIPT}
chmod +x ${APP_BASE_DIR}/bin/${RESTART_SCRIPT}
