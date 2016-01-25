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
# replace all occurrence of a variable inside the given file
#
# Variables inside those file has to be define by using the following schema:
# %[[VAR]]%
#
# @param 1 : string 'file' [IN]
#           the absolute path and name of the file where those variables should be replaced
#
# @param 2 : string 'var' [IN]
#           the variable name to be replaced
#
# @param 3 : string 'value' [IN]
#           the new replacement value
#

function lib_config_replace_var_in_file()
{
	local v_file="$1"
	local v_var="$2"
	local v_value="$3"

	lib_validate_var_is_set "v_file"  "Illegal argument 'file'."
	lib_validate_var_is_set "v_var"   "Illegal argument 'var'."
	lib_validate_var_is_set "v_value" "Illegal argument 'value'."
	
 	local v_escaped_value=$(echo "${v_value}" | sed 's,/,\/,g')

	lib_log_info "... patch config var : '${v_var}' = '${v_escaped_value}'"
	sed -i -e "s,\%\[\[$v_var\]\]\%,${v_escaped_value}," "${v_file}"
}

#-----------------------------------------------------------------------------------------
function lib_config_contains_prop()
{
	local v_file="$1"
	local v_key="$2"
	local r_retvar="$3"
	
	lib_validate_var_is_set "v_file"   "Illegal argument 'file'."
	lib_validate_var_is_set "v_key"    "Illegal argument 'key'."
	lib_validate_var_is_set "r_retvar" "No return var given."

	local v_regex="^[[:space:]\t]*$v_key[[:space:]\t]*="
		
	local v_found=$(grep "$v_regex" "$v_file")

	if [[ -n $v_found ]];
	then
		eval $r_retvar=true
	else
		eval $r_retvar=false
	fi
}

#-----------------------------------------------------------------------------------------
function lib_config_save_prop()
{
	local v_file="$1"
	local v_key="$2"
	local v_value="$3"
	
	lib_validate_var_is_set "v_file"  "Illegal argument 'file'."
	lib_validate_var_is_set "v_key"   "Illegal argument 'key'."
	lib_validate_var_is_set "v_value" "Illegal argument 'value'."
	
 	local v_escaped_value=${v_value}
 	
 	v_escaped_value=$(echo -e "${v_escaped_value}" | sed 's,/,\/,g')
    v_escaped_value=$(echo -e "${v_escaped_value}" | sed 's,:,\\:,g')

	lib_config_contains_prop $v_file $v_key r_result
	
	if [[ $r_result = true ]];
	then
		echo "... patch [${v_file}] '${v_key}' = '${v_value}' ... escaped '${v_escaped_value}' "
		sed -i -e 's:^[ \t]*'${v_key}'[ \t]*=\([ \t]*.*\)$:'${v_key}'='${v_escaped_value}':' "${v_file}"
	else
		echo "... new   [${v_file}] '${v_key}'='${v_value}' ... escaped '${v_escaped_value}' "
		echo "${v_key}=${v_escaped_value}" >> "${v_file}"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_config_read_props()
{
	local v_propsfile="$1"
	local r_retvar="$2"

	#                strip empty lines           | strip comments
    local v_result=$(sed '/^\s*$/d' $v_propsfile | sed '/^#/ d'  )

	eval "$r_retvar=\"$v_result\""
}

#-----------------------------------------------------------------------------------------
function lib_config_read_props_with_prefix()
{
	local v_propsfile="$1"
	local v_prefix="$2"
	local r_retvar="$3"

	#                strip empty lines        | strip comments | strip sections | trim spaces around =                  | add prefix to each line
    local v_result=$(sed '/^$/d' $v_propsfile | sed '/^#/ d'   | sed '/^\[/d'   | sed 's/[[:space:]]*=[[:space:]]*/=/g' | sed "s/^/$v_prefix/"   )

	eval "$r_retvar=\"$v_result\""
}

#-----------------------------------------------------------------------------------------
# load given properties file and inject all properties from that file by renaming it
# Those variables will be visible to the code within this script later on.
#
# @param 1 [IN]
#		  the file where those properties should be read from
#
# @param 2 [IN]
#		  the prefix which should be added to each property name
#

function lib_config_inject_props_with_prefix()
{
	local v_propsfile="$1"
	local v_prefix="$2"

    lib_validate_var_is_set "v_propsfile" "Miss argument 'properties-file'."
    lib_validate_var_is_set "v_prefix"    "Miss argument 'prefix'."

	lib_config_read_props_with_prefix "${v_propsfile}" "${v_prefix}" "r_result"
	local v_props=${r_result}
	
	for v_prop in ${v_props};
	do
		local v_key=$(echo ${v_prop} | cut -f1 -d=)
		local v_val=$(echo ${v_prop} | cut -f2 -d=)

		lib_log_info "... inject config prop : '${v_key}' = '${v_val}'"
		eval "${v_key}=\"${v_val}\""
	done
}
