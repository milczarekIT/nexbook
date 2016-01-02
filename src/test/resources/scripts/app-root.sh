#!/bin/bash

FULL_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SCRIPTS_PATH="src/test/resources/scripts"
APP_ROOT="${FULL_PATH/'/'$SCRIPTS_PATH/''}"

echo ${APP_ROOT}