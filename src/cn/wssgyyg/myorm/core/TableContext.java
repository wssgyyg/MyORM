package cn.wssgyyg.myorm.core;

import cn.wssgyyg.myorm.bean.ColumnInfo;
import cn.wssgyyg.myorm.bean.TableInfo;
import cn.wssgyyg.myorm.utils.JavaFileUtils;
import cn.wssgyyg.myorm.utils.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 负责获取管理数据库所有表结构和类结构的关系，并可以根据表结构生成类结构
 */
public class TableContext {

    /**
     * 表名为key，表信息对象为value
     */
    public static Map<String, TableInfo> tables = new HashMap<>();

    /**
     * 将po的Class对象和表信息对象关联起来，便于重用
     */
    public static Map<Class, TableInfo> poClassTableMap = new HashMap<>();

    private TableContext() {
    }

    static {
        try {
            Connection con = DBManager.getConn();
            DatabaseMetaData dbmd = con.getMetaData();
            ResultSet tableRet = dbmd.getTables(null, "%", "%", new String[]{"TABLE"});
            while (tableRet.next()) {
                String tableName = (String) tableRet.getObject("TABLE_NAME");

                TableInfo ti = new TableInfo(tableName, new ArrayList<ColumnInfo>(), new HashMap<String, ColumnInfo>());
                String sql = "select * from " + tableName;
                try {
                    PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();
                    ResultSetMetaData meta = rs.getMetaData();
                    int columeCount = meta.getColumnCount();
                    for (int i = 1; i < columeCount + 1; i++) {
                        ColumnInfo columnInfo = new ColumnInfo(meta.getColumnName(i), meta.getColumnTypeName(i), 0);
                        ti.getColumns().put(columnInfo.getName(), columnInfo);
                    }
                    tables.put(tableName, ti);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                ResultSet rs2 = dbmd.getPrimaryKeys(con.getCatalog(), null, tableName);
                while (rs2.next()) {
                    String priKeyName = rs2.getString("COLUMN_NAME");
                    ColumnInfo ci = tables.get(tableName).getColumns().get(priKeyName);
                    ci.setKeyType(1);

                    tables.get(tableName).getPriKeys().add(ci);
                }

                tables.get(tableName).setOnlyPriKey(tables.get(tableName).getPriKeys().get(0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //更新类结构
        updateJavaPOFile();

        //加载PO包下所有的类，重复使用提高效率
        loadPOTabels();

    }

    //根据表结构更新PO类
    //实现了表结构到PO类的转化
    public static void updateJavaPOFile(){
        Map<String, TableInfo> map = TableContext.tables;
        for (TableInfo t: map.values()) {
            JavaFileUtils.createJavaPOFile(t, new MysqlTypeConverter());
        }
    }

    /**
     * 加载PO包下面的类
     */
    public static void loadPOTabels(){
        for (TableInfo t : tables.values()) {
            try {
                Class clazz = Class.forName(DBManager.getConf().getPoPackage() + "." + StringUtils.firstChar2UpperCase(t.getTname()));
                poClassTableMap.put(clazz, t);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
