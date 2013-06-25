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

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.AnnotationUtils;
import org.apache.directory.server.core.annotations.ContextEntry;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreateIndex;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.factory.DSAnnotationProcessor;
import org.apache.directory.server.core.kerberos.KeyDerivationInterceptor;
import org.apache.directory.server.factory.ServerAnnotationProcessor;
import org.apache.directory.shared.ldap.model.entry.DefaultEntry;
import org.apache.directory.shared.ldap.model.ldif.LdifEntry;
import org.apache.directory.shared.ldap.model.ldif.LdifReader;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;

public class LdapServer {

    // Public methods --------------------------------------------------------

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        create2();
        create1();
    }

    //@formatter:off
    @CreateDS( 
        name = "JBossOrgDS",
        allowAnonAccess=true,
        partitions =
        {
            @CreatePartition(
                name = "jbossorg",
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
                })
        },
        additionalInterceptors = { KeyDerivationInterceptor.class })
    @CreateLdapServer ( 
        transports = 
        {
            @CreateTransport( protocol = "LDAP",  port = 10389, address = "0.0.0.0" ), 
        })            
    //@formatter:on
    public static void create1() throws Exception {
        DirectoryService directoryService = DSAnnotationProcessor.getDirectoryService();
        final SchemaManager schemaManager = directoryService.getSchemaManager();
        importLdif(directoryService, schemaManager, "jboss-org.ldif");
        final ManagedCreateLdapServer createLdapServer = new ManagedCreateLdapServer(
                (CreateLdapServer) AnnotationUtils.getInstance(CreateLdapServer.class));
        ServerAnnotationProcessor.instantiateLdapServer(createLdapServer, directoryService).start();
    }

    //@formatter:off
    @CreateDS( 
        name = "JBossComDS",
        allowAnonAccess=true,
        partitions =
        {
            @CreatePartition(
                name = "jbosscom",
                suffix = "dc=jboss,dc=com",
                contextEntry = @ContextEntry( 
                    entryLdif =
                        "dn: dc=jboss,dc=com\n" +
                        "dc: jboss\n" +
                        "objectClass: top\n" +
                        "objectClass: domain\n\n" ),
                indexes = 
                {
                    @CreateIndex( attribute = "objectClass" ),
                    @CreateIndex( attribute = "dc" ),
                    @CreateIndex( attribute = "ou" )
                })
        },
        additionalInterceptors = { KeyDerivationInterceptor.class })
    @CreateLdapServer ( 
        transports = 
        {
            @CreateTransport( protocol = "LDAP",  port = 11389, address = "0.0.0.0" ), 
        })            
    //@formatter:on
    public static void create2() throws Exception {
        DirectoryService directoryService = DSAnnotationProcessor.getDirectoryService();
        final SchemaManager schemaManager = directoryService.getSchemaManager();
        importLdif(directoryService, schemaManager, "jboss-com.ldif");
        final ManagedCreateLdapServer createLdapServer = new ManagedCreateLdapServer(
                (CreateLdapServer) AnnotationUtils.getInstance(CreateLdapServer.class));
        ServerAnnotationProcessor.instantiateLdapServer(createLdapServer, directoryService).start();
    }

    /**
     * 
     * @param directoryService
     * @param schemaManager
     * @param ldifFile
     * @throws Exception
     */
    private static void importLdif(DirectoryService directoryService, final SchemaManager schemaManager, String ldifFile)
            throws Exception {
        try {
            for (LdifEntry ldifEntry : new LdifReader(LdapServer.class.getResourceAsStream("/" + ldifFile))) {
                directoryService.getAdminSession().add(new DefaultEntry(schemaManager, ldifEntry.getEntry()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
