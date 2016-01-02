#!/bin/bash

FULL_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SCRIPTS_PATH="src/test/resources/scripts"
APP_ROOT="${FULL_PATH/'/'$SCRIPTS_PATH/''}"

FIX_DIRS=("fix-logs" "fix-data")

for FIX_DIR in "${FIX_DIRS[@]}"
do
    DIR_TO_REMOVE="$APP_ROOT/$FIX_DIR"
    echo "remove $DIR_TO_REMOVE"
    rm -rf ${DIR_TO_REMOVE}
done
