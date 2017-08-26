package cn.wssgyyg.myorm.pool;

import cn.wssgyyg.myorm.core.DBManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBConnPool {
    /**
     * 连接池对象
     */
    private static List<Connection> pool;

    /**
     * 最大连接数
     */
    private static final int POOL_MAX_SIZE = DBManager.getConf().getPoolMaxSize();

    /**
     * 最小连接数
     */
    private static final int POOL_MIN_SIZE = DBManager.getConf().getPoolMinSize();

    /**
     * 初始化连接池，使池中的连接数达到最小值
     */
    public void initPool() {
        if (pool == null) {
            pool = new ArrayList<Connection>();
        }

        while (pool.size() < DBConnPool.POOL_MIN_SIZE) {
            pool.add(DBManager.createConn());
            System.out.println("初始化池，池中连接数" + pool.size());
        }
    }

    /**
     * Constructor
     */
    public DBConnPool() {
        initPool();
    }

    /**
     * 从连接池中取出一个连接
     *
     * @return
     */
    public synchronized Connection getConnection() {
        int lastIndex = pool.size() - 1;
        Connection connection = pool.get(lastIndex);
        pool.remove(lastIndex);
        return connection;
    }

    /**
     * 将连接放回池中
     *
     * @param connection
     */
    public synchronized void close(Connection connection) {
        if (pool.size() >= POOL_MAX_SIZE) {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            pool.add(connection);
        }
    }

    public int getSize(){
        return pool.size();
    }
}
