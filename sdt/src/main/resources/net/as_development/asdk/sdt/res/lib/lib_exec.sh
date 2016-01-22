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

set -e

#-----------------------------------------------------------------------------------------
# enable/disable simulation mode of command execution
#
# @param boolean 'state' [IN]
#        true enable simulation mode; false disable it
#

function lib_exec_set_simulate ()
{
    local v_state="$1"

    lib_validate_var_is_set "v_state" "Invalid argument 'state'."

    lib_exec_m_simulate=${v_state}
}

#-----------------------------------------------------------------------------------------
# exec a command (or simulate it by printing it out instead of executing them ...)
#
# @param string 'cmd' [IN]
#        the command for execution
#

function lib_exec ()
{
    local v_cmd="$1"

	lib_validate_var_is_set "v_cmd" "Invalid argument 'cmd'."

    lib_log_trace "lib_exec : '${v_cmd}'"

    if [ ${lib_exec_m_simulate} == true ];
    then
        lib_log_info "SIMULATE : '${v_cmd}'"
    else
        eval "${v_cmd}"
    fi
}

#-----------------------------------------------------------------------------------------
lib_exec_set_simulate false
