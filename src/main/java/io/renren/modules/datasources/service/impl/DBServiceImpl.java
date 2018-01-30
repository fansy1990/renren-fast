package io.renren.modules.datasources.service.impl;

import io.renren.modules.datasources.model.SimpleColumn;
import io.renren.modules.datasources.service.DBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/30 下午10:27.
 */
@Service("dbService")
public class DBServiceImpl implements DBService {
    private static Logger log = LoggerFactory.getLogger(DBServiceImpl.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public void execute(String sql) {
        log.info("SQL:\n{}",sql);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void drop(String table) {
        execute("drop table "+table +" if exists ");
    }

    @Override
    public void create(String tableName, List<SimpleColumn> columnList) {
        drop(tableName);// 先删除
        execute(constructSQL(tableName,columnList));

    }

    /**
     * 创建表SQL
     * @param realTableName
     * @param columnList
     * @return
     */

    private String constructSQL(String realTableName, List<SimpleColumn> columnList) {
        StringBuilder builder = new StringBuilder();

        builder.append(" create table ").append(realTableName).append(" ").append("( ");

        for(SimpleColumn column:columnList){
            switch (column.getColType()){
                case DOUBLE:
                    builder.append(" ").append(column.getColName()).append(" ").append("double(16,2) ,");
                    break;
                case INT:
                    builder.append(" ").append(column.getColName()).append(" ").append("int(11) ,");
                    break;
                case VARCHAR:
                    builder.append(" ").append(column.getColName()).append(" ").append("varchar(255) ,");
                    break;
                default:
                    log.error("错误的类型：{}",column);
            }
        }
        builder.deleteCharAt(builder.length()-1);// 去掉最后一个逗号
        builder.append(" )");
        return builder.toString();
    }
}
