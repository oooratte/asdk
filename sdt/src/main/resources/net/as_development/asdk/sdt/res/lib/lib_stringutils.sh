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
