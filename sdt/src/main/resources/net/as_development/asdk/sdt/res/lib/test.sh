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

SDT_SCRIPT_ROOT=$WORKSPACE_HOME/asdk/sdt/src/main/resources/net/as_development/asdk/sdt/res
SDT_LIB_DIR=$SDT_SCRIPT_ROOT/lib

. $SDT_LIB_DIR/lib.sh

set -e

lib_exec_set_simulate true

OX_SCRIPTFUNC_SH="/tmp/foo.sh"

#-----------------------------------------------------------------------------------------
function f_refactor_commandline ()
{
    lib_log_info "... refactor command line"

    lib_config_contains_prop "${OX_SCRIPTFUNC_SH}" "JAVA_XTRAOPTS_MEM" v_exists

    if [ "${v_exists}" == "true" ];
    then
        lib_log_info "... already refactored. Nothing todo."
        return
    fi

    local v_cmdline=""
    local v_memopts=""
    local v_dbgopts=""

    v_memopts="-Xms1024m"
    v_memopts="${v_memopts} -Xmx4096m"
    v_memopts="${v_memopts} -Xss512k"
    v_memopts="${v_memopts} -XX:MaxPermSize=512m"

    v_dbgopts="-agentlib:jdwp=transport=dt_socket,server=y,address=8102,suspend=n"

    v_cmdline="-Dsun.net.inetaddr.ttl=3600"
    v_cmdline="${v_cmdline} -Dnetworkaddress.cache.ttl=3600"
    v_cmdline="${v_cmdline} -Dnetworkaddress.cache.negative.ttl=10"
    v_cmdline="${v_cmdline} -Dlogback.threadlocal.put.duplicate=false"
    v_cmdline="${v_cmdline} -server"
    v_cmdline="${v_cmdline} -Djava.awt.headless=true"
    v_cmdline="${v_cmdline} -XX:+UseConcMarkSweepGC"
    v_cmdline="${v_cmdline} -XX:+UseParNewGC"
    v_cmdline="${v_cmdline} -XX:CMSInitiatingOccupancyFraction=75"
    v_cmdline="${v_cmdline} -XX:+UseCMSInitiatingOccupancyOnly"
    v_cmdline="${v_cmdline} -XX:NewRatio=3"
    v_cmdline="${v_cmdline} -XX:+UseTLAB"
    v_cmdline="${v_cmdline} -XX:+DisableExplicitGC"
    v_cmdline="${v_cmdline} -Dosgi.compatibility.bootdelegation=false"
    v_cmdline="${v_cmdline} -XX:-OmitStackTraceInFastThrow"
    v_cmdline="${v_cmdline} \${JAVA_XTRAOPTS_MEM}"
    v_cmdline="${v_cmdline} \${JAVA_XTRAOPTS_DBG}"

    local v_quote_value=true
    local v_escape_value=false

    lib_config_remove_prop "${OX_SCRIPTFUNC_SH}" "JAVA_XTRAOPTS"

    lib_config_save_prop "${OX_SCRIPTFUNC_SH}" "JAVA_XTRAOPTS_MEM" "${v_memopts}" "${v_quote_value}" "${v_escape_value}"
    lib_config_save_prop "${OX_SCRIPTFUNC_SH}" "JAVA_XTRAOPTS_DBG" "${v_dbgopts}" "${v_quote_value}" "${v_escape_value}"
    lib_config_save_prop "${OX_SCRIPTFUNC_SH}" "JAVA_XTRAOPTS"     "${v_cmdline}" "${v_quote_value}" "${v_escape_value}"

    lib_log_info "ok."
}

#-----------------------------------------------------------------------------------------
function f_configure_memory ()
{
    local v_ms="$1"
    local v_mx="$2"
    local v_mp="$3"
    local v_ss="$4"
    
    lib_validate_var_is_set "v_ms" "Illegal argument 'ms'."
    lib_validate_var_is_set "v_mx" "Illegal argument 'mx'."
    lib_validate_var_is_set "v_mp" "Illegal argument 'mp'."
    lib_validate_var_is_set "v_ss" "Illegal argument 'ss'."

    lib_log_info "... configure memory settings : min=${v_ms} max=${v_mx} permgen=${v_mp} stack=${v_ss}"

    local v_memopts=""

    v_memopts="-Xms${v_ms}"
    v_memopts="${v_memopts} -Xmx${v_mx}"
    v_memopts="${v_memopts} -Xss${v_ss}"
    v_memopts="${v_memopts} -XX:MaxPermSize=${v_mp}"

    local v_quote_value=true
    local v_escape_value=false

    lib_config_save_prop "${OX_SCRIPTFUNC_SH}" "JAVA_XTRAOPTS_MEM" "${v_memopts}" "${v_quote_value}" "${v_escape_value}"

    lib_log_info "ok."
}

#-----------------------------------------------------------------------------------------
function f_configure_remote_debugging ()
{
    local v_port="$1"
    
    lib_validate_var_is_set "v_port" "Illegal argument 'port'."

    lib_log_info "... configure remote debugging : port=${v_port}"

    local v_dbgopts=""
    v_dbgopts="-agentlib:jdwp=transport=dt_socket,server=y,address=${v_port},suspend=n"

    local v_quote_value=true
    local v_escape_value=false

    lib_config_save_prop "${OX_SCRIPTFUNC_SH}" "JAVA_XTRAOPTS_DBG" "${v_dbgopts}" "${v_quote_value}" "${v_escape_value}"

    lib_log_info "ok."
}

f_refactor_commandline
more "/tmp/foo.sh"
