package org.diploma.app.model.db.entity.projection;

public interface PostsStatistics {

    int getPostsCount();

    int getViewsCount();

    long getFirstPublication();
}
