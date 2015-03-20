/**
 * Copyright (C) 2015 BonitaSoft S.A.
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
package org.bonitasoft.engine.command.web;

import org.bonitasoft.engine.LocalServerTestsInitializer;
import org.bonitasoft.engine.command.web.profile.ProfileImportCommandIT;
import org.bonitasoft.engine.test.runner.BonitaSuiteRunner;
import org.bonitasoft.engine.test.runner.BonitaSuiteRunner.Initializer;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Emmanuel Duchastenier
 */
@RunWith(BonitaSuiteRunner.class)
@SuiteClasses({
        UserPermissionCommandIT.class,
        ProfileImportCommandIT.class,
        ActorPermissionCommandIT.class,
        ActivityCommandIT.class,
        EntityMemberCommandsIT.class
})
@Initializer(LocalServerTestsInitializer.class)
public class ExternalCommandsTests {

}
