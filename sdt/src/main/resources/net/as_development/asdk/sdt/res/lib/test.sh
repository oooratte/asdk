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

. ./lib_log.sh
. ./lib_validate.sh
. ./lib_fileutils.sh
. ./lib_apt.sh
. ./lib_config.sh

cp /tmp/foo.xml /tmp/foo.xml1
v_persistence_impl="\<\!\-\- no persistence \-\-\>"
lib_config_replace_var_in_file "/tmp/foo.xml1" "AMQ_PERSISTENCE_DEFINITION" "${v_persistence_impl}"
cat /tmp/foo.xml1
