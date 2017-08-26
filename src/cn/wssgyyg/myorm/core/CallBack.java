package cn.wssgyyg.myorm.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface CallBack {
    Object doExecute(Connection connection, PreparedStatement ps, ResultSet resultSet);
}
