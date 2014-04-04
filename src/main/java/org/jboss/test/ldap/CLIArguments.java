/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

    @Parameter(names = { "--port", "-p" }, description = "takes [portNumber] as a parameter and binds the LDAP server on that port")
    private int port = DEFAULT_PORT;

    @Parameter(names = { "--bind", "-b" }, description = "takes [bindAddress] as a parameter and binds the LDAP server on the address")
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
