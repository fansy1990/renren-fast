package io.renren.modules.datasources.service.impl;

import io.renren.common.utils.DBUtils;
import io.renren.common.utils.R;
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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/1/30 下午10:27.
 */
@Service("dbService")
public class DBServiceImpl implements DBService {
    private static Logger log = LoggerFactory.getLogger(DBServiceImpl.class);
    private static String COMMA = ",";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void execute(String sql) {
        log.info("SQL:\n{}", sql);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void drop(String table) {
        execute("drop table  if exists " + table);
    }

    @Override
    public void create(String tableName, List<SimpleColumn> columnList) {
        drop(tableName);// 先删除
        execute(constructSQL(tableName, columnList));

    }


    @Override
    public void insertBatch(DataSourceEntity dataSourceEntity) throws Exception {
        // 1. prepare sql
        StringBuilder sql = new StringBuilder();
        sql.append(" insert into " + dataSourceEntity.getRealName() + "(");
        for (SimpleColumn column : dataSourceEntity.getColumnList()) {
            sql.append(column.getColName()).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" ) values( ");
        for (int i = 0; i < dataSourceEntity.getColumnList().size(); i++) {
            sql.append("?").append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");


        // 2. prepare data

        final List<String> lines = getLines(dataSourceEntity);

        final String splitter = getSplitter(dataSourceEntity);

        final List<SimpleColumn> columnList = dataSourceEntity.getColumnList();
        // 3. import data
        jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String line = lines.get(i);
                if (line == null || line.length() < 1) {
                    return;
                }
                String[] lineData = line.split(splitter, -1);
                for (int j = 0; j < lineData.length; j++) {
                    setData(j + 1, ps, lineData[j], columnList.get(j));
                }
            }

            private void setData(int j, PreparedStatement ps, String lineDatum, SimpleColumn simpleColumn) throws SQLException {
                switch (simpleColumn.getColType()) {
                    case VARCHAR:
                        ps.setString(j, lineDatum);
                        break;
                    case DOUBLE:
                        ps.setDouble(j, Double.parseDouble(lineDatum));
                        break;
                    case INT:
                        ps.setInt(j, Integer.parseInt(lineDatum));
                        break;
                    default:
                        log.error("column:{} with wrong column type:{}",
                                new Object[]{simpleColumn.getColName(), simpleColumn.getColType()});
                }
            }

            @Override
            public int getBatchSize() {
                return lines.size();
            }
        });
        // 4. delete file
        if(dataSourceEntity.getType() == 1) {// 文本删除临时文件
            String realFilePath = redisUtils.get(DigestUtils.md5Hex(
                    ShiroUtils.getUserEntity().getUserId() + dataSourceEntity.getFilePath() +
                            dataSourceEntity.getName()));
            FileUtils.deleteQuietly(new File(realFilePath));
        }

    }

    /**
     * 获取分隔符
     * @param dataSourceEntity
     * @return
     */
    private String getSplitter(DataSourceEntity dataSourceEntity) {
        if(dataSourceEntity.getType()==1){
            return dataSourceEntity.getSplitter();
        }
        return COMMA;// RDBMS数据源，则返回逗号分隔符
    }

    /**
     * 获取数据，使用分隔符进行分割
     * @param dataSourceEntity
     * @return
     * @throws IOException
     */
    private List<String> getLines(DataSourceEntity dataSourceEntity) throws Exception {
        if(dataSourceEntity.getType() == 1) {
            String realFilePath = redisUtils.get(DigestUtils.md5Hex(
                    ShiroUtils.getUserEntity().getUserId() + dataSourceEntity.getFilePath() +
                            dataSourceEntity.getName()));
            List<String> lines = FileUtils.readLines(new File(realFilePath), Charset.forName("utf-8"));
            return lines;
        }else{
            //RDBMS 数据源
            Connection conn = DBUtils.getConn(dataSourceEntity);
            if(conn == null) {
                throw new SQLException("数据库连接异常，请检查!");
            }

            PreparedStatement stmt = null;
            ResultSet rs =null ;
            List<String> lines = new ArrayList<>();
            try {
                stmt = conn.prepareStatement(dataSourceEntity.getQuery());
                rs = stmt.executeQuery(dataSourceEntity.getQuery());
                ResultSetMetaData data = rs.getMetaData();
//                final int columnSize = data.getColumnCount();
                StringBuilder builder = null;
                while(rs.next()){
                     builder = new StringBuilder();
                    for(int i=1;i< data.getColumnCount();i++){
                        builder.append(rs.getObject(i).toString()).append(COMMA);
                    }
                    lines.add(builder.toString()+
                            rs.getObject(data.getColumnCount()).toString());
                }

            }catch (Exception e){
                log.warn("获取查询:{},数据异常!",dataSourceEntity.getQuery());
                throw new SQLException("获取列结构异常!");
            }finally{
                if(rs!=null){
                    rs.close();
                }
                if(stmt != null){
                    stmt.close();
                }
                if(conn != null){
                    conn.close();
                }
            }
            return lines;
        }

    }


    /**
     * 创建表SQL
     *
     * @param realTableName
     * @param columnList
     * @return
     */

    private String constructSQL(String realTableName, List<SimpleColumn> columnList) {
        StringBuilder builder = new StringBuilder();

        builder.append(" create table ").append(realTableName).append(" ").append("( ");

        for (SimpleColumn column : columnList) {
            switch (column.getColType()) {
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
                    log.error("错误的类型：{}", column);
            }
        }
        builder.deleteCharAt(builder.length() - 1);// 去掉最后一个逗号
        builder.append(" )");
        return builder.toString();
    }
}
