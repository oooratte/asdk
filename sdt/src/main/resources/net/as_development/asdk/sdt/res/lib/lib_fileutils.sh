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

#-----------------------------------------------------------------------------------------
function lib_fileutils_append_text_to_file_if_not_exists ()
{
    local v_file="$1"
    local v_text="$2"
    
    lib_validate_var_is_set "v_file" "Invalid argument 'file'."
    lib_validate_var_is_set "v_text" "Invalid argument 'text'."

    lib_fileutils_file_contains_string "${v_file}" "${v_text}" "v_exists"
    if [ "${v_exists}" == "true" ];
    then
        lib_log_debug "text [${v_text}] already defined in file [${v_file}] ..."
    else
        lib_log_debug "text [${v_text}] not defined in file [${v_file}] - will be defined now ..."
        lib_fileutils_append_text_to_file "${v_file}" "${v_text}"
    fi
}
