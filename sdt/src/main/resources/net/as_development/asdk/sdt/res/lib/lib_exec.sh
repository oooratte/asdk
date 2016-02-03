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

    lib_log_debug "lib_exec : '${v_cmd}'"

    if [ ${lib_exec_m_simulate} == true ];
    then
        lib_log_info "SIMULATE : '${v_cmd}'"
    else
        eval "${v_cmd}"
    fi
}

#-----------------------------------------------------------------------------------------
lib_exec_set_simulate false
