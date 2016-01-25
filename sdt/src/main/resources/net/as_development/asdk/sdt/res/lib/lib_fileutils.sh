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
function lib_fileutils_file_contains_string ()
{
    local v_file="$1"
    local v_search="$2"
    local r_retvar="$3"
    
    lib_validate_var_is_set "v_file"   "Invalid argument 'file'."
    lib_validate_var_is_set "v_search" "Invalid argument 'search'."
    lib_validate_var_is_set "r_retvar" "Miss return variable."
    
    local v_found=false

    if [ -f "${v_file}" ];
    then
        local v_search_result=$(cat "${v_file}" | grep "${v_search}")
        if [ -z "${v_search_result}" ];
        then
            v_found=false
        else
            v_found=true
        fi
    fi

    eval "${r_retvar}=\"${v_found}\""
}

#-----------------------------------------------------------------------------------------
function lib_fileutils_append_text_to_file ()
{
    local v_file="$1"
    local v_text="$2"
    
    lib_validate_var_is_set "v_file" "Invalid argument 'file'."
    lib_validate_var_is_set "v_text" "Invalid argument 'text'."

    echo "${v_text}" >> "${v_file}"    
}
