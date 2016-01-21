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
LIB_LOG_LEVEL_ERROR="0"
LIB_LOG_LEVEL_WARN="1"
LIB_LOG_LEVEL_INFO="2"
LIB_LOG_LEVEL_DEBUG="3"
LIB_LOG_LEVEL_TRACE="4"

#-----------------------------------------------------------------------------------------
LIB_LOG_COLOR_RED="31"
LIB_LOG_COLOR_YELLOW="33"
LIB_LOG_COLOR_WHITE="0"
LIB_LOG_COLOR_BLUE="34"

#-----------------------------------------------------------------------------------------
function lib_log_set_level()
{
	lib_log_m_level="$1"
}

#-----------------------------------------------------------------------------------------
function lib_log_set_colorize()
{
	local v_state="$1"

	lib_log_m_colorize=${v_state}
}

#-----------------------------------------------------------------------------------------
function lib_log_trace()
{
	local v_msg="$1"

	lib_log_is_level_on LIB_LOG_LEVEL_TRACE r_result
	if [[ $r_result = true ]];
	then
		lib_log_to_std $LIB_LOG_COLOR_BLUE "$v_msg"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_debug()
{
	local v_msg="$1"

	lib_log_is_level_on LIB_LOG_LEVEL_DEBUG r_result
	if [[ $r_result = true ]];
	then
		lib_log_to_std $LIB_LOG_COLOR_BLUE "$v_msg"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_info()
{
	local v_msg="$1"

	lib_log_is_level_on LIB_LOG_LEVEL_INFO r_result
	if [[ $r_result = true ]];
	then
		lib_log_to_std $LIB_LOG_COLOR_WHITE "$v_msg"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_warn()
{
	local v_msg="$1"

	lib_log_is_level_on LIB_LOG_LEVEL_WARN r_result
	if [[ $r_result = true ]];
	then
		lib_log_to_std $LIB_LOG_COLOR_YELLOW "$v_msg"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_error()
{
	local v_msg="$1"

	lib_log_is_level_on LIB_LOG_LEVEL_ERROR r_result
	if [[ $r_result = true ]];
	then
		lib_log_to_std $LIB_LOG_COLOR_RED "$v_msg"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_is_level_on()
{
	local v_level="$1"
	local v_retvar="$2"
	
	if [[ $v_level -le $lib_log_m_level ]];
	then
		eval $v_retvar=true
	else
		eval $v_retvar=false
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_to_std()
{
	local v_color="$1"
	local v_msg="$2"

	if [ $lib_log_m_colorize == true ];
	then
		echo -e "\e[${v_color}m${v_msg}\e[0m"
	else
		echo "${v_msg}"
	fi
}

#-----------------------------------------------------------------------------------------
lib_log_set_level $LIB_LOG_LEVEL_INFO
lib_log_set_colorize false
