#!/bin/bash

set -u
set -e

export JVM_OPTS="\
-XX:+CMSClassUnloadingEnabled \
-XX:PermSize=512M -XX:MaxPermSize=1024M \
-Dsbt.log.noformat=true"


HTTP_METHOD='GET'
HTTP_URLS='http://localhost:8080,http://localhost:8080' # Comma separated baseURls
HTTP_RESOURCE_PATH='/json'
RUN_NAME='HelloGoLoadBalanced'
PAGE_NAME='json'

RAMP_UP_USERS_PER_SEC_START='1.0'       # Must be a floating-point number string
RAMP_UP_USERS_PER_SEC_END='1000.0'      # Must be a floating-point number string
RAMP_UP_DURATION_SECS='30'              # Must be an integer string. No decimal point
STEADY_STATE_USERS_PER_SEC='1000.0'     # Must be a floating-point number string
STEADY_STATE_DURATION_SECS='300'        # Must be an intenger string. No decimal point
RAMP_DOWN_USERS_PER_SEC_START='1000.0'  # Must be a floating-point number string
RAMP_DOWN_USERS_PER_SEC_END='1.0'       # Must be a floating-point number string
RAMP_DOWN_DURATION_SECS='1'             # Must be an integer string. No decimal point


if [ -z "$HTTP_URLS" ]
then
  echo "HTTP_URLs is unset. Open start.sh and set a valid URL"
  exit 1
fi

SBT_CMD="; set javaOptions ++= Seq(\"-XX:PermSize=512M\", \
\"-XX:MaxPermSize=1024M\", \
\"-Xms4096m\", \"-Xmx7224m\", \
\"-Dsun.net.inetaddr.ttl=0\", \
\"-Djava.net.preferIPv4Stack=true\", \
\"-Djava.net.preferIPv6Addresses=false\", \
\"-Dloadbalancedworkload.method=${HTTP_METHOD}\", \
\"-Dloadbalancedworkload.baseURLs=${HTTP_URLS}\", \
\"-Dloadbalancedworkload.resourcePath=${HTTP_RESOURCE_PATH}\", \
\"-Dloadbalancedworkload.scenarioName=${RUN_NAME}\", \
\"-Dloadbalancedworkload.pageName=${PAGE_NAME}\", \
\"-Dloadbalancedworkload.rampUpUsersPerSecStart=${RAMP_UP_USERS_PER_SEC_START}\", \
\"-Dloadbalancedworkload.rampUpUsersPerSecEnd=${RAMP_UP_USERS_PER_SEC_END}\", \
\"-Dloadbalancedworkload.rampUpDurationSecs=${RAMP_UP_DURATION_SECS}\", \
\"-Dloadbalancedworkload.steadyStateUsersPerSec=${STEADY_STATE_USERS_PER_SEC}\", \
\"-Dloadbalancedworkload.steadyStateDurationSecs=${STEADY_STATE_DURATION_SECS}\", \
\"-Dloadbalancedworkload.rampDownUsersPerSecStart=${RAMP_DOWN_USERS_PER_SEC_START}\", \
\"-Dloadbalancedworkload.rampDownUsersPerSecEnd=${RAMP_DOWN_USERS_PER_SEC_END}\", \
\"-Dloadbalancedworkload.rampDownDurationSecs=${RAMP_DOWN_DURATION_SECS}\", \
\"-Dgatling.core.outputDirectoryBaseName=${RUN_NAME}\") ; testOnly meteorfox.benchmarks.LoadBalancedOpenModelWorkload"


sbt "${SBT_CMD}"

