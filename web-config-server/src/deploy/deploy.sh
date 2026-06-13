#!/bin/bash

#$1 Basic authorization value for manage endpoint
#$2 propKey
#$3 profile
#$4 gitUname
#$5 gitUpwd
#$6 configUser
#$7 configPass

CONFIG_SERV_BASE_DIR=/data/ForrREST/configservice
DEPLOYMENT_FILE=ForrCloudConfigService.jar
STARTUP_SCRIPT=startup.sh
DEPLOYMENT_FOLDER=/home/tomcat/deploy/

# shutdown the application first
curl -X POST http://localhost:9990/manage/shutdown -H "authorization: Basic $1" -H 'cache-control: no-cache'

# wait 15 seconds
echo 'sleep 15 seconds'
sleep 15s

# copy the jar file
cp ${DEPLOYMENT_FOLDER}/${DEPLOYMENT_FILE} ${CONFIG_SERV_BASE_DIR}/bin
cp ${DEPLOYMENT_FOLDER}/${STARTUP_SCRIPT} ${CONFIG_SERV_BASE_DIR}/bin

chmod +x ${CONFIG_SERV_BASE_DIR}/bin/${STARTUP_SCRIPT}

#${CONFIG_SERV_BASE_DIR}/bin/${STARTUP_SCRIPT} $2 $3 $4 $5 $6 $7