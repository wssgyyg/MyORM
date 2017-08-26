package cn.wssgyyg.myorm.core;

import cn.wssgyyg.myorm.bean.ColumnInfo;
import cn.wssgyyg.myorm.bean.TableInfo;
import cn.wssgyyg.myorm.utils.JDBCUtils;
import cn.wssgyyg.myorm.utils.ReflectUtils;
import cn.wssgyyg.po.Emp;
import cn.wssgyyg.vo.EmpVO;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Query接口针对MySql数据库的实现
 */
public class MySqlQuery extends Query {


    @Override
    public Object QueryPagenate(int pageNum, int size) {
        return null;
    }

    public static void main(String[] args) {
        String sql = "select * from emp where id > ? ";
        Object[] params = new Object[]{1};
        List<Emp> result = new MySqlQuery().queryRows(sql, Emp.class, params);
        for (Emp emp : result) {
            System.out.println(emp.getEmpname());
        }
    }
}
