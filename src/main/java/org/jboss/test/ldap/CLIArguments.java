/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.jboss.test.ldap;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

/**
 * Command line arguments for ldap-server.
 */
public class CLIArguments {

    public static final String DEFAULT_ADDR = "0.0.0.0";
    public static final int DEFAULT_PORT = 10389;

    @Parameter(description = "[LDIFs to import]")
    private final List<String> ldifFiles = new ArrayList<String>();

    @Parameter(names = { "--help", "-h" }, description = "shows this help and exits", help = true)
    private boolean help;

    @Parameter(names = { "--port",
            "-p" }, description = "takes [portNumber] as a parameter and binds the LDAP server on that port")
    private int port = DEFAULT_PORT;

    @Parameter(names = { "--bind",
            "-b" }, description = "takes [bindAddress] as a parameter and binds the LDAP server on the address")
    private String bindAddress = DEFAULT_ADDR;

    public List<String> getLdifFiles() {
        return ldifFiles;
    }

    public boolean isHelp() {
        return help;
    }

    public int getPort() {
        return port;
    }

    public String getBindAddress() {
        return bindAddress;
    }

}
