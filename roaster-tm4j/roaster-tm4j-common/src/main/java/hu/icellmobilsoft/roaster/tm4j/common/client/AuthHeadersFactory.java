/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hu.icellmobilsoft.roaster.tm4j.common.client;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import hu.icellmobilsoft.roaster.tm4j.common.config.ITm4jReporterServerConfig;

/**
 * Sets the {@literal Authorization} header for the TM4J rest client
 *
 * @author martin.nagy
 * @since 0.2.0
 */
@Dependent
public class AuthHeadersFactory implements ClientHeadersFactory {

    @Inject
    private ITm4jReporterServerConfig config;

    /**
     * Initializes the object, validates the config
     */
    @PostConstruct
    public void init() {
        config.validate();
    }

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders) {
        incomingHeaders.putSingle("Authorization", "Basic " + config.getBasicAuthToken());
        return incomingHeaders;
    }

}
