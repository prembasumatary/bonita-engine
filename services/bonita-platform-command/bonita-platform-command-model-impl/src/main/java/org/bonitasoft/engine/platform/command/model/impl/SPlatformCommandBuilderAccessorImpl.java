/**
 * Copyright (C) 2012 BonitaSoft S.A.
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
package org.bonitasoft.engine.platform.command.model.impl;

import org.bonitasoft.engine.platform.command.model.SPlatformCommandBuilder;
import org.bonitasoft.engine.platform.command.model.SPlatformCommandBuilderAccessor;
import org.bonitasoft.engine.platform.command.model.SPlatformCommandLogBuilder;
import org.bonitasoft.engine.platform.command.model.SPlatformCommandUpdateBuilder;

/**
 * @author Zhang Bole
 */
public class SPlatformCommandBuilderAccessorImpl implements SPlatformCommandBuilderAccessor {

    @Override
    public SPlatformCommandBuilder getSPlatformCommandBuilder() {
        return new SPlatformCommandBuilderImpl();
    }

    @Override
    public SPlatformCommandLogBuilder getSPlatformCommandLogBuilder() {
        return new SPlatformCommandLogBuilderImpl();
    }

    @Override
    public SPlatformCommandUpdateBuilder getSPlatformCommandUpdateBuilder() {
        return new SPlatformCommandUpdateBuilderImpl();
    }

}