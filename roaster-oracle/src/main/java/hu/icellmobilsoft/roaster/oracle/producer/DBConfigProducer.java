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
package hu.icellmobilsoft.roaster.oracle.producer;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.roaster.oracle.annotation.DBConnection;
import hu.icellmobilsoft.roaster.oracle.config.ManagedDBConfig;

/**
 * Producer for creating ManagedDBConfig
 *
 * @author balazs.joo
 * @since 0.0.2
 */
@ApplicationScoped
public class DBConfigProducer {

    /**
     * Creates ManagedDBConfig for the injected configKey
     */
    @Produces
    @Dependent
    @DBConnection(configKey = "")
    public ManagedDBConfig getDBConfig(InjectionPoint injectionPoint) throws BaseException {
        Optional<DBConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, DBConnection.class);
        String configKey = annotation.map(DBConnection::configKey)
                .orElseThrow(() -> new BaseException(CoffeeFaultType.INVALID_INPUT, "configKey value not found!"));
        ManagedDBConfig dbConfig = CDI.current().select(ManagedDBConfig.class).get();
        dbConfig.setConfigKey(configKey);
        return dbConfig;
    }

}
