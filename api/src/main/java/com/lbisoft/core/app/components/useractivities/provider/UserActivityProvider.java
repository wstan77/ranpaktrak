package com.lbisoft.core.app.components.useractivities.provider;

import org.springframework.stereotype.Component;

import com.lbisoft.core.app.db.model.generated.UserActivityDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;
import com.lbisoft.core.framework.useractivities.UserActivity;

import ma.glasnost.orika.metadata.ClassMapBuilder;

/**
 * The UserActivityProvider class provides logic to map fields
 * between instances of {@link EeUserActivityDb} and {@link UserActivity}
 */
@Component
public class UserActivityProvider extends AbstractProvider<UserActivityDb, UserActivity> {

    /**
     * Defines mappings between fields of the two models that are not exactly the
     * same
     */
    @Override
    protected void defineClassMap(ClassMapBuilder<UserActivityDb, UserActivity> classMapBuilder) {
        classMapBuilder.field("activityLk", "type.activityLk");
        classMapBuilder.field("relatedDate", "relatedDateAsString");
    }

    /**
     * Defines child relationships for UserActivity
     */
    @Override
    protected void defineChildren(ChildrenMap<UserActivity> childrenMap) {
    }
}
