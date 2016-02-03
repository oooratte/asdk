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
function lib_apt_selfupdate ()
{
	lib_exec "apt-get update"
}

#-----------------------------------------------------------------------------------------
function lib_apt_install_package ()
{
    local v_package="$1"
    
    lib_validate_var_is_set "v_package" "Invalid argument 'package'."

    lib_exec "apt-get install -y --force-yes ${v_package}"
}

#-----------------------------------------------------------------------------------------
function lib_apt_install_package_with_args ()
{
    local v_package="$1"
    declare -a v_args=("${!2}")
    
    lib_validate_var_is_set "v_package" "Invalid argument 'package'."

    lib_exec "apt-get install -y --force-yes ${v_package} ${v_args[@]}"
}

#-----------------------------------------------------------------------------------------
function lib_apt_configure_proxy ()
{
	local v_ip="$1"
	local v_port="$2"
	
	lib_validate_var_is_set "v_ip"   "Invalid argument 'ip'."
	lib_validate_var_is_set "v_port" "Invalid argument 'port'."

	local v_proxy_conf=/etc/apt/apt.conf.d/01proxy
	rm -f $v_proxy_conf
	touch $v_proxy_conf

	echo "Acquire::http { Proxy \"http://${v_ip}:${v_port}\"; };" >> $v_proxy_conf
	echo "Acquire::https { Proxy \"https://\"; };"                >> $v_proxy_conf
}

#-----------------------------------------------------------------------------------------
function lib_apt_install_proxy_client ()
{
	apt-get install -y squid-deb-proxy-client
}

#-----------------------------------------------------------------------------------------
function lib_apt_add_package_repo ()
{
    local v_package_repo="$1"
    
    lib_validate_var_is_set "v_package_repo" "Invalid argument 'package_repo'."

    local v_pkg_registry_file="/etc/apt/sources.list.d/sdt.list"
    local v_pkg_entry="deb ${v_package_repo} /"

    lib_fileutils_file_contains_string "${v_pkg_registry_file}" "${v_pkg_entry}" "v_exists"
    
    if [ "${v_exists}" == "true" ];
    then
        lib_log_warn "APT Repo '${v_package_repo}' already registered."
    else
        lib_fileutils_append_text_to_file "${v_pkg_registry_file}" "${v_pkg_entry}"
    fi
}

#-----------------------------------------------------------------------------------------
function lib_apt_update_packages_by_name ()
{
    local v_pkg_search="$1"
    declare -a v_args=("${!2}")

    lib_validate_var_is_set "v_pkg_search" "Invalid argument 'package_search'."

    local v_pkg_list=$(dpkg --get-selections | grep -i "${v_pkg_search}" | sed 's:install$::' )
    local v_pkg=

    for v_pkg in ${v_pkg_list};
    do
        lib_exec "apt-get install -y \"${v_pkg}\" ${v_args[@]}"
    done
}

#-----------------------------------------------------------------------------------------
function lib_apt_remove_packages_by_name ()
{
    local v_pkg_search="$1"

    lib_validate_var_is_set "v_pkg_search" "Invalid argument 'package_search'."

    local v_pkg_list=$(dpkg --get-selections | grep -i "${v_pkg_search}" | sed 's:install$::' )
    local v_pkg=

    for v_pkg in ${v_pkg_list};
    do
        lib_exec "apt-get purge -y \"${v_pkg}\""
    done

    lib_exec "apt-get autoremove -y"
}

