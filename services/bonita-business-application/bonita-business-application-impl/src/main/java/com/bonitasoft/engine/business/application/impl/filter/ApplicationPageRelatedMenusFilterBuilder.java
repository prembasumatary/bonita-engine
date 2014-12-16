/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 ******************************************************************************/

package com.bonitasoft.engine.business.application.impl.filter;

import java.util.Collections;
import java.util.List;

import org.bonitasoft.engine.persistence.FilterOption;
import org.bonitasoft.engine.persistence.OrderByOption;
import org.bonitasoft.engine.persistence.OrderByType;
import org.bonitasoft.engine.persistence.QueryOptions;

import com.bonitasoft.engine.business.application.model.SApplicationMenu;
import com.bonitasoft.engine.business.application.model.builder.impl.SApplicationMenuBuilderFactoryImpl;

/**
 * @author Elias Ricken de Medeiros
 */
public class ApplicationPageRelatedMenusFilterBuilder implements FilterBuilder {

    private SelectRange range;
    private long applicationPageId;

    public ApplicationPageRelatedMenusFilterBuilder(SelectRange range, long applicationPageId) {
        this.range = range;
        this.applicationPageId = applicationPageId;
    }

    @Override
    public QueryOptions buildQueryOptions() {
        SApplicationMenuBuilderFactoryImpl factory = new SApplicationMenuBuilderFactoryImpl();
        List<OrderByOption> orderByOptions = Collections.singletonList(new OrderByOption(SApplicationMenu.class, factory.getIdKey(), OrderByType.ASC));
        List<FilterOption> filters = Collections.singletonList(new FilterOption(SApplicationMenu.class, factory.getApplicationPageIdKey(), applicationPageId));
        return new QueryOptions(range.getStartIndex(), range.getMaxResults(), orderByOptions, filters, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationPageRelatedMenusFilterBuilder)) return false;

        ApplicationPageRelatedMenusFilterBuilder that = (ApplicationPageRelatedMenusFilterBuilder) o;

        if (applicationPageId != that.applicationPageId) return false;
        if (range != null ? !range.equals(that.range) : that.range != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = range != null ? range.hashCode() : 0;
        result = 31 * result + (int) (applicationPageId ^ (applicationPageId >>> 32));
        return result;
    }

}