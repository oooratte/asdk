#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
# either express or implied. See the License for the specific
# language governing permissions and limitations under the License.
#

# break on errors

set -e

# pass the whole environment to subshells !
# e.g. run-scriptlet needs our environment variables ...

set -o allexport

#-----------------------------------------------------------------------------------------
echo "bootstrap SDT ..."

SDT_BOOTCFG=$HOME/.sdt/config.properties

if [ ! -f $SDT_BOOTCFG ];
then
	echo "Could not load bootstrap configuration from file $SDT_BOOTCFG."
	exit 1
fi

#-----------------------------------------------------------------------------------------
echo "... load boot config"

. $SDT_BOOTCFG

#-----------------------------------------------------------------------------------------

if [ -z $SDT_HOME ];
then
	echo "SDT_HOME not configured."
	exit 1
fi

#-----------------------------------------------------------------------------------------
echo "... define environment"

SDT_SHARE=$HOME/.sdt/share

SDT_BIN_DIR=$SDT_HOME/bin
SDT_LIB_DIR=$SDT_HOME/lib
SDT_TEMP_DIR=$SDT_HOME/temp

SDT_CONFIG_DIR=$SDT_HOME/config
SDT_STATES_DIR=$SDT_HOME/states
SDT_LAYOUT_DIR=$SDT_HOME/layout

SDT_SHARED_STATES_DIR=$SDT_SHARE/states
SDT_SHARED_CONFIG_DIR=$SDT_SHARE/config
SDT_SHARED_DEBUG_DIR=$SDT_SHARE/debug

SDT_LAYOUT_SERVICES_FILE=$SDT_LAYOUT_DIR/services.txt

#-----------------------------------------------------------------------------------------
echo "... load framework libs"

. $SDT_HOME/lib/lib.sh

#-----------------------------------------------------------------------------------------
echo "... define functions"

#-----------------------------------------------------------------------------------------
function sdt_show_help ()
{
    lib_log_info " "
    lib_log_info "sdt.sh [options]"
    lib_log_info " "
    lib_log_info "    options :"
    lib_log_info " "
    lib_log_info "    -rs | --run-scriptlet <scriptlet> [arguments]"
    lib_log_info " "
    lib_log_info "                 run the specified scriptlet"
    lib_log_info "                 The scriptlet has to be defined relative to SDT_HOME."
    lib_log_info "                 All other parameters of current command line are passed to the scriptlet."
    lib_log_info " "
    lib_log_info "    -rc | --run-command <command> [arguments]"
    lib_log_info " "
    lib_log_info "                 run the specified command"
    lib_log_info "                 The command will be executed within the context of SDT."
    lib_log_info "                 All arguments are passed to the command."
    lib_log_info " "
    lib_log_info "    -d | --debug"
    lib_log_info " "
    lib_log_info "                 enable debug logging"
    lib_log_info " "
    lib_log_info "    -h | --help"
    lib_log_info " "
    lib_log_info "                 show this help"
    lib_log_info " "
}

#-----------------------------------------------------------------------------------------
function sdt_run_scriptlet ()
{
    local v_scriptlet="$1"
    local v_args="$2"
    
    lib_validate_var_is_set v_scriptlet "Illegal argument 'scriptlet'."
    # args are optional !

    local v_cmd="${SDT_HOME}/${v_scriptlet}"
    
    if [ ! -z "${v_args}" ];
    then
        # note: args starts with space already !
        v_cmd="${v_cmd}${v_args}"
    fi
    
    lib_exec "${v_cmd}"
}

#-----------------------------------------------------------------------------------------
function sdt_run_command ()
{
    local v_command="$1"
    local v_args="$2"
    
    lib_validate_var_is_set v_command "Illegal argument 'command'."
    # args are optional !

    if [ ! -z "${v_args}" ];
    then
        # note: args starts with space already !
        v_command="${v_command}${v_args}"
    fi
    
    lib_log_info "sdt_run_command ${v_command} ..."
    lib_exec "${v_command}"
}

#-----------------------------------------------------------------------------------------
# parse command line
#
# - parse all parameter THIS script knows to handle
# - but collect all 'unknown' parameter also
# - those 'unknown' parameter has might to be passed to another script then

echo "... parse command line"

ARG_RUN_SCRIPTLET=
ARG_RUN_COMMAND=
ARG_LIST_OF_UNKNOWNS=
ARG_ENABLE_DEBUG=false

while [[ $# > 0 ]]
	do
	key="$1"
	shift

	case $key in

	    -rs|--run-scriptlet)
	    ARG_RUN_SCRIPTLET="$1"
	    shift
	    ;;

        -rc|--run-command)
        ARG_RUN_COMMAND="$1"
        shift
        ;;

        -d|--debug)
        ARG_ENABLE_DEBUG=true
        ;;

	    -h|--help)
	    sdt_show_help
	    ;;

	    *)
	    ARG_LIST_OF_UNKNOWNS="${ARG_LIST_OF_UNKNOWNS} $1"
	    ;;

	esac
done

#-----------------------------------------------------------------------------------------

echo "... init logging"

if [ "${ARG_ENABLE_DEBUG}" == "true" ];
then
    echo "... set log level to DEBUG"
    lib_log_set_level $LIB_LOG_LEVEL_DEBUG
else
    echo "... set log level to INFO"
    lib_log_set_level $LIB_LOG_LEVEL_INFO
fi

#-----------------------------------------------------------------------------------------
# MAIN

echo "... do main"

if [ ! -z "${ARG_RUN_SCRIPTLET}" ];
then
    sdt_run_scriptlet "${ARG_RUN_SCRIPTLET}" "${ARG_LIST_OF_UNKNOWNS}"
fi

if [ ! -z "${ARG_RUN_COMMAND}" ];
then
    sdt_run_command "${ARG_RUN_COMMAND}" "${ARG_LIST_OF_UNKNOWNS}"
fi

exit 0

