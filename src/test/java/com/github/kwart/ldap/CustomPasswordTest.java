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

package com.github.kwart.ldap;

import static org.junit.Assert.assertThrows;

import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CustomPasswordTest {

    private LdapServer ldapServer;

    @Before
    public void before() throws Exception {
        String[] args = new String[] { "-ap", "testPassword" };
        CLIArguments cliArguments = new CLIArguments();
        new ExtCommander(cliArguments, args);
        ldapServer = new LdapServer(cliArguments);
    }

    @After
    public void after() throws Exception {
        ldapServer.stop();
    }

    @Test
    public void testDefaultPwdFails() throws Exception {
        Properties env = createProperties("secret");
        assertThrows(AuthenticationException.class, () -> new InitialLdapContext(env, null));
    }

    @Test
    public void testCustomPwdPasses() throws Exception {
        Properties env = createProperties("testPassword");
        new InitialLdapContext(env, null).close();
    }

    private Properties createProperties(String pwd) {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://127.0.0.1:10389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
        env.put(Context.SECURITY_CREDENTIALS, pwd);
        return env;
    }
}
