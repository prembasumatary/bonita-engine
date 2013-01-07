/*
 * Copyright (C) 2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 */
package com.bonitasoft.engine.service;

import org.bonitasoft.engine.parameter.ParameterService;

/**
 * @author Matthieu Chaffotte
 */
public interface TenantServiceAccessor extends org.bonitasoft.engine.service.TenantServiceAccessor {

    ParameterService getParameterService();

}
