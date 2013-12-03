/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.external.web.forms;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.api.impl.SessionInfos;
import org.bonitasoft.engine.bpm.connector.ConnectorDefinitionWithInputValues;
import org.bonitasoft.engine.bpm.process.ActivationState;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotEnabledException;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.classloader.ClassLoaderService;
import org.bonitasoft.engine.command.SCommandExecutionException;
import org.bonitasoft.engine.command.SCommandParameterizationException;
import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.commons.transaction.TransactionContentWithResult;
import org.bonitasoft.engine.core.operation.model.SOperation;
import org.bonitasoft.engine.core.operation.model.builder.SOperationBuilders;
import org.bonitasoft.engine.core.process.definition.ProcessDefinitionService;
import org.bonitasoft.engine.core.process.definition.SProcessDefinitionNotFoundException;
import org.bonitasoft.engine.core.process.definition.exception.SProcessDefinitionReadException;
import org.bonitasoft.engine.core.process.definition.model.SProcessDefinition;
import org.bonitasoft.engine.core.process.definition.model.SProcessDefinitionDeployInfo;
import org.bonitasoft.engine.core.process.instance.model.SProcessInstance;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.RetrieveException;
import org.bonitasoft.engine.execution.ProcessExecutor;
import org.bonitasoft.engine.expression.model.builder.SExpressionBuilders;
import org.bonitasoft.engine.external.web.forms.ExecuteActionsBaseEntry;
import org.bonitasoft.engine.log.technical.TechnicalLogSeverity;
import org.bonitasoft.engine.log.technical.TechnicalLoggerService;
import org.bonitasoft.engine.operation.Operation;
import org.bonitasoft.engine.service.ModelConvertor;
import org.bonitasoft.engine.service.TenantServiceAccessor;

/**
 * @author Ruiheng Fan
 * @author Celine Souchet
 * @author Matthieu Chaffotte
 * @author Elias Ricken de Medeiros
 */
public class ExecuteActionsAndStartInstanceExt extends ExecuteActionsBaseEntry {

    @Override
    public Serializable execute(final Map<String, Serializable> parameters, final TenantServiceAccessor serviceAccessor)
            throws SCommandParameterizationException, SCommandExecutionException {
        final List<Operation> operations = getParameter(parameters, OPERATIONS_LIST_KEY, "Mandatory parameter " + OPERATIONS_LIST_KEY
                + " is missing or not convertible to List.");
        final Map<String, Object> operationsInputValues = getParameter(parameters, OPERATIONS_INPUT_KEY, "Mandatory parameter " + OPERATIONS_INPUT_KEY
                + " is missing or not convertible to Map.");
        final List<ConnectorDefinitionWithInputValues> connectorsWithInput = getParameter(parameters, CONNECTORS_LIST_KEY, "Mandatory parameter "
                + CONNECTORS_LIST_KEY + " is missing or not convertible to List.");
        final Long sProcessDefinitionID = getParameter(parameters, PROCESS_DEFINITION_ID_KEY, "Mandatory parameter " + PROCESS_DEFINITION_ID_KEY
                + " is missing or not convertible to long.");

        final Long userId = getParameter(parameters, USER_ID_KEY, "Mandatory parameter " + USER_ID_KEY + " is missing or not convertible to String.");

        try {
            final TechnicalLoggerService logger = serviceAccessor.getTechnicalLoggerService();
            final ClassLoaderService classLoaderService = serviceAccessor.getClassLoaderService();
            final ClassLoader processClassloader = classLoaderService.getLocalClassLoader("process", sProcessDefinitionID);
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(processClassloader);

                return startProcess(sProcessDefinitionID, userId, operations, operationsInputValues, connectorsWithInput, logger).getId();
            } finally {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        } catch (final BonitaException e) {
            throw new SCommandExecutionException(
                    "Error executing command 'Map<String, Serializable> ExecuteActionsAndStartInstanceExt(Map<Operation, Map<String, Serializable>> operationsMap, long processDefinitionID)'",
                    e);
        } catch (final SBonitaException e) {
            throw new SCommandExecutionException(
                    "Error executing command 'Map<String, Serializable> ExecuteActionsAndStartInstanceExt(Map<Operation, Map<String, Serializable>> operationsMap, long processDefinitionID)'",
                    e);
        }
    }

