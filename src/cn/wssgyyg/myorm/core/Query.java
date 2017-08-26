package cn.wssgyyg.myorm.core;

import cn.wssgyyg.myorm.bean.ColumnInfo;
import cn.wssgyyg.myorm.bean.TableInfo;
import cn.wssgyyg.myorm.utils.JDBCUtils;
import cn.wssgyyg.myorm.utils.ReflectUtils;
import cn.wssgyyg.po.Emp;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责查询，对外提供服务的核心类
 *
 * @author wssgyyg
 */
public abstract class Query implements Cloneable{

    /**
     * 采用模板方法模式将JDBC操作封装成模板，便于重用
     * @param sql sql语句
     * @param params sql的参数
     * @param clazz 记录要封装到的bean
     * @param callBack CallBack的实现类，实现回调
     * @return
     */
    public Object excuteQueryTemplate(String sql, Object[] params, Class clazz, CallBack callBack) {

        Connection conn = DBManager.getConn();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            JDBCUtils.handleParams(ps, params);
            return callBack.doExecute(conn, ps, rs);


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DBManager.close(ps, conn);
        }
    }

    /**
     * 直接执行一个DML语句
     *
     * @param sql    sql语句
     * @param params 参数
     * @return 执行sql语句后影响记录的行数
     */

    public int excuteDML(String sql, Object[] params) {
        Connection connection = DBManager.getConn();
        int count = 0;

        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);

            JDBCUtils.handleParams(ps, params);

            count = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.close(ps, connection);
        }
        return 0;
    }

    /**
     * 将一个对象存储到数据库中
     *
     * @param obj 需要存储的对象
     */
    void insert(Object obj) {
        Class clazz = obj.getClass();
        //存储参数
        List<Object> params = new ArrayList<>();
        TableInfo ti = TableContext.poClassTableMap.get(clazz);
        StringBuilder sql = new StringBuilder("insert into " + ti.getTname() + " (");
        Field[] fs = clazz.getDeclaredFields();
        //记录值不为空的变量
        int countFieldNotNull = 0;
        for (Field field : fs) {
            String fieldName = field.getName();
            Object fieldValue = ReflectUtils.invokeGet(obj, fieldName);

            if (fieldValue != null) {
                countFieldNotNull++;
                sql.append(fieldName + ",");
                params.add(fieldValue);
            }

        }

        sql.setCharAt(sql.length() - 1, ')');
        sql.append(" values (");
        for (int i = 0; i < countFieldNotNull; i++) {
            sql.append("?,");
        }
        sql.setCharAt(sql.length() - 1, ')');

        excuteDML(sql.toString(), params.toArray());
    }

    /**
     * 删除clazz表示类对应的表中的记录
     *
     * @param clazz 跟表对应的类的Class对象
     * @param id    主键的值
     */
    void delete(Class clazz, Object id) {
        //Emp.class, 2 --> delete from Emp where id = 2;

        //通过Class对象找TableInfo
        TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);

        ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();

        String sql = "delete from " + tableInfo.getTname() + " where " + onlyPriKey.getName() + "=? ";

        excuteDML(sql, new Object[]{id});
    }

    /**
     * 删除对象在数据库中对应的记录（对象所在的类对应到表，对象的主键的值对应到记录）
     *
     * @param object
     */
    void delete(Object object) {
        Class c = object.getClass();
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();  //主键

        Object priKeyValue = ReflectUtils.invokeGet(object, onlyPriKey.getName());
        delete(c, priKeyValue);
    }

    /**
     * 更新对象对应的巨鹿，并且只更新指定的字段的值
     *
     * @param obj        所要更新的对象
     * @param fieldNames 更新的属性列表
     * @return 执行sql语句后影响记录的行数
     */
    public int update(Object obj, String[] fieldNames) {
        //obj("uname", "pwd") --> update 表名 set uname = ? pwd = ? where id = ?;
        Class clazz = obj.getClass();
        //存储参数
        List<Object> params = new ArrayList<>();
        TableInfo ti = TableContext.poClassTableMap.get(clazz);
        StringBuilder sql = new StringBuilder("update " + ti.getTname() + " set ");
        ColumnInfo priKey = ti.getOnlyPriKey();
        for (String fname : fieldNames) {
            Object fvalue = ReflectUtils.invokeGet(obj, fname);
            params.add(fvalue);
            sql.append(fname + " = ?,");
        }
        sql.setCharAt(sql.length() - 1, ' ');
        sql.append("where ");
        sql.append(priKey.getName() + "=?");
        params.add(ReflectUtils.invokeGet(obj, priKey.getName()));
        return excuteDML(sql.toString(), params.toArray());
    }

    /**
     * 查询返回多行记录，并且将每行记录封装到clazz指定的类的对象中。
     *
     * @param sql    查询语句
     * @param clazz  封装数据的javabean类的Class对象
     * @param params sql的参数
     * @return 查询返回的结果
     */
    public List queryRows(String sql, Class clazz, Object[] params) {

        return (List)excuteQueryTemplate(sql, params, clazz, new CallBack() {
            @Override
            public Object doExecute(Connection connection, PreparedStatement ps, ResultSet resultSet) {
                List list = null;
                try {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    while (resultSet.next()) {
                        if (list == null) {
                            list = new ArrayList();
                        }
                        Object rowObj = clazz.newInstance();


                        for (int i = 0; i < metaData.getColumnCount(); i++) {
                            String columnName = metaData.getColumnLabel(i + 1);
                            Object columnValue = resultSet.getObject(i + 1) == null ? new Object() : resultSet.getObject(i + 1);

                            //调用rowObj对象的setUserName方法，将columnValue的值设置进去
                            ReflectUtils.invokeSet(rowObj, columnName, columnValue);
                        }

                        list.add(rowObj);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return list;
            }
        });
    }

    /**
     * 查询返回多行记录，并且将每行记录封装到clazz指定的类的对象中。
     *
     * @param sql    查询语句
     * @param clazz  封装数据的javabean类的Class对象
     * @param params sql的参数
     * @return 查询返回的结果
     */

    public Object queryUniqueRow(String sql, Class clazz, Object[] params) {
        List list = queryRows(sql, clazz, params);

        return (list == null || list.size() == 0) ? null : list.get(0);

    }

    /**
     * 查询返回一个值（一行一列），并将该值返回
     *
     * @param sql    查询语句
     * @param params sql的参数
     * @return 查询到的值
     */
    public Object queryValue(String sql, Object[] params) {

        return excuteQueryTemplate(sql, params, null, new CallBack() {
            @Override
            public Object doExecute(Connection connection, PreparedStatement ps, ResultSet resultSet) {
                Object value = null;
                try {
                    while (resultSet.next()) {
                        value = resultSet.getObject(1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return value;
            }
        });

    }

    /**
     * 查询返回一个数字（一行一列），并将该值返回
     *
     * @param sql    查询语句
     * @param params sql的参数
     * @return 查询到的数字
     */
    Number queryNumber(String sql, Object[] params) {
        Number number = (Number) queryValue(sql, params);
        return number;
    }

    /**
     * 分页查询
     *
     * @param pageNum 第几页数据
     * @param size    每页显示多少数据
     * @return
     */
    public abstract Object QueryPagenate(int pageNum, int size);

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
