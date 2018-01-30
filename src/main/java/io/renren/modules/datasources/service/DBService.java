package io.renren.modules.datasources.service;

import io.renren.modules.datasources.model.SimpleColumn;

import java.util.List;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/30 下午10:26.
 */
public interface DBService {
    public void execute(String sql);

    public void drop(String table);

    public void create(String tableName, List<SimpleColumn> columnList);
}
