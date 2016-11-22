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
# validate if given variable is set - and exit with error code if not
# (printing the given error message)
#
# Note : As a special feature the value of the might existing variable is checked
#        against the string 'null'.
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

    if [ "$v_check_value" == "null" ];
    then
        lib_log_error "${v_msg}"
        exit 1
    fi
}
