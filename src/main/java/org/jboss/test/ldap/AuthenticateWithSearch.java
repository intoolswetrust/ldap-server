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

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * An LDAP bind application with user search by <code>uid</code> attribute.
 * <p>
 * Usage:
 * 
 * <pre>
 * java org.jboss.test.ldap.AuthenticateWithSearch ldap://localhost:10389 jduke theduke
 * </pre>
 * 
 * </p>
 * 
 * @author Josef Cacek
 */
public class AuthenticateWithSearch {

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
            System.err.println("\tjava " + AuthenticateWithSearch.class.getName() + " <ldapURL> <username> <password>");
            System.err.println();
            System.err.println("Example:");
            System.err.println("\tjava -cp ldap-server.jar " + AuthenticateWithSearch.class.getName()
                    + " ldap://localhost:10389 jduke theduke");
            System.err.println();
            System.err.println("Exit codes:");
            System.err.println("\t0\tAuthentication succeeded");
            System.err.println("\t1\tWrong parameters count");
            System.err.println("\t2\tAuthentication failed");
            System.exit(1);
        }

        // Initial bind with Admin account - used to search User DN
        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.PROVIDER_URL, args[0]);
        env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
        env.put(Context.SECURITY_CREDENTIALS, "secret");
        int exitCode = 2;
        LdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
            final SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            final String baseDN = "dc=jboss,dc=org";
            NamingEnumeration<?> namingEnum = ctx.search(baseDN, "(uid={0})", new Object[] { args[1] }, searchControls);
            String userDN = null;
            if (namingEnum.hasMore()) {
                // We found a user with given uid in the LDAP
                SearchResult sr = (SearchResult) namingEnum.next();
                if (sr.isRelative()) {
                    userDN = sr.getName() + "," + baseDN;
                } else {
                    userDN = sr.getName();
                }
                System.out.println("User found: " + userDN);
            }
            namingEnum.close();
            ctx.close();
            if (userDN != null) {
                // try bind with user DN and user's password
                env.put(Context.SECURITY_PRINCIPAL, userDN);
                env.put(Context.SECURITY_CREDENTIALS, args[2]);
                ctx = new InitialLdapContext(env, null);
                System.out.println("User is authenticated");
                exitCode = 0;
            }
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }
        System.exit(exitCode);
    }
}
