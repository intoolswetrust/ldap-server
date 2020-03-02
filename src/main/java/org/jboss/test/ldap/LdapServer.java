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

import java.util.List;

//import org.apache.commons.io.IOUtils;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.IOUtils;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;

/**
 * Creates and starts LDAP server(s).
 *
 * @author Josef Cacek
 */
public class LdapServer {

    private static final String LDIF_FILENAME_JBOSS_ORG = "jboss-org.ldif";

    private final DirectoryService directoryService;
    private final org.apache.directory.server.ldap.LdapServer ldapServer;

    // Public methods --------------------------------------------------------

    /**
     * Starts an LDAP server.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final CLIArguments cliArguments = new CLIArguments();
        final ExtCommander jCmd = new ExtCommander(cliArguments, args);
        jCmd.setProgramName("java -jar ldap-server.jar");
        jCmd.setUsageHead(
                "The ldap-server is a simple LDAP server implementation based on ApacheDS. It creates one user partition with root 'dc=jboss,dc=org'.");
        jCmd.setUsageTail("Examples:\n\n" //
                + "$ java -jar ldap-server.jar users.ldif\n" //
                + " Starts LDAP server on port 10389 (all interfaces) and imports users.ldif\n\n" //
                + "$ java -jar ldap-server.jar -sp 10636 users.ldif\n" //
                + " Starts LDAP server on port 10389 and LDAPs on port 10636 and imports the LDIF\n\n" //
                + "$ java -jar ldap-server.jar -b 127.0.0.1 -p 389\n" //
                + " Starts LDAP server on address 127.0.0.1:389 and imports default data (one user entry 'uid=jduke,ou=Users,dc=jboss,dc=org'");
        if (cliArguments.isHelp()) {
            jCmd.usage();
            return;
        }
        new LdapServer(cliArguments);
    }

    /**
     * Create a single LDAP server.
     *
     * @param cliArguments
     *
     * @throws Exception
     */
    public LdapServer(CLIArguments cliArguments) throws Exception {
        long startTime = System.currentTimeMillis();

        InMemoryDirectoryServiceFactory dsFactory = new InMemoryDirectoryServiceFactory();
        dsFactory.init("ds");

        directoryService = dsFactory.getDirectoryService();
        System.out.println("Directory service started in " + (System.currentTimeMillis() - startTime) + "ms");
        directoryService.setAllowAnonymousAccess(cliArguments.isAllowAnonymous());
//        directoryService.addLast(new CountLookupInterceptor());
        importLdif(cliArguments.getLdifFiles());

        ldapServer = new org.apache.directory.server.ldap.LdapServer();
        TcpTransport tcp = new TcpTransport(cliArguments.getBindAddress(), cliArguments.getPort());
        if (cliArguments.getSslPort() != null) {
            TcpTransport ldapsTcp = new TcpTransport(cliArguments.getBindAddress(), cliArguments.getSslPort());
            ldapsTcp.setEnableSSL(true);
            ldapsTcp.setEnabledProtocols(cliArguments.getSslEnabledProtocols());
            ldapsTcp.setEnabledCiphers(cliArguments.getSslCipherSuite());
            ldapsTcp.setNeedClientAuth(cliArguments.isSslNeedClientAuth());
            ldapsTcp.setWantClientAuth(cliArguments.isSslWantClientAuth());

            ldapServer.setKeystoreFile(cliArguments.getSslKeystoreFile());
            ldapServer.setCertificatePassword(cliArguments.getSslKeystorePassword());
            ldapServer.setTransports(tcp, ldapsTcp);
        } else {
            ldapServer.setTransports(tcp);
        }
        ldapServer.setDirectoryService(directoryService);

        ldapServer.start();

        System.out.println("You can connect to the server now");
        final String host;
        if (CLIArguments.DEFAULT_ADDR.equals(cliArguments.getBindAddress())) {
            host = "127.0.0.1";
        } else {
            host = cliArguments.getBindAddress();
        }
        System.out.println("URL:      ldap://" + formatPossibleIpv6(host) + ":" + cliArguments.getPort());
        if (cliArguments.getSslPort() != null) {
            System.out.println("          ldaps://" + formatPossibleIpv6(host) + ":" + cliArguments.getSslPort());
        }
        System.out.println("User DN:  uid=admin,ou=system");
        System.out.println("Password: secret");
        System.out.println("LDAP server started in " + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * Stops LDAP server and the underlying directory service.
     * 
     * @throws Exception
     */
    public void stop() throws Exception {
        ldapServer.stop();
        directoryService.shutdown();
    }

    /**
     * Imports given LDIF file to the directory using given directory service and schema manager.
     *
     * @param ldifFiles
     * @throws Exception
     */
    private void importLdif(List<String> ldifFiles) throws Exception {
        if (ldifFiles == null || ldifFiles.isEmpty()) {
            System.out.println("Importing default data\n");
            importLdif(new LdifReader(LdapServer.class.getResourceAsStream("/" + LDIF_FILENAME_JBOSS_ORG)));
        } else {
            for (String ldifFile : ldifFiles) {
                System.out.println("Importing " + ldifFile + "\n");
                importLdif(new LdifReader(ldifFile));
            }
        }
    }

    private void importLdif(LdifReader ldifReader) throws Exception {
        try {
            for (LdifEntry ldifEntry : ldifReader) {
                checkPartition(ldifEntry);
                System.out.print(ldifEntry.toString());
                directoryService.getAdminSession()
                        .add(new DefaultEntry(directoryService.getSchemaManager(), ldifEntry.getEntry()));
            }
        } finally {
            IOUtils.closeQuietly(ldifReader);
        }
    }

    private void checkPartition(LdifEntry ldifEntry) throws Exception {
        Dn dn = ldifEntry.getDn();
        Dn parent = dn.getParent();
        try {
            directoryService.getAdminSession().exists(parent);
        } catch (Exception e) {
            System.out.println("Creating new partition for DN=" + dn + "\n");
            AvlPartition partition = new AvlPartition(directoryService.getSchemaManager());
            partition.setId(dn.getName());
            partition.setSuffixDn(dn);
            directoryService.addPartition(partition);
        }
    }

    private String formatPossibleIpv6(String host) {
        return (host != null && host.contains(":")) ? "[" + host + "]" : host;
    }

}
