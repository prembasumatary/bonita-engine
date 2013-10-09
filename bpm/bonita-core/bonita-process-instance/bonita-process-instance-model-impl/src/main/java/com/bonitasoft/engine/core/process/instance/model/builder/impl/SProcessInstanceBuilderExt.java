/*******************************************************************************
 * Copyright (C) 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.core.process.instance.model.builder.impl;

import org.bonitasoft.engine.commons.NullCheckingUtil;
import org.bonitasoft.engine.core.process.definition.model.SProcessDefinition;
import org.bonitasoft.engine.core.process.instance.model.SStateCategory;
import org.bonitasoft.engine.core.process.instance.model.builder.impl.SProcessInstanceBuilderImpl;
import org.bonitasoft.engine.core.process.instance.model.impl.SProcessInstanceImpl;

import com.bonitasoft.engine.core.process.instance.model.builder.SProcessInstanceBuilder;

/**
 * @author Celine Souchet
 */
public class SProcessInstanceBuilderExt extends SProcessInstanceBuilderImpl implements SProcessInstanceBuilder {

    static final String STRING_INDEX_1_KEY = "stringIndex1";

    static final String STRING_INDEX_2_KEY = "stringIndex2";

    static final String STRING_INDEX_3_KEY = "stringIndex3";

    static final String STRING_INDEX_4_KEY = "stringIndex4";

    static final String STRING_INDEX_5_KEY = "stringIndex5";

    @Override
    public SProcessInstanceBuilder createNewInstance(final String name, final long processDefinitionId) {
        NullCheckingUtil.checkArgsNotNull(name, processDefinitionId);
        entity = new SProcessInstanceImpl(name, processDefinitionId);
        entity.setStateCategory(SStateCategory.NORMAL);
        return this;
    }

    @Override
    public SProcessInstanceBuilder createNewInstance(final String name, final long processDefinitionId, final String description) {
        NullCheckingUtil.checkArgsNotNull(name, processDefinitionId);
        entity = new SProcessInstanceImpl(name, processDefinitionId);
        entity.setStateCategory(SStateCategory.NORMAL);
        entity.setDescription(description);
        return this;
    }

    @Override
    public SProcessInstanceBuilder createNewInstance(final SProcessDefinition definition) {
        return createNewInstance(definition.getName(), definition.getId(), definition.getDescription());
    }

    @Override
    public SProcessInstanceBuilder setStringIndex(final int index, final String value) {
        switch (index) {
            case 1:
                entity.setStringIndex1(value);
                break;
            case 2:
                entity.setStringIndex2(value);
                break;
            case 3:
                entity.setStringIndex3(value);
                break;
            case 4:
                entity.setStringIndex4(value);
                break;
            case 5:
                entity.setStringIndex5(value);
                break;
            default:
                throw new IndexOutOfBoundsException("string index label must be between 1 and 5 (included)");
        }
        return this;
    }

    @Override
    public String getStringIndex1Key() {
        return STRING_INDEX_1_KEY;
    }

    @Override
    public String getStringIndex2Key() {
        return STRING_INDEX_2_KEY;
    }

    @Override
    public String getStringIndex3Key() {
        return STRING_INDEX_3_KEY;
    }

    @Override
    public String getStringIndex4Key() {
        return STRING_INDEX_4_KEY;
    }

    @Override
    public String getStringIndex5Key() {
        return STRING_INDEX_5_KEY;
    }

}