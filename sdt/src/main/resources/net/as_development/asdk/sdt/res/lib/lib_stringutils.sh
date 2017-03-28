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
# check if given string is undefined/null or even empty
#
# @param 1 : 'string' [IN]
#        the string to be checked here
#
# @param 2 : 'retvar' [OUT]
#        true if string is empty; false otherwise
#

function lib_stringutils_is_empty ()
{
    local v_string="$1"
    local r_retvar="$2"

    # we cant validate for parameter v_string ... as it can be null or empty ;-)
    lib_validate_var_is_set "r_retvar" "Illegal argument 'retvar'."

    if [ -z "${v_string// }" ];
    then
        eval $r_retvar=true
    else
        eval $r_retvar=false
    fi
}

#-----------------------------------------------------------------------------------------
# replace all occurrence of a variable inside given string
#
# A variable inside the string can be defined using the following schema:
# %[[VAR]]%
#
# This schema was define to be different to the normal ${} schema of bash
# which leads to many many workarounds to suppress default mechanism around
# those default schema ,-)
#
# @param 1 : 'string' [IN]
#        the string containing variables to be replaced by this method
#
# @param 2 : 'var' [IN]
#        the variable name to be replaced
#
# @param 3 : 'value' [IN]
#        the new value as replacement for the defined variable
#
# @param 4 : 'retvar' [OUT]
#        the new string where all found variables was replaced
#

function lib_stringutils_replacevar ()
{
    local v_string="$1"
    local v_var="$2"
    local v_value="$3"
    local r_retvar="$4"

    lib_validate_var_is_set "v_string" "Illegal argument 'string'."
    lib_validate_var_is_set "v_var"    "Illegal argument 'var'."
    lib_validate_var_is_set "v_value"  "Illegal argument 'value'."
    lib_validate_var_is_set "r_retvar" "Illegal argument 'retvar'."

    local v_result=$(echo -n "${v_string}" | sed -e "s,\%\[\[$v_var\]\]\%,${v_value},")
    eval "${r_retvar}=\"${v_result}\""
}

#-----------------------------------------------------------------------------------------
# replace all occurrence of a single character inside given string
#
# @param 1 : 'string' [IN]
#        the string where those characters should be replaced
#
# @param 2 : 'sarch' [IN]
#        the character to be replaced
#
# @param 3 : 'replace' [IN]
#        the new character as replacement
#
# @param 4 : 'retvar' [OUT]
#        the new string where all found variables was replaced
#

function lib_stringutils_replace_char ()
{
    local v_string="$1"
    local v_search="$2"
    local v_replace="$3"
    local r_retvar="$4"

    lib_validate_var_is_set "v_string"  "Illegal argument 'string'."
    lib_validate_var_is_set "v_search"  "Illegal argument 'search'."
    lib_validate_var_is_set "v_replace" "Illegal argument 'replace'."
    lib_validate_var_is_set "r_retvar"  "Illegal argument 'retvar'."

    local v_result=$(echo -n "${v_string}" | sed -n 1'p' | tr "${v_search}" "${v_replace}")
    eval "${r_retvar}=\"${v_result}\""
}

#-----------------------------------------------------------------------------------------
# check if given string ends with defined char sequence
#
# @param 1 : 'string' [IN]
#        the string where the end should be checked
#
# @param 2 : 'ends-with' [IN]
#        the character to be searched
#
# @param 3 : 'retvar' [OUT]
#        true if string ends with given character sequence; false otherwise
#

function lib_stringutils_ends_with ()
{
    local v_string="$1"
    local v_ends_with="$2"
    local r_retvar="$3"

    lib_validate_var_is_set "v_string"    "Illegal argument 'string'."
    lib_validate_var_is_set "v_ends_with" "Illegal argument 'ends-with'."
    lib_validate_var_is_set "r_retvar"    "Illegal argument 'retvar'."

    if [[ "${v_string}" == *${v_ends_with} ]];
    then
        eval $r_retvar=true
    else
        eval $r_retvar=false
    fi
}
