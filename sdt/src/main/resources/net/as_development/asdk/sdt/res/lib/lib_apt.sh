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
# const values

SDT_PKG_REGISTRY=/etc/apt/sources.list.d/sdt.list

#-----------------------------------------------------------------------------------------
function lib_apt_selfupdate ()
{
	lib_exec "apt-get update"
}

#-----------------------------------------------------------------------------------------
function lib_apt_clean_cache ()
{
    lib_exec "apt-get clean"
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
    local v_args="$2"
    
    lib_validate_var_is_set "v_package" "Invalid argument 'package'."
    # v_args are optional !

    lib_exec "apt-get install -y --force-yes ${v_package} ${v_args}"
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

    local v_pkg_registry_file="${SDT_PKG_REGISTRY}"
    local v_pkg_entry="deb ${v_package_repo} /"

    lib_fileutils_append_text_to_file_if_not_exists "${v_pkg_registry_file}" "${v_pkg_entry}"
}

#-----------------------------------------------------------------------------------------
function lib_apt_remove_package_repo ()
{
    local v_package_repo="$1"
    
    lib_validate_var_is_set "v_package_repo" "Invalid argument 'package_repo'."

    local v_pkg_registry_file="${SDT_PKG_REGISTRY}"
    local v_pkg_entry="deb ${v_package_repo} /"

    lib_fileutils_remove_text_from_file_if_exists "${v_pkg_registry_file}" "${v_pkg_entry}"
}

#-----------------------------------------------------------------------------------------
function lib_apt_update_packages_by_name ()
{
    local v_pkg_search="$1"
    local v_args="$2"

    lib_validate_var_is_set "v_pkg_search" "Invalid argument 'package_search'."
    # v_args are optional !

    local v_pkg_list=$(dpkg --get-selections | grep -i "${v_pkg_search}" | sed 's:install$::')
    local v_pkg=

    for v_pkg in ${v_pkg_list};
    do
        lib_exec "apt-get install --reinstall -y --force-yes --fix-missing \"${v_pkg}\" ${v_args}"
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
        lib_exec "apt-get purge -y --fix-missing --fix-broken \"${v_pkg}\""
    done

    lib_exec "apt-get autoremove -y --fix-missing --fix-broken"
}

