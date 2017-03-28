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
function lib_dirutils_dir_exists ()
{
    local v_dir="$1"
    local r_retvar="$2"

    lib_validate_var_is_set "v_dir"    "Invalid argument 'dir'."
    lib_validate_var_is_set "r_retvar" "No return var given."

    if [ -d "${v_dir}" ];
    then
        eval $r_retvar=true
    else
        eval $r_retvar=false
    fi
}

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
