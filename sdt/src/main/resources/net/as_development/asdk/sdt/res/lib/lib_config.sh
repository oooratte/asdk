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
#set -x

#-----------------------------------------------------------------------------------------
function lib_config_replace_var_in_file()
{
	local v_file="$1"
	local v_key="$2"
	local v_value="$3"

	test -z "$v_file"  && lib_log_error "Illegal argument 'file'."  && exit 1
	test -z "$v_key"   && lib_log_error "Illegal argument 'key'."   && exit 1
	test -z "$v_value" && lib_log_error "Illegal argument 'value'." && exit 1
	
 	local v_escaped_value=$(echo $v_value | sed 's,/,\/,g')

	lib_log_info "... patch config var : '$v_key' = '$v_escaped_value'"
	sed -i -e "s,\%\[\[$v_key\]\]\%,$v_escaped_value," $v_file
}

#-----------------------------------------------------------------------------------------
function lib_config_contains_prop()
{
	local v_file="$1"
	local v_key="$2"
	local r_retvar="$3"
	
	test -z "$v_file"   && lib_log_error "Illegal argument 'file'."  && exit 1
	test -z "$v_key"    && lib_log_error "Illegal argument 'key'."   && exit 1
	test -z "$r_retvar" && lib_log_error "No return var given."      && exit 1

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
	
	test -z "$v_file"  && lib_log_error "Illegal argument 'file'."  && exit 1
	test -z "$v_key"   && lib_log_error "Illegal argument 'key'."   && exit 1
	test -z "$v_value" && lib_log_error "Illegal argument 'value'." && exit 1
	
 	local v_escaped_value=$(echo $v_value | sed 's,/,\/,g')

	lib_config_contains_prop $v_file $v_key r_result
	
	if [[ $r_result = true ]];
	then
		echo "... patch [$v_file] '$v_key' = '$v_value'"
		sed -i -e 's:^[ \t]*'$v_key'[ \t]*=\([ \t]*.*\)$:'$v_key'='$v_value':' "$v_file"
	else
		echo "... new   [$v_file] '$v_key'='$v_value'"
		echo "$v_key=$v_value" >> "$v_file"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_config_read_props()
{
	local v_propsfile="$1"
	local v_retvar="$2"

	#                strip empty lines           | strip comments
    local v_result=$(sed '/^\s*$/d' $v_propsfile | sed '/^#/ d'  )

	eval "$v_retvar=\"$v_result\""
}

#-----------------------------------------------------------------------------------------
function lib_config_read_props_with_prefix()
{
	local v_propsfile="$1"
	local v_prefix="$2"
	local v_retvar="$3"

	#                strip empty lines        | strip comments | strip sections | trim spaces around =                  | add prefix to each line
    local v_result=$(sed '/^$/d' $v_propsfile | sed '/^#/ d'   | sed '/^\[/d'   | sed 's/[[:space:]]*=[[:space:]]*/=/g' | sed "s/^/$v_prefix/"   )

	eval "$v_retvar=\"$v_result\""
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

	lib_config_read_props_with_prefix $v_propsfile $v_prefix r_result
	local v_props=$r_result
	
	for v_prop in $v_props;
	do
		local v_key=$(echo $v_prop | cut -f1 -d=)
		local v_val=$(echo $v_prop | cut -f2 -d=)

		lib_log_info "... inject config prop : '$v_key' = '$v_val'"
		eval "$v_key=$v_val"
	done
}
