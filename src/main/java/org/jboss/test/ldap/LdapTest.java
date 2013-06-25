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
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * A LdapTest.
 * 
 * @author Josef Cacek
 */
public class LdapTest {

    /**
     * 
     * @param args
     * @throws NamingException
     */
    public static void main(String[] args) throws NamingException {
        String ldapUrl = "ldap://localhost:10389";
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.REFERRAL, "follow");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        //        env.put(Context.SECURITY_PRINCIPAL, "uid=test,ou=Users,dc=jboss,dc=org");
        env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
        env.put(Context.SECURITY_CREDENTIALS, "secret");
        //        env.put(Context.SECURITY_CREDENTIALS, "theduke");
        LdapContext ctx = new InitialLdapContext(env, null);
        //        ctx.setRequestControls(null);
        NamingEnumeration<?> namingEnum = ctx.search("dc=jboss,dc=org", "(|(cn=Admin)(objectClass=referral))",
                getSimpleSearchControls());
        //        NamingEnumeration<?> namingEnum = ctx.search("ou=Users,dc=jboss,dc=org", "(|(objectClass=referral)(uid=rslave))",
        //                getSimpleSearchControls());
        //        NamingEnumeration<?> namingEnum = ctx.search("ou=Roles,dc=jboss,dc=org",
        //                "(|(member=uid=jduke,ou=Users,dc=jboss,dc=org)(objectClass=referral))", getSimpleSearchControls());
        while (namingEnum.hasMore()) {
            SearchResult sr = (SearchResult) namingEnum.next();
            System.out.println((sr.isRelative() ? "Relative: " : "") + sr.getNameInNamespace() + ", Name: " + sr.getName());
            Attributes attrs = sr.getAttributes();
            System.out.println(attrs.get("cn").get());
        }
        namingEnum.close();
        ctx.close();

        System.out.println("Done");
    }

    private static SearchControls getSimpleSearchControls() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        //        searchControls.setTimeLimit(30000);
        //String[] attrIDs = {"objectGUID"};
        //searchControls.setReturningAttributes(attrIDs);
        return searchControls;
    }

    // Constructors ----------------------------------------------------------

    // Public methods --------------------------------------------------------

    // Protected methods -----------------------------------------------------

    // Private methods -------------------------------------------------------

    // Embedded classes ------------------------------------------------------
}
