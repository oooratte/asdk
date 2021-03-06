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
    local v_at_beginning="$3"
    
    lib_validate_var_is_set "v_file" "Invalid argument 'file'."
    lib_validate_var_is_set "v_text" "Invalid argument 'text'."

    if [ "${v_at_beginning}" == "true" ];
    then
        sed -i'' -e "1s:^:${v_text}:" "${v_file}"
    else
        echo "${v_text}" >> "${v_file}"
    fi
}

#-----------------------------------------------------------------------------------------
function lib_fileutils_remove_text_from_file ()
{
    local v_file="$1"
    local v_text="$2"
    
    lib_validate_var_is_set "v_file" "Invalid argument 'file'."
    lib_validate_var_is_set "v_text" "Invalid argument 'text'."

    local v_escaped_text=$(echo "${v_text}" | sed 's,/,\/,g')
    sed -i -e "\|${v_escaped_text}|d" "${v_file}"
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

#-----------------------------------------------------------------------------------------
function lib_fileutils_remove_text_from_file_if_exists ()
{
    local v_file="$1"
    local v_text="$2"
    
    lib_validate_var_is_set "v_file" "Invalid argument 'file'."
    lib_validate_var_is_set "v_text" "Invalid argument 'text'."

    lib_fileutils_file_contains_string "${v_file}" "${v_text}" "v_exists"
    if [ "${v_exists}" == "false" ];
    then
        lib_log_debug "text [${v_text}] not defined in file [${v_file}] ..."
    else
        lib_log_debug "text [${v_text}]  defined in file [${v_file}] - will be removed now ..."
        lib_fileutils_remove_text_from_file "${v_file}" "${v_text}"
    fi
}

#-----------------------------------------------------------------------------------------
function lib_fileutils_file_exists ()
{
    local v_file="$1"
    local r_retvar="$2"

    lib_validate_var_is_set "v_file"   "Invalid argument 'file'."
    lib_validate_var_is_set "r_retvar" "No return var given."

    if [ -f "${v_file}" ];
    then
        eval $r_retvar=true
    else
        eval $r_retvar=false
    fi
}

#-----------------------------------------------------------------------------------------
function lib_fileutils_get_path ()
{
    local v_file="$1"
    local r_retvar="$2"

    lib_validate_var_is_set "v_file"   "Invalid argument 'file'."
    lib_validate_var_is_set "r_retvar" "No return var given."

    local v_path=$(dirname ${v_file})
    eval "${r_retvar}=\"${v_path}\"/"
}

#-----------------------------------------------------------------------------------------
function lib_fileutils_copy_file ()
{
    local v_file="$1"
    local v_target="$2"
    
    lib_validate_var_is_set "v_file"   "Invalid argument 'file'."
    lib_validate_var_is_set "v_target" "Invalid argument 'target'."

    lib_fileutils_file_exists "${v_file}" v_exists
    lib_validate_var_is_true "v_exists" "File '${v_file}' for copy do not exists."

    lib_stringutils_ends_with "${v_target}" "/" v_target_is_dir

    if [ "${v_target_is_dir}" == "true" ];
    then
        cp "${v_file}" "${v_target}/"
    else
        lib_fileutils_get_path "${v_target}" v_dir
        lib_dirutils_ensure_dir "${v_dir}"
        cp "${v_file}" "${v_target}" # target = dir + file name !
    fi
}
