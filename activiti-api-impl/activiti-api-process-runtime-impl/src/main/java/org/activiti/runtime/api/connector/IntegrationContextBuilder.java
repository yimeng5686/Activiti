/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.runtime.api.connector;

import org.activiti.api.process.model.IntegrationContext;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.persistence.entity.integration.IntegrationContextEntity;
import org.activiti.model.connector.Action;
import org.activiti.model.connector.Variable;
import org.activiti.runtime.api.model.impl.IntegrationContextImpl;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IntegrationContextBuilder {

    public IntegrationContext from(IntegrationContextEntity integrationContextEntity,
                                   DelegateExecution execution, Action action) {
        IntegrationContextImpl integrationContext = buildFromExecution(execution, action);
        integrationContext.setId(integrationContextEntity.getId());
        return integrationContext;
    }

    public IntegrationContext from(DelegateExecution execution, Action action) {
        IntegrationContextImpl integrationContext = buildFromExecution(execution,
                action);
        return integrationContext;
    }

    private IntegrationContextImpl buildFromExecution(DelegateExecution execution,
                                                      Action action) {
        IntegrationContextImpl integrationContext = new IntegrationContextImpl();
        integrationContext.setProcessInstanceId(execution.getProcessInstanceId());
        integrationContext.setProcessDefinitionId(execution.getProcessDefinitionId());
        integrationContext.setActivityElementId(execution.getCurrentActivityId());

        String implementation = ((ServiceTask) execution.getCurrentFlowElement()).getImplementation();

        integrationContext.setConnectorType(implementation);

        integrationContext.setInBoundVariables(buildInBoundVariables(
                action,
                execution));

        return integrationContext;
    }

    private Map<String, Object> buildInBoundVariables(Action action,
                                                      DelegateExecution execution) {
        Map<String, Object> inBoundVariables;
        if (action != null) {

            inBoundVariables = action.getInput().stream().filter(input -> execution.getVariables().containsKey(input.getName())).collect(Collectors.toMap(Variable::getName,
                    Function.identity()));
        } else {
            inBoundVariables = execution.getVariables();
        }
        return inBoundVariables;
    }
}
