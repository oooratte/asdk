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
READY_POLL_FREQUENCY=2s
READY_MAX_RETRIES=30
READY_STATE_DIR=$SPDT_STATES_DIR/ready
READY_SHARED_STATES_DIR=$SPDT_SHARED_STATES_DIR/ready
READY_FINAL_NOTIFY_FILE=$READY_SHARED_STATES_DIR/.is-ready

#-----------------------------------------------------------------------------------------
function l_ready_ensure_states_dirs()
{
	mkdir -p $READY_STATE_DIR
	mkdir -p $READY_SHARED_STATES_DIR
}

#-----------------------------------------------------------------------------------------
function l_ready_clean_states()
{
	rm -rf $READY_STATE_DIR/
	rm -f READY_FINAL_NOTIFY_FILE/

	l_ready_ensure_states_dirs
}

#-----------------------------------------------------------------------------------------
function l_ready_name_ready_file_for_service()
{
	local v_service_name="$1"

	test -z $v_service_name && echo "Invalid argument 'v_service_name'"

	l_ready_ensure_states_dirs

	local v_ready_file=$READY_STATE_DIR/.${v_service_name}-is-ready
	echo $v_ready_file
}

#-----------------------------------------------------------------------------------------
# wait until a service is ready
#
# Doing so we pool for a special file at a special place.
# It's up to the service to touch that file at the right moment.
#
# To be sure touching the right file another lib.sh function f_mark_service_as_ready()
# should be called.
#
# @param [1] 'service_name'
# 		 the name of the service.
#
# @exit  in case job will not become ready in time.

function l_ready_wait_for_one_service()
{
	local v_service_name="$1"

	test -z $v_service_name && echo "Invalid argument 'v_service_name'"

	local v_ready_file=$(l_ready_name_ready_file_for_service $v_service_name)
	local v_retry=0

	while true;
	do
		echo "... is service '$v_service_name' ready ? [$v_ready_file]"
		if [ -f $v_ready_file ];
		then
			break
		fi

		sleep $READY_POLL_FREQUENCY

		v_retry=$(($v_retry + 1))
		if [ $v_retry -gt $READY_MAX_RETRIES ];
		then
			echo "... give up. service '$v_service_name' not read in time."
			exit 1
		fi

		echo "... retry $v_retry of $READY_MAX_RETRIES"
	done
}

#-----------------------------------------------------------------------------------------
function l_ready_wait_for_all_services()
{
	local v_service_list=""

	while read v_service;
	do
		v_service_list="$v_service_list $v_service"
	done < $SPDT_LAYOUT_SERVICES_FILE

	for v_service in $v_service_list
	do
		l_ready_wait_for_one_service "$v_service"
	done
}

#-----------------------------------------------------------------------------------------
# mark a service as ready/usable
#
# @param 1 [IN]
# 		 the name of the service.
#

function l_ready_mark_service_as_ready()
{
	local v_service_name="$1"

	test -z $v_service_name && echo "Invalid argument 'v_service_name'"

	local v_ready_file=$(l_ready_name_ready_file_for_service $v_service_name)
	touch $v_ready_file
}

#-----------------------------------------------------------------------------------------
function l_ready_notify()
{
	l_ready_ensure_states_dirs
	touch $READY_FINAL_NOTIFY_FILE
}
