package org.diploma.app.model.db.entity;

public interface PostStatistics {

    int getPostsCount();

    int getLikesCount();

    int getDislikesCount();

    int getViewsCount();

    long getFirstPublication();
}
