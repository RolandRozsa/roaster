/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.zephyr.common.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

/**
 * {@link JacksonJsonProvider} implementation for using {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} so we can minimize the
 * dependency on the jira rest api.
 *
 * @author martin.nagy
 * @since 0.4.0
 */
@Provider
@Consumes({"application/json", "application/*+json", "text/json"})
@Produces({"application/json", "application/*+json", "text/json"})
public class ZephyrJsonProvider extends JacksonJsonProvider {

    /**
     * Initializes the object with {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} turned off
     */
    public ZephyrJsonProvider() {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
