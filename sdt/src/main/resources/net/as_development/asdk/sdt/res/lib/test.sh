#!/bin/bash

. ./lib_log.sh
. ./lib_validate.sh
. ./lib_fileutils.sh
. ./lib_apt.sh
. ./lib_config.sh


CFG_OXDB_WRITE_HOST=192.168.0.191
CFG_OXDB_PORT=3307
CFG_OXDB_DB_NAME=configdb

lib_config_save_prop "/tmp/cfg.properties" "writeUrl" "jdbc:mysql://${CFG_OXDB_WRITE_HOST}:${CFG_OXDB_PORT}/${CFG_OXDB_DB_NAME}"
#lib_config_save_prop "/tmp/cfg.properties" "writeUrl" "jdbc:mysql://${CFG_OXDB_WRITE_HOST}:${CFG_OXDB_PORT}/${CFG_OXDB_DB_NAME}"
