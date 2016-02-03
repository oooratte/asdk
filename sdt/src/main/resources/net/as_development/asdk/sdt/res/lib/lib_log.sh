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
function lib_log_enable_printf()
{
    local v_state="$1"

    lib_log_m_use_printf=${v_state}
}

#-----------------------------------------------------------------------------------------
function lib_log_trace()
{
	local v_msg="$1"

	lib_log_is_level_on LIB_LOG_LEVEL_TRACE v_result
	if [[ $v_result = true ]];
	then
		lib_log_to_std $LIB_LOG_COLOR_BLUE "$v_msg"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_debug()
{
	local v_msg="$1"

	lib_log_is_level_on LIB_LOG_LEVEL_DEBUG v_result
	if [[ $v_result = true ]];
	then
		lib_log_to_std $LIB_LOG_COLOR_BLUE "$v_msg"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_info()
{
	local v_msg="$1"

	lib_log_is_level_on LIB_LOG_LEVEL_INFO v_result
	if [[ $v_result = true ]];
	then
		lib_log_to_std $LIB_LOG_COLOR_WHITE "$v_msg"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_warn()
{
	local v_msg="$1"

	lib_log_is_level_on LIB_LOG_LEVEL_WARN v_result
	if [[ $v_result = true ]];
	then
		lib_log_to_std $LIB_LOG_COLOR_YELLOW "$v_msg"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_error()
{
	local v_msg="$1"

	lib_log_is_level_on LIB_LOG_LEVEL_ERROR v_result
	if [[ $v_result = true ]];
	then
		lib_log_to_std $LIB_LOG_COLOR_RED "$v_msg"
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_is_level_on()
{
	local v_level="$1"
	local r_retvar="$2"
	
	if [[ $v_level -le $lib_log_m_level ]];
	then
		eval $r_retvar=true
	else
		eval $r_retvar=false
	fi
}

#-----------------------------------------------------------------------------------------
function lib_log_to_std()
{
	local v_color="$1"
	local v_msg="$2"

	if [ $lib_log_m_colorize == true ];
	then
        if [ $lib_log_m_use_printf == true ];
        then
            # mac terminal needs printf instead of echo
            printf "\e[${v_color}m${v_msg}\e[0m\n"
        else
    		echo -e "\e[${v_color}m${v_msg}\e[0m"
        fi
	else
		echo "${v_msg}"
	fi
}

#-----------------------------------------------------------------------------------------
lib_log_set_level $LIB_LOG_LEVEL_INFO
lib_log_set_colorize false
lib_log_enable_printf false