    private ProcessInstance startProcess(final long processDefinitionId, final long userId, final List<Operation> operations,
            final Map<String, Object> context, final List<ConnectorDefinitionWithInputValues> connectorsWithInput, final TechnicalLoggerService logger)
            throws ProcessDefinitionNotFoundException, CreationException, RetrieveException, ProcessDefinitionNotEnabledException {
        final TenantServiceAccessor tenantAccessor = getTenantAccessor();
        final ProcessDefinitionService processDefinitionService = tenantAccessor.getProcessDefinitionService();
        final ProcessExecutor processExecutor = tenantAccessor.getProcessExecutor();
        final SOperationBuilders sOperationBuilders = tenantAccessor.getSOperationBuilders();
        final SExpressionBuilders sExpressionBuilders = tenantAccessor.getSExpressionBuilders();
        final SessionInfos session = SessionInfos.getSessionInfos();
        final long starterId;
        if (userId == 0) {
            starterId = session.getUserId();
        } else {
            starterId = userId;
        }
        // Retrieval of the process definition:
        SProcessDefinition sDefinition;
        try {
            final GetProcessDeploymentInfo transactionContentWithResult = new GetProcessDeploymentInfo(processDefinitionId, processDefinitionService);
            transactionContentWithResult.execute();
            final SProcessDefinitionDeployInfo deployInfo = transactionContentWithResult.getResult();
            if (ActivationState.DISABLED.name().equals(deployInfo.getActivationState())) {
                throw new ProcessDefinitionNotEnabledException(deployInfo.getName(), deployInfo.getVersion(), deployInfo.getProcessId());
            }
            sDefinition = getServerProcessDefinition(processDefinitionId, processDefinitionService);
        } catch (final SProcessDefinitionNotFoundException e) {
            throw new ProcessDefinitionNotFoundException(e);
        } catch (final SBonitaException e) {
            throw new RetrieveException(e);
        }
        SProcessInstance startedInstance;
        try {
            final List<SOperation> sOperations = toSOperation(operations, sOperationBuilders, sExpressionBuilders);
            startedInstance = processExecutor.start(sDefinition, starterId, session.getUserId(), sOperations, context, connectorsWithInput);
        } catch (final SBonitaException e) {
            log(tenantAccessor, e);
            throw new CreationException(e);
        }// FIXME in case process instance creation exception -> put it in failed
        if (logger.isLoggable(getClass(), TechnicalLogSeverity.INFO)) {
            final StringBuilder stb = new StringBuilder();
            stb.append("The user <");
            stb.append(session.getUsername());
            if (starterId != session.getUserId()) {
                stb.append("> acting as delegate of user with id <");
                stb.append(starterId);
            }
            stb.append("> has started instance <");
            stb.append(startedInstance.getId());
            stb.append("> of process <");
            stb.append(sDefinition.getName());
            stb.append("> in version <");
            stb.append(sDefinition.getVersion());
            stb.append("> and id <");
            stb.append(sDefinition.getId());
            stb.append(">");
            logger.log(getClass(), TechnicalLogSeverity.INFO, stb.toString());
        }
        return ModelConvertor.toProcessInstance(sDefinition, startedInstance);
    }

    /**
     * @author Baptiste Mesta
     */
    private final class GetProcessDeploymentInfo implements TransactionContentWithResult<SProcessDefinitionDeployInfo> {

        private final Long processDefinitionUUID;

        private final ProcessDefinitionService processDefinitionService;

        private SProcessDefinitionDeployInfo processDefinitionDI;

        private GetProcessDeploymentInfo(final Long processDefinitionUUID, final ProcessDefinitionService processDefinitionService) {
            this.processDefinitionUUID = processDefinitionUUID;
            this.processDefinitionService = processDefinitionService;
        }

        @Override
        public void execute() throws SProcessDefinitionNotFoundException, SProcessDefinitionReadException {
            processDefinitionDI = processDefinitionService.getProcessDeploymentInfo(processDefinitionUUID);
        }

        @Override
        public SProcessDefinitionDeployInfo getResult() {
            return processDefinitionDI;
        }
    }

}