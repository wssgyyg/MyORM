package cn.wssgyyg.myorm.utils;

import java.sql.*;

/**
 * 分装了JDBC查询常用的操作
 */
public class JDBCUtils {

    /**
     * 给sql设参
     * @param statement preparedstatement对象
     * @param params 参数数组
     */
    public static void handleParams(PreparedStatement statement, Object[] params) {
        if (params != null){
            for (int i = 0; i < params.length; i++) {
                try {
                    statement.setObject(i + 1, params[i]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
