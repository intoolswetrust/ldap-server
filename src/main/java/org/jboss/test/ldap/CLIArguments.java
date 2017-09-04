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

    @Parameter(names = { "--allow-anonymous", "-a" }, description = "allows anonymous bind to the server")
    private boolean allowAnonymous;

    @Parameter(names = { "--port",
            "-p" }, description = "takes [portNumber] as a parameter and binds the LDAP server on that port")
    private int port = DEFAULT_PORT;

    @Parameter(names = { "--bind",
            "-b" }, description = "takes [bindAddress] as a parameter and binds the LDAP server on the address")
    private String bindAddress = DEFAULT_ADDR;

    @Parameter(names = { "--ssl-port",
            "-sp" }, description = "adds SSL transport layer (i.e. 'ldaps' protocol). It takes [portNumber] as a parameter and binds the LDAPs server on the port")
    private Integer sslPort = null;

    @Parameter(names = { "--ssl-need-client-auth", "-snc" }, description = "enables SSL 'needClientAuth' flag")
    private boolean sslNeedClientAuth;

    @Parameter(names = { "--ssl-want-client-auth", "-swc" }, description = "enables SSL 'wantClientAuth' flag")
    private boolean sslWantClientAuth;

    @Parameter(names = { "--ssl-enabled-protocol",
            "-sep" }, description = "takes [sslProtocolName] as argument and enables it for 'ldaps'. Can be used multiple times."
                    + " If the argument is not provided following are used: TLSv1, TLSv1.1, TLSv1.2")
    private List<String> sslEnabledProtocols;

    @Parameter(names = { "--ssl-enabled-ciphersuite", "-scs" }, description = "takes [sslCipherSuite] as argument and enables it for 'ldaps'. Can be used multiple times.")
    private List<String> sslCipherSuite;

    @Parameter(names = { "--ssl-keystore-file", "-skf" }, description = "takes keystore [filePath] as argument. The keystore should contain privateKey to be used by LDAPs")
    private String sslKeystoreFile;

    @Parameter(names = { "--ssl-keystore-password", "-skp" }, description = "takes keystore [password] as argument")
    private String sslKeystorePassword;

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

    public boolean isAllowAnonymous() {
        return allowAnonymous;
    }

    public Integer getSslPort() {
        return sslPort;
    }

    public boolean isSslNeedClientAuth() {
        return sslNeedClientAuth;
    }

    public boolean isSslWantClientAuth() {
        return sslWantClientAuth;
    }

    public List<String> getSslEnabledProtocols() {
        return sslEnabledProtocols;
    }

    public List<String> getSslCipherSuite() {
        return sslCipherSuite;
    }

    public String getSslKeystoreFile() {
        return sslKeystoreFile;
    }

    public String getSslKeystorePassword() {
        return sslKeystorePassword;
    }
}
