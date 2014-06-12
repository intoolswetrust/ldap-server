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

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * A simple LDAP bind application.
 * <p>
 * Usage:
 * 
 * <pre>
 * java org.jboss.test.ldap.Authenticate ldap://localhost:10389 uid=jduke,ou=Users,dc=jboss,dc=org theduke
 * </pre>
 * 
 * </p>
 * 
 * @author Josef Cacek
 */
public class Authenticate {

    /**
     * The main.
     * 
     * @param args
     * @throws NamingException
     */
    public static void main(String[] args) {
        if (args == null || args.length != 3) {
            System.err.println("Simple LDAP authenticator");
            System.err.println();
            System.err.println("Usage:");
            System.err.println("\tjava " + Authenticate.class.getName() + " <ldapURL> <userDN> <password>");
            System.err.println();
            System.err.println("Example:");
            System.err
                    .println("\tjava -cp ldap-server.jar org.jboss.test.ldap.Authenticate ldap://localhost:10389 uid=jduke,ou=Users,dc=jboss,dc=org theduke");
            System.err.println();
            System.err.println("Exit codes:");
            System.err.println("\t0\tAuthentication succeeded");
            System.err.println("\t1\tWrong parameters count");
            System.err.println("\t2\tAuthentication failed");
            System.exit(1);
        }
        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.PROVIDER_URL, args[0]);
        env.put(Context.SECURITY_PRINCIPAL, args[1]);
        env.put(Context.SECURITY_CREDENTIALS, args[2]);
        int exitCode = 2;
        try {
            final LdapContext ctx = new InitialLdapContext(env, null);
            System.out.println("User is authenticated");
            exitCode = 0;
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        System.exit(exitCode);
    }
}
