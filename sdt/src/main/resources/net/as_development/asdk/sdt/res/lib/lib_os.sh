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
LIB_OS_PLATFORM_DEBIAN="debian"
LIB_OS_PLATFORM_RHEL="rhel"
LIB_OS_PLATFORM_SLES="sles"

#-----------------------------------------------------------------------------------------
function lib_os_is_platform ()
{
    local l_platform="$1"
    local r_retvar="$1"

	test -z "$l_platform" && lib_log_error "Invalid argument 'platform'." && exit 1
    test -z "$r_retvar"   && lib_log_error "No return var given."         && exit 1

    lib_os_get_platform v_detected_platform

    if [ "${v_detected_platform}" == "${l_platform}" ];
    then
        eval $r_retvar=true
    else
        eval $r_retvar=false
    fi
}

#-----------------------------------------------------------------------------------------
function lib_os_get_platform ()
{
	local r_retvar="$1"

	test -z "$r_retvar" && lib_log_error "No return var given." && exit 1

	if [ -z $lib_os_m_platform ];
	then
		local v_done=false
		local v_info=$(uname -a)

# debug-mock ;-)
#        v_info="Linux debian-jessie 3.16.0-4-amd64 #1 SMP Debian 3.16.7-ckt11-1+deb8u3 (2015-08-04) x86_64 GNU/Linux"

		lib_log_trace "... os info : '${v_info}'"

		if [ ${v_done} == false ] && [[ "${v_info}" == *"Debian"* ]];
		then
			lib_log_trace "... found debian"
			lib_os_m_platform=$LIB_OS_PLATFORM_DEBIAN
			v_done=true
		fi

		if [ ${v_done} == false ] && [[ "${v_info}" == *"rhel"* ]];
		then
			lib_log_trace "... found rhel"
			lib_os_m_platform=$LIB_OS_PLATFORM_RHEL
			v_done=true
		fi

		if [ ${v_done} == false ] && [[ "${v_info}" == *"sles"* ]];
		then
			lib_log_trace "... found sles"
			lib_os_m_platform=$LIB_OS_PLATFORM_SLES
			v_done=true
		fi

		if [ ${v_done} == false ];
		then
			lib_log_error "Could not determine OS."
			exit 1
		fi
	fi

    lib_log_debug "os = $lib_os_m_platform"
	eval $r_retvar=$lib_os_m_platform
}
