/**
 * Copyright (C) 2012, 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.api.impl;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.PermissionAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.engine.api.ThemeAPI;
import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.commons.exceptions.SBonitaRuntimeException;
import org.bonitasoft.engine.session.SessionService;
import org.bonitasoft.engine.session.model.SSession;
import org.bonitasoft.engine.sessionaccessor.SessionAccessor;

/**
 * @author Matthieu Chaffotte
 * @author Celine Souchet
 * @author Baptiste Mesta
 */
public class APIAccessorImpl implements APIAccessor {

    private static final long serialVersionUID = -3602975597536895697L;

    public APIAccessorImpl(final SessionAccessor sessionAccessor, final SessionService sessionService) {
        try {
            final long tenantId = sessionAccessor.getTenantId();
            final SSession session = sessionService.createSession(tenantId, this.getClass().getSimpleName());
            sessionAccessor.setSessionInfo(session.getId(), tenantId);
        } catch (final SBonitaRuntimeException sbre) {
            throw sbre;
        } catch (final SBonitaException sbe) {
            throw new SBonitaRuntimeException(sbe);
        }
    }

    @Override
    public IdentityAPI getIdentityAPI() {
        return new IdentityAPIImpl();
    }

    @Override
    public ProcessAPI getProcessAPI() {
        return new ProcessAPIImpl();
    }

    @Override
    public CommandAPI getCommandAPI() {
        return new CommandAPIImpl();
    }

    @Override
    public ProfileAPI getProfileAPI() {
        return new ProfileAPIImpl();
    }

    @Override
    public ThemeAPI getThemeAPI() {
        return new ThemeAPIImpl();
    }

    @Override
    public PermissionAPI getPermissionAPI() {
        return new PermissionAPIImpl();
    }
}
