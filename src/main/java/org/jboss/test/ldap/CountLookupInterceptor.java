/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
        return super.lookup(lookupContext);
    }

    @Override
    public EntryFilteringCursor search(SearchOperationContext searchContext) throws LdapException {
        counter++;
        System.out.println(">> search: " + searchContext + " (counter=" + counter + ")");
        return super.search(searchContext);
    }

    public static synchronized void resetCounter() {
        counter = 0L;
    }

    public static long getCounter() {
        return counter;
    }

}
