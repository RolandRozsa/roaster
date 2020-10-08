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
package hu.icellmobilsoft.roaster.tm4j.common;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.tm4j.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.tm4j.common.client.Tm4jService;
import hu.icellmobilsoft.roaster.tm4j.common.client.model.Execution;
import hu.icellmobilsoft.roaster.tm4j.common.config.InvalidConfigException;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterConfig;
import hu.icellmobilsoft.roaster.tm4j.common.spi.Tm4jRecord;

import java.lang.reflect.Method;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author martin.nagy
 * @since 0.2.0
 */
class DefaultTm4jReporter implements Tm4jReporter {
    private final Logger log = Logger.getLogger(DefaultTm4jReporter.class);

    private final Tm4jReporterConfig config;
    private final Tm4jService tm4JService;

    public DefaultTm4jReporter(Tm4jReporterConfig config) {
        this.config = config;
        this.tm4JService = new Tm4jService(config.getServer());
        validateConfig();
    }

    private void validateConfig() {
        if (config.getProjectKey() == null) {
            throw new InvalidConfigException("projectKey parameter is missing");
        }
        if (config.getTestCycleKey() == null) {
            throw new InvalidConfigException("testCycleKey parameter is missing");
        }
        if (!tm4JService.isTestRunExist(config.getTestCycleKey())) {
            throw new InvalidConfigException("supplied testCycleKey not found: " + config.getTestCycleKey());
        }
    }

    @Override
    public void reportSuccess(Tm4jRecord tm4jRecord) {
        getTestCaseIds(tm4jRecord.getTestMethod())
                .forEach(testCaseKey -> {
                    Execution execution = createExecution(tm4jRecord, testCaseKey);
                    execution.setStatus("Pass");
                    execution.setComment(createCommentBase(tm4jRecord.getId()));
                    publishResult(execution);
                });
    }

    @Override
    public void reportFail(Tm4jRecord tm4jRecord, Throwable cause) {
        getTestCaseIds(tm4jRecord.getTestMethod())
                .forEach(testCaseKey -> {
                    Execution execution = createExecution(tm4jRecord, testCaseKey);
                    execution.setStatus("Fail");
                    execution.setComment(
                            createCommentBase(tm4jRecord.getId()) +
                                    createFailureComment(cause)
                    );
                    publishResult(execution);
                });
    }

    @Override
    public void reportDisabled(Tm4jRecord tm4jRecord, Optional<String> reason) {
        getTestCaseIds(tm4jRecord.getTestMethod())
                .forEach(testCaseKey -> {
                    Execution execution = createExecution(tm4jRecord, testCaseKey);
                    execution.setStatus("Blocked");
                    execution.setComment(
                            createCommentBase(tm4jRecord.getId()) +
                                    createDisabledTestComment(reason)
                    );
                    publishResult(execution);
                });
    }

    private void publishResult(Execution execution) {
        tm4JService.postResult(config.getTestCycleKey(), execution);
        log.info("Test result published to TM4J: [{0}]", execution.getTestCaseKey());
    }

    private Stream<String> getTestCaseIds(Method testMethod) {
        return Arrays.stream(testMethod.getAnnotationsByType(TestCaseId.class))
                .map(TestCaseId::value)
                .map(testCaseId -> {
                    if (tm4JService.isTestCaseExist(testCaseId)) {
                        return testCaseId;
                    } else {
                        log.warn("Test case ID not found: [{0}]", testCaseId);
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    private Execution createExecution(Tm4jRecord tm4jRecord, String testCaseKey) {
        Execution execution = new Execution();
        execution.setProjectKey(config.getProjectKey());
        execution.setTestCaseKey(testCaseKey);
        execution.setActualStartDate(tm4jRecord.getStartTime());
        execution.setActualEndDate(tm4jRecord.getEndTime());
        execution.setExecutionTime(getDurationInMillis(tm4jRecord));
        return execution;
    }

    private long getDurationInMillis(Tm4jRecord tm4jRecord) {
        return tm4jRecord.getStartTime().until(tm4jRecord.getEndTime(), ChronoUnit.MILLIS);
    }

    private String createCommentBase(String uniqueId) {
        return "Environment: " + config.getEnvironment().toUpperCase() +
                "</br></br>" +
                "Test method: " + uniqueId;
    }

    private String createFailureComment(Throwable cause) {
        return "</br></br>Reason of failure: " + htmlEscape(cause.toString());
    }

    private String htmlEscape(String string) {
        return string
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String createDisabledTestComment(Optional<String> reason) {
        return reason.map(s -> "</br></br>Test case has been skipped by: " + s)
                .orElse("");
    }

}
