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

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.server.core.api.filtering.EntryFilteringCursor;
import org.apache.directory.server.core.api.interceptor.BaseInterceptor;
import org.apache.directory.server.core.api.interceptor.context.LookupOperationContext;
import org.apache.directory.server.core.api.interceptor.context.SearchOperationContext;

/**
 * ApacheDS Interceptor which holds counter for lookup and search requests.
 * 
 * @author Josef Cacek
 */
public class CountLookupInterceptor extends BaseInterceptor {

    private static volatile long counter = 0L;

    @Override
    public Entry lookup(LookupOperationContext lookupContext) throws LdapException {
        counter++;
        System.out.println(">> lookup: " + lookupContext + " (counter=" + counter + ")");
        return next(lookupContext);
    }

    @Override
    public EntryFilteringCursor search(SearchOperationContext searchContext) throws LdapException {
        counter++;
        System.out.println(">> search: " + searchContext + " (counter=" + counter + ")");
        return next(searchContext);
    }

    public static synchronized void resetCounter() {
        counter = 0L;
    }

    public static long getCounter() {
        return counter;
    }

}
