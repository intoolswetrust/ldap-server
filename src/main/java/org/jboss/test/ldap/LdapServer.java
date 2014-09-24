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

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.AnnotationUtils;
import org.apache.directory.server.core.annotations.ContextEntry;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreateIndex;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.factory.DSAnnotationProcessor;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.apache.directory.server.factory.ServerAnnotationProcessor;

/**
 * Creates and starts LDAP server(s).
 *
 * @author Josef Cacek
 */
public class LdapServer {

    private static final int LDAP_PORT = 10389;

    private static final String LDIF_FILENAME_JBOSS_ORG = "jboss-org.ldif";

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
        jCmd.setUsageHead("The ldap-server is a simple LDAP server implementation based on ApacheDS. It creates one user partition with root 'dc=jboss,dc=org'.");
        jCmd.setUsageTail("Examples:\n\n" //
                + "$ java -jar ldap-server.jar users.ldif\n" //
                + " Starts LDAP server on port 10389 (all interfaces) and imports users.ldif\n\n" //
                + "$ java -jar ldap-server.jar -b 127.0.0.1 -p 389\n" //
                + " Starts LDAP server on address 127.0.0.1:389 and imports default data (one user entry 'uid=jduke,ou=Users,dc=jboss,dc=org'");
        if (cliArguments.isHelp()) {
            jCmd.usage();
            return;
        }
        createServer1(cliArguments);
    }

    /**
     * Create a single LDAP server.
     *
     * @param cliArguments
     *
     * @throws Exception
     */
    //@formatter:off
    @CreateDS(
        name = "JBossOrgDS",
        allowAnonAccess=true,
        factory=InMemoryDirectoryServiceFactory.class,
        enableChangeLog = false,
        additionalInterceptors=CountLookupInterceptor.class,
        partitions =
        {
            @CreatePartition(
                name = "jbossorg",
                type = AvlPartition.class,
                suffix = "dc=jboss,dc=org",
                contextEntry = @ContextEntry(
                    entryLdif =
                        "dn: dc=jboss,dc=org\n" +
                        "dc: jboss\n" +
                        "objectClass: top\n" +
                        "objectClass: domain\n\n" ),
                indexes =
                {
                    @CreateIndex( attribute = "objectClass" ),
                    @CreateIndex( attribute = "dc" ),
                    @CreateIndex( attribute = "ou" )
                }
            )
        })
    @CreateLdapServer (
        transports =
        {
            @CreateTransport( protocol = "LDAP",  port = LDAP_PORT, address = "0.0.0.0" ),
        })
    //@formatter:on
    public static void createServer1(CLIArguments cliArguments) throws Exception {
        long startTime = System.currentTimeMillis();
        DirectoryService directoryService = DSAnnotationProcessor.getDirectoryService();
        System.out.println("Directory service started in " + (System.currentTimeMillis() - startTime) + "ms");
        final SchemaManager schemaManager = directoryService.getSchemaManager();
        importLdif(directoryService, schemaManager, cliArguments.getLdifFiles());
        final ManagedCreateLdapServer createLdapServer = new ManagedCreateLdapServer(
                (CreateLdapServer) AnnotationUtils.getInstance(CreateLdapServer.class));
        fixTransportAddress(createLdapServer, cliArguments.getBindAddress(), cliArguments.getPort());
        ServerAnnotationProcessor.instantiateLdapServer(createLdapServer, directoryService).start();

        System.out.println("You can connect to the server now");
        final String host;
        if (CLIArguments.DEFAULT_ADDR.equals(cliArguments.getBindAddress())) {
            host = "127.0.0.1";
        } else {
            host = cliArguments.getBindAddress();
        }
        System.out.println("URL:      ldap://" + host + ":" + cliArguments.getPort());
        System.out.println("User DN:  uid=admin,ou=system");
        System.out.println("Password: secret");
        System.out.println("LDAP server started in " + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * Imports given LDIF file to the directoy using given directory service and schema manager.
     *
     * @param directoryService
     * @param schemaManager
     * @param ldifFiles
     * @throws LdapException
     */
    private static void importLdif(DirectoryService directoryService, final SchemaManager schemaManager, List<String> ldifFiles)
            throws LdapException {
        if (ldifFiles == null || ldifFiles.isEmpty()) {
            System.out.println("Importing default data:\n");
            importLdif(directoryService, schemaManager,
                    new LdifReader(LdapServer.class.getResourceAsStream("/" + LDIF_FILENAME_JBOSS_ORG)));
        } else {
            for (String ldifFile : ldifFiles) {
                System.out.println("Importing " + ldifFile + ":\n");
                importLdif(directoryService, schemaManager, new LdifReader(ldifFile));
            }
        }
    }

    private static void importLdif(DirectoryService directoryService, final SchemaManager schemaManager, LdifReader ldifReader)
            throws LdapException {
        try {
            for (LdifEntry ldifEntry : ldifReader) {
                System.out.print(ldifEntry.toString());
                directoryService.getAdminSession().add(new DefaultEntry(schemaManager, ldifEntry.getEntry()));
            }
        } finally {
            IOUtils.closeQuietly(ldifReader);
        }
    }

    /**
     * Fixes bind address in the CreateTransport annotation.
     *
     * @param createLdapServer
     * @param port
     */
    private static void fixTransportAddress(ManagedCreateLdapServer createLdapServer, String address, int port) {
        final CreateTransport[] createTransports = createLdapServer.transports();
        for (int i = 0; i < createTransports.length; i++) {
            final ManagedCreateTransport mgCreateTransport = new ManagedCreateTransport(createTransports[i]);
            mgCreateTransport.setAddress(address);
            mgCreateTransport.setPort(port);
            createTransports[i] = mgCreateTransport;
        }
    }
}
