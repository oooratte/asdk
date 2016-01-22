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
# validate if given variable is set - and exit with error code if not
# (printing the given error message)
#
# @param    var [IN]
#           the name of the variable to be checked
#
# @param    msg [IN]
#           the error message shown in case variable is not set
#

function lib_validate_var_is_set ()
{
    local v_var="$1"
    local v_msg="$2"

    local v_check_value=$(eval echo \$$v_var)
    local v_value_length=${#v_check_value}

#    echo "DBG : var    = '${v_var}'"
#    echo "DBG : value  = '${v_check_value}'"
#    echo "DBG : length = '${v_value_length}'"

    if [ -z "$v_check_value" ];
    then
        lib_log_error "${v_msg}"
        exit 1
    fi
}
