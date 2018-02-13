package io.renren.common.utils;

import io.renren.modules.datasources.model.DataSourceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * DB 工具类
 *
 * @author fanzhe
 * @email fansy1990@foxmail.com
 * @date 2018/2/13 上午8:18.
 */
public class DBUtils {
    private static Logger log = LoggerFactory.getLogger(DBUtils.class);


    public static Connection getConn(DataSourceEntity dataSourceEntity) {

        try {
            Class.forName(dataSourceEntity.getDriver());
            return DriverManager.getConnection(dataSourceEntity.getUrl(),
                    dataSourceEntity.getUser(), dataSourceEntity.getPassword());
        } catch (Exception e) {
//				e.printStackTrace();
            log.warn("数据库连接异常：{}", e.getMessage());
        }

        return null;
    }

    public static ResultSet getResultSet(Connection conn, String query) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
