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
function lib_dirutils_ensure_dir ()
{
    local v_dir="$1"
    
    lib_validate_var_is_set "v_dir" "Invalid argument 'dir'."
    
    if [ ! -d "${v_dir}" ];
    then
        lib_log_info "... dir '${v_dir}' do not exists - will be created new"
        mkdir -p "${v_dir}"
    fi

    if [ ! -d "${v_dir}" ];
    then
        lib_log_error "... could not create dir '${v_dir}'"
        exit 1
    fi
}
