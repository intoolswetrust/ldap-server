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
 * A simple test of running LDAP server.
 * 
 * @author Josef Cacek
 */
public class LdapTest {

    // Public methods --------------------------------------------------------

    /**
     * The main.
     * 
     * @param args
     * @throws NamingException
     */
    public static void main(String[] args) throws NamingException {
        final String ldapUrl = "ldap://[::1]:10389";
        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
        env.put(Context.SECURITY_CREDENTIALS, "secret");
        final LdapContext ctx = new InitialLdapContext(env, null);
        // ctx.setRequestControls(null);
        final SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<?> namingEnum = ctx.search("dc=jboss,dc=org", "(uid=*)", searchControls);
        while (namingEnum.hasMore()) {
            SearchResult sr = (SearchResult) namingEnum.next();
            Attributes attrs = sr.getAttributes();
            System.out.println(attrs.get("cn"));
        }
        namingEnum.close();
        ctx.close();
    }
}
