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
DEBUG_FOPT_INIT_DISABLED=init-disabled.opt

#-----------------------------------------------------------------------------------------
function l_debug_break_init_if_disabled ()
{
	local v_init_script="$1"

	test -z $v_init_script && l_log_error "Invalid argument 'init_script'." && exit 1

	if [ -f $SPDT_SHARED_DEBUG_DIR/$DEBUG_FOPT_INIT_DISABLED ];
	then
		l_log_debug "... intialization of '$v_init_script' disabled."
		exit 0
	fi
}

#-----------------------------------------------------------------------------------------
function l_debug_enable_init_in_vm ()
{
	local v_debug_path="$1"

	test -z $v_debug_path && l_log_error "Invalid argument 'debug_path'." && exit 1

	rm -f $v_debug_path/$DEBUG_FOPT_INIT_DISABLED
}

#-----------------------------------------------------------------------------------------
function l_debug_disable_init_in_vm ()
{
	local v_debug_path="$1"

	test -z $v_debug_path && l_log_error "Invalid argument 'debug_path'." && exit 1

	mkdir -p $v_debug_path
	touch $v_debug_path/$DEBUG_FOPT_INIT_DISABLED
}
