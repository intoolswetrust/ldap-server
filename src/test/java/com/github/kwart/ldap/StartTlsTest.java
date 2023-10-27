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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A simple test of running LDAP server.
 *
 * @author Josef Cacek
 */
public class StartTlsTest {

    private LdapServer ldapServer;

    @Before
    public void before() throws Exception {
        CLIArguments cliArguments = new CLIArguments();
        new ExtCommander(cliArguments, new String[] { "-skf", "src/test/resources/tls-host-loopback-san.p12", "-skp", "123456",
                "-sep", "TLSv1.3", "-scs", "TLS_AES_128_GCM_SHA256" });
        ldapServer = new LdapServer(cliArguments);
    }

    @After
    public void after() throws Exception {
        ldapServer.stop();
    }

    @Test
    public void test() throws Exception {
        String host = "127.0.0.1";
        String port = "10389";
        String protocol = "ldap";
        String ldapUrl = protocol + "://" + host + ":" + port;

        SSLContext sslCtx = SSLContext.getInstance("TLS");
        sslCtx.init(null, new TrustManager[] { new NoVerificationTrustManager() }, new SecureRandom());
        SSLContext.setDefault(sslCtx);

        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
        env.put(Context.SECURITY_CREDENTIALS, "secret");
        LdapContext ctx = new InitialLdapContext(env, null);

        // Start TLS
        StartTlsResponse tls = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
        SSLSession sess = tls.negotiate();

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<?> namingEnum = ctx.search("dc=ldap,dc=example", "(uid=*)", searchControls);
        assertTrue(namingEnum.hasMore());
        SearchResult sr = (SearchResult) namingEnum.next();
        Attributes attrs = sr.getAttributes();
        assertEquals("Java Duke", attrs.get("cn").get());
        namingEnum.close();

        // Stop TLS
        tls.close();

        ctx.close();
    }

    public static class NoVerificationTrustManager extends X509ExtendedTrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
        }

    }
}
