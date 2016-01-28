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
function lib_packageinst_selfupdate ()
{
    lib_os_get_platform v_os
    local v_done=false

    if [ ${v_done} == false ] && [ ${v_os} == $LIB_OS_PLATFORM_DEBIAN ];
    then
        lib_apt_selfupdate
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
        lib_log_error "lib_packageinst_selfupdate : Support for the current OS not implemented yet."
        exit 1
    fi
}

#-----------------------------------------------------------------------------------------
function lib_packageinst_install_package ()
{
	local v_pkg="$1"

	test -z $v_pkg && lib_log_error "Invalid argument 'package'." && exit 1

    lib_os_get_platform v_os
    local v_done=false

    if [ ${v_done} == false ] && [ ${v_os} == $LIB_OS_PLATFORM_DEBIAN ];
    then
        lib_apt_install_package "${v_pkg}"
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
        lib_log_error "lib_packageinst_install_package : Support for the current OS not implemented yet."
        exit 1
    fi
}
