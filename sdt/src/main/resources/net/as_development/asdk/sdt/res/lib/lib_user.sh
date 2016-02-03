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
function lib_user_create ()
{
    local v_all_args="$@"

    lib_os_get_platform v_os
    local v_done=false

    if [ ${v_done} == false ] && [ ${v_os} == $LIB_OS_PLATFORM_DEBIAN ];
    then
        # no quotes around all args !
        lib_user_create_debian ${v_all_args}
        v_done=true
    fi

    # if [ ${v_done} == false ] && [ ${v_os} == $LIB_OS_PLATFORM_RHEL];
    # then
    #     ...
    #     v_done=true
    # fi
    #
    # if [ ${v_done} == false ] && [ ${v_os} == $LIB_OS_PLATFORM_SLES];
    # then
    #     ...
    #     v_done=true
    # fi

    if [ ${v_done} == false ];
    then
        lib_log_error "lib_user_create : Support for the current OS not implemented yet."
        exit 1
    fi
}

#-----------------------------------------------------------------------------------------
function lib_user_create_debian ()
{
    local v_user="$1"
    local v_group="$2"
    local v_password="$3"
    local v_uid="$4"
    local v_gid="$5"
    
    lib_validate_var_is_set v_user     "Invalid argument 'user'."
    lib_validate_var_is_set v_group    "Invalid argument 'group'."
    lib_validate_var_is_set v_password "Invalid argument 'password'."
    lib_validate_var_is_set v_uid      "Invalid argument 'uid'."
    lib_validate_var_is_set v_gid      "Invalid argument 'gid'."
    
    lib_user_create_group_debian         "${v_group}" "${v_gid}"
    lib_user_create_user_in_group_debian "${v_user}"  "${v_group}" "${v_password}" "${v_uid}"
}

#-----------------------------------------------------------------------------------------
function lib_user_create_group_debian ()
{
    local v_group="$1"
    local v_gid="$2"
    
    lib_validate_var_is_set v_group "Invalid argument 'group'."
    lib_validate_var_is_set v_gid   "Invalid argument 'gid'."
    
    lib_log_debug "group : ${v_group}"
    lib_log_debug "gid   : ${v_gid}"

    lib_log_info "... look if group '${v_group}' exists already"
    local v_group_check=$(grep -c "^${v_group}:" /etc/group)

    lib_log_debug "... result of check = [${v_group_check}]"
    if [ "${v_group_check}" == "0" ];
    then
        lib_log_info "... group '${v_group}' do not exists - will be created new"
        lib_exec "addgroup --quiet --gid ${v_gid} ${v_group}"
    else
        lib_log_info "... group '${v_group}' already exists - check gid"
    fi
}

#-----------------------------------------------------------------------------------------
function lib_user_create_user_in_group_debian ()
{
    local v_user="$1"
    local v_group="$2"
    local v_password="$3"
    local v_uid="$4"
    
    lib_validate_var_is_set v_user     "Invalid argument 'user'."
    lib_validate_var_is_set v_group    "Invalid argument 'group'."
    lib_validate_var_is_set v_password "Invalid argument 'password'."
    lib_validate_var_is_set v_uid      "Invalid argument 'uid'."
    
    lib_log_debug "user     : ${v_user}"
    lib_log_debug "group    : ${v_group}"
    lib_log_debug "password : ${v_password}"
    lib_log_debug "uid      : ${v_uid}"

    lib_log_info "... look if user '${v_user}' exists already"
    local v_user_check=$(grep -c "^${v_user}:" /etc/passwd)

    lib_log_debug "... result of check = [${v_user_check}]"
    if [ "${v_user_check}" == "0" ];
    then
        lib_log_info "... user '${v_user}' do not exists - will be created new"
        lib_exec "adduser --quiet --no-create-home --disabled-login --gecos \"\" --uid ${v_uid} --gid ${v_gid} ${v_user}"
    else
        lib_log_info "... user '${v_user}' already exists - check uid"
    fi

    lib_log_info "... (re)define password for user '${v_user}'"
    lib_exec "echo -e \"${v_password}\n${v_password}\" | passwd ${v_user}"
}