package cn.wssgyyg.myorm.core;

import cn.wssgyyg.myorm.bean.Configuration;
import cn.wssgyyg.myorm.pool.DBConnPool;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * 根据配置信息，维持连接对象的管理（增加连接池功能）
 */
public class DBManager {

    /**
     * 配置信息
     */
    private static Configuration conf;

    /**
     * 连接池对象
     */
    private static DBConnPool pool;

    /**
     * 类初始化时执行
     */
    static{
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        conf = new Configuration();
        conf.setDriver(properties.getProperty("driver"));
        conf.setPoPackage(properties.getProperty("poPackage"));
        conf.setPwd(properties.getProperty("pwd"));
        conf.setUrl(properties.getProperty("url"));
        conf.setSrcPath(properties.getProperty("srcPath"));
        conf.setUser(properties.getProperty("user"));
        conf.setUsingDB(properties.getProperty("usingDB"));
        conf.setQueryClass(properties.getProperty("queryClass"));
        conf.setPoolMaxSize((Integer.parseInt(properties.getProperty("poolMaxSize"))));
        conf.setPoolMinSize((Integer.parseInt(properties.getProperty("poolMinSize"))));

       System.out.println(TableContext.class);
    }

    /**
     * 创建新的Connection对象
     * @return 创建的Connection对象
     */
    public static Connection createConn(){
        try {
            Class.forName(conf.getDriver());
            //TODO 目前直接建立连接，后期加连接池提高效率
            Connection connection = DriverManager.getConnection(conf.getUrl(), conf.getUser(), conf.getPwd());
            return connection;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得Connection对象
     * @return 创建的Connection对象
     */
    public static Connection getConn(){
        if (pool == null){
            pool = new DBConnPool();
        }
        return pool.getConnection();
    }

    /**
     * 返回configure对象
     * @return
     */
    public static Configuration getConf(){
        return conf;
    }

    /**
     * 关闭传入的ResultSet, Statement, Connection对象
     * @param rs ResultSet对象
     * @param stat Statement对象
     * @param connection Connection对象
     */
    public static void close(ResultSet rs, Statement stat, Connection connection) {
        if (rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (stat != null){
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        pool.close(connection);
    }

    /**
     * 关闭传入的Statement Connection对象
     * @param stat Statement对象
     * @param connection Connection对象
     */
    public static void close(Statement stat, Connection connection) {

        if (stat != null){
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        pool.close(connection);

    }

}
