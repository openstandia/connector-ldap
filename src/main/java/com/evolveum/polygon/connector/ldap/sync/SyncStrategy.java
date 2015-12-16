/**
 * Copyright (c) 2015 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evolveum.polygon.connector.ldap.sync;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.SyncResultsHandler;
import org.identityconnectors.framework.common.objects.SyncToken;

import com.evolveum.polygon.connector.ldap.AbstractLdapConfiguration;
import com.evolveum.polygon.connector.ldap.LdapConfiguration;
import com.evolveum.polygon.connector.ldap.LdapUtil;
import com.evolveum.polygon.connector.ldap.schema.SchemaTranslator;

/**
 * @author semancik
 *
 */
public abstract class SyncStrategy {
	
	private static final Log LOG = Log.getLog(SyncStrategy.class);
	
	private AbstractLdapConfiguration configuration;
    private LdapNetworkConnection connection;
    private SchemaManager schemaManager;
    private SchemaTranslator schemaTranslator;
    
	public SyncStrategy(AbstractLdapConfiguration configuration, LdapNetworkConnection connection, 
			SchemaManager schemaManager, SchemaTranslator schemaTranslator) {
		super();
		this.configuration = configuration;
		this.connection = connection;
		this.schemaManager = schemaManager;
		this.schemaTranslator = schemaTranslator;
	}

	public AbstractLdapConfiguration getConfiguration() {
		return configuration;
	}

	public LdapNetworkConnection getConnection() {
		return connection;
	}
	
	public SchemaManager getSchemaManager() {
		return schemaManager;
	}

	public SchemaTranslator getSchemaTranslator() {
		return schemaTranslator;
	}

	public abstract void sync(ObjectClass objectClass, SyncToken token, SyncResultsHandler handler, OperationOptions options);

	public abstract SyncToken getLatestSyncToken(ObjectClass objectClass);
	
	protected boolean isAcceptableForSynchronization(Entry entry, 
			org.apache.directory.api.ldap.model.schema.ObjectClass requiredldapObjectClass,
			String[] modifiersNamesToFilterOut) {
		if (requiredldapObjectClass != null) {
			if (!LdapUtil.isObjectClass(entry, requiredldapObjectClass)) {
				LOG.ok("Skipping synchronization of entry {0} because object class does not match", entry.getDn());
				return false;
			}
		}
		if (modifiersNamesToFilterOut != null && modifiersNamesToFilterOut.length > 0) {
			if (!LdapUtil.hasModifierName(entry, modifiersNamesToFilterOut)) {
				LOG.ok("Skipping synchronization of entry {0} because modifiers name is filtered out", entry.getDn());
				return false;
			}
		}
		return true;
	}
	
}
