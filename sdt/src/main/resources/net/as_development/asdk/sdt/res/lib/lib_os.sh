#!/bin/bash
#
# This is free and unencumbered software released into the public domain.
#
# Anyone is free to copy, modify, publish, use, compile, sell, or
# distribute this software, either in source code form or as a compiled
# binary, for any purpose, commercial or non-commercial, and by any
# means.
#
# In jurisdictions that recognize copyright laws, the author or authors
# of this software dedicate any and all copyright interest in the
# software to the public domain. We make this dedication for the benefit
# of the public at large and to the detriment of our heirs and
# successors. We intend this dedication to be an overt act of
# relinquishment in perpetuity of all present and future rights to this
# software under copyright law.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
# IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
# OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
# ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
# OTHER DEALINGS IN THE SOFTWARE.
#
# For more information, please refer to <http://unlicense.org/>
#


set -e

#-----------------------------------------------------------------------------------------
LIB_OS_PLATFORM_DEBIAN="debian"
LIB_OS_PLATFORM_RHEL="rhel"
LIB_OS_PLATFORM_SLES="sles"

LIB_OS_VERSION_DEBIAN_WHEEZY="debian_wheezy"
LIB_OS_VERSION_DEBIAN_JESSIE="debian_jessie"

LIB_OS_PLATFORM_RHEL_7="rhel_7"
LIB_OS_PLATFORM_RHEL_8="rhel_8"

LIB_OS_PLATFORM_SLES_11="sles_11"
LIB_OS_PLATFORM_SLES_12="sles_12"

#-----------------------------------------------------------------------------------------
function lib_os_is_platform ()
{
    local v_platform="$1"
    local r_retvar="$1"

	lib_validate_var_is_set "v_platform" "Invalid argument 'platform'."
    lib_validate_var_is_set "r_retvar"   "No return var given."

    lib_os_get_platform v_detected_platform

    if [ "${v_detected_platform}" == "${v_platform}" ];
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

	lib_validate_var_is_set "r_retvar" "No return var given."

	if [ -z $lib_os_m_platform ];
	then
		local v_done=false
		local v_info=$(lsb_release -a | tr '\n' ' ')

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
			lib_log_error "Could not determine OS platform."
			exit 1
		fi
	fi

    lib_log_debug "os platform = $lib_os_m_platform"
	eval "${r_retvar}=\"${lib_os_m_platform}\""
}

#-----------------------------------------------------------------------------------------
function lib_os_get_version ()
{
    local r_retvar="$1"

    lib_validate_var_is_set "r_retvar" "No return var given."

    if [ -z $lib_os_m_version ];
    then
        local v_done=false
        local v_info=$(lsb_release -a | tr '\n' ' ')

        lib_log_trace "... os info : '${v_info}'"

        if [ ${v_done} == false ] && [[ "${v_info}" == *"wheezy"* ]];
        then
            lib_log_trace "... found debian 7"
            lib_os_m_version=$LIB_OS_VERSION_DEBIAN_WHEEZY
            v_done=true
        fi

        if [ ${v_done} == false ] && [[ "${v_info}" == *"jessie"* ]];
        then
            lib_log_trace "... found debian 8"
            lib_os_m_version=$LIB_OS_VERSION_DEBIAN_JESSIE
            v_done=true
        fi

        if [ ${v_done} == false ];
        then
            lib_log_error "Could not determine OS version."
            exit 1
        fi
    fi

    lib_log_debug "os version = $lib_os_m_version"
    eval "${r_retvar}=\"${lib_os_m_version}\""
}

#-----------------------------------------------------------------------------------------
lib_os_m_platform=
lib_os_m_version=
