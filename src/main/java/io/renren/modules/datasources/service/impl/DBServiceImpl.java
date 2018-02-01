package io.renren.modules.datasources.service.impl;

import io.renren.common.utils.RedisUtils;
import io.renren.common.utils.ShiroUtils;
import io.renren.modules.datasources.model.DataSourceEntity;
import io.renren.modules.datasources.model.SimpleColumn;
import io.renren.modules.datasources.service.DBService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    @Autowired
    private RedisUtils redisUtils;
    @Override
    public void execute(String sql) {
        log.info("SQL:\n{}",sql);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void drop(String table) {
        execute("drop table  if exists "+table );
    }

    @Override
    public void create(String tableName, List<SimpleColumn> columnList) {
        drop(tableName);// 先删除
        execute(constructSQL(tableName,columnList));

    }

    @Override
    public void insertBatchCSV(DataSourceEntity dataSourceEntity) throws IOException {
        // 1. prepare sql
        StringBuilder sql =new StringBuilder();
        sql.append(" insert into "+ dataSourceEntity.getRealName()+"(");
        for(SimpleColumn column: dataSourceEntity.getColumnList()){
            sql.append(column.getColName()).append(",");
        }
        sql.deleteCharAt(sql.length()-1);
        sql.append(" ) values( ");
        for(int i =0 ;i< dataSourceEntity.getColumnList().size();i++){
            sql.append("?").append(",");
        }
        sql.deleteCharAt(sql.length()-1);
        sql.append(")");
        // 2. prepare data
        String realFilePath = redisUtils.get(DigestUtils.md5Hex(
                ShiroUtils.getUserEntity().getUserId() + dataSourceEntity.getFilePath()+
        dataSourceEntity.getName()));
        final List<String> lines = FileUtils.readLines(new File(realFilePath), Charset.forName("utf-8"));
        final String splitter = dataSourceEntity.getSplitter();
        final List<SimpleColumn> columnList = dataSourceEntity.getColumnList();
        // 3. import data
        jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String line = lines.get(i);
                if(line == null || line.length() < 1){
                    return ;
                }
                String[] lineData= line.split(splitter,-1);
                for(int j=0 ;j < lineData.length;j++){
                    setData(j+1,ps,lineData[j],columnList.get(j));
                }
//                ps.setLong(1, customer.getCustId());
//                ps.setString(2, customer.getName());
//                ps.setInt(3, customer.getAge() );
            }

            private void setData(int j,PreparedStatement ps, String lineDatum, SimpleColumn simpleColumn) throws SQLException {
                switch (simpleColumn.getColType()){
                    case VARCHAR:
                        ps.setString(j,lineDatum);
                        break;
                    case DOUBLE:
                        ps.setDouble(j,Double.parseDouble(lineDatum));
                        break;
                    case INT:
                        ps.setInt(j,Integer.parseInt(lineDatum));
                        break;
                    default:
                        log.error("column:{} with wrong column type:{}",
                                new Object[]{simpleColumn.getColName(),simpleColumn.getColType()});
                }
            }

            @Override
            public int getBatchSize() {
                return lines.size();
            }
        });
        // 4. delete file
        FileUtils.deleteQuietly(new File(realFilePath));
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
