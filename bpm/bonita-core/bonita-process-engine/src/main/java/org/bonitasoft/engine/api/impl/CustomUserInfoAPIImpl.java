/**
 * Copyright (C) 2014 BonitaSoft S.A.
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
 */
package org.bonitasoft.engine.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.identity.CustomUserInfoDefinition;
import org.bonitasoft.engine.identity.CustomUserInfoDefinitionCreator;
import org.bonitasoft.engine.identity.IdentityService;
import org.bonitasoft.engine.identity.SIdentityException;
import org.bonitasoft.engine.identity.impl.CustomUserInfoDefinitionImpl;
import org.bonitasoft.engine.identity.model.SCustomUserInfoDefinition;
import org.bonitasoft.engine.identity.model.builder.SCustomUserInfoDefinitionBuilder;
import org.bonitasoft.engine.identity.model.builder.SCustomUserInfoDefinitionBuilderFactory;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.descriptor.SearchEntitiesDescriptor;
import org.bonitasoft.engine.search.identity.SearchUsers;

/**
 * @author Vincent Elcrin
 */
public class CustomUserInfoAPIImpl {

    private IdentityService service;

    public CustomUserInfoAPIImpl(IdentityService service) {
        this.service = service;
    }

    public CustomUserInfoDefinition createCustomUserInfoDefinition(SCustomUserInfoDefinitionBuilderFactory factory, CustomUserInfoDefinitionCreator creator) throws CreationException {
        if (creator == null) {
            throw new CreationException("Can not create null custom user details.");
        }

        final SCustomUserInfoDefinitionBuilder builder = factory.createNewInstance();
        builder.setName(creator.getName());
        builder.setDisplayName(creator.getDisplayName());
        builder.setDescription(creator.getDescription());
        try {
            return toCustomUserInfoDefinition(service.createCustomUserInfoDefinition(builder.done()));
        } catch (SIdentityException e) {
            throw new CreationException(e);
        }
    }

    public List<CustomUserInfoDefinition> getCustomUserInfoDefinitions(int startIndex, int maxResult) throws SIdentityException {
        List<CustomUserInfoDefinition> definitions = new ArrayList<CustomUserInfoDefinition>();
        for (SCustomUserInfoDefinition sDefinition : service.getCustomUserInfoDefinitions(startIndex, maxResult)) {
            definitions.add(toCustomUserInfoDefinition(sDefinition));
        }
        return definitions;
    }

    private CustomUserInfoDefinition toCustomUserInfoDefinition(SCustomUserInfoDefinition sDefinition) {
        CustomUserInfoDefinitionImpl definition = new CustomUserInfoDefinitionImpl();
        definition.setName(sDefinition.getName());
        definition.setDisplayName(sDefinition.getDisplayName());
        definition.setDescription(sDefinition.getDescription());
        return definition;
    }
}
