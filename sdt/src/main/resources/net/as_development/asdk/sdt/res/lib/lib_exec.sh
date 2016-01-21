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
function lib_exec_set_simulate ()
{
    local l_state="$1"

	test -z "$l_state" && lib_log_error "Invalid argument 'state'." && exit 1

    lib_exec_m_simulate=${l_state}
}

#-----------------------------------------------------------------------------------------
function lib_exec ()
{
    local l_cmd="$1"

	test -z "$l_cmd" && lib_log_error "Invalid argument 'cmd'." && exit 1

    lib_log_trace "lib_exec : '${l_cmd}'"

    if [ ${lib_exec_m_simulate} == true ];
    then
        lib_log_info "SIMULATE : '${l_cmd}'"
    else
        eval "${l_cmd}"
    fi
}

#-----------------------------------------------------------------------------------------
lib_exec_set_simulate false
