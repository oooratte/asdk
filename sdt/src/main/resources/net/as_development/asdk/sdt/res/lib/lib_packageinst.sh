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

    lib_packageinst_selfupdate

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
