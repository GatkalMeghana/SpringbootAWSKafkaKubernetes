#!/bin/bash

#$1 propKey
#$2 profile
#$3 gitUname
#$4 gitUpwd
#$5 configUser
#$6 configPass

CONFIG_SERV_BASE_DIR=/data/ForrREST/configservice
DEPLOYMENT_FILE=ForrCloudConfigService.jar
JAVA_HOME=/usr/java/jdk1.8.0_151

nohup ${JAVA_HOME}/bin/java -Xmx256m -Xss256k -DpropKey=$1 -Dprofile=$2 -DgitUname=$3 -DgitUpwd=$4 -DconfigUser=$5 -DconfigPass=$6 -jar ${CONFIG_SERV_BASE_DIR}/bin/${DEPLOYMENT_FILE} > /dev/null 2>&1 &