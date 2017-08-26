package cn.wssgyyg.test;

import cn.wssgyyg.myorm.core.DBManager;
import cn.wssgyyg.myorm.core.Query;
import cn.wssgyyg.myorm.core.QueryFactory;
import cn.wssgyyg.vo.EmpVO;

import java.util.List;

/**
 * 测试连接池的调用效率
 */
public class Test2 {
    public static void main(String[] args) {
        long a = System.currentTimeMillis();
        for (int i = 0; i < 3000; i++) {
            test01();
        }
        long b = System.currentTimeMillis();
        //不加连接池：17367ms
        //加连接池：3905ms
        System.out.println(b - a);
    }

    public static void test01(){
        Query q = QueryFactory.getFactory().createQuery();

        String sql = "select emp.id, emp.empname, salary+bonus 'totalSalary', age, dept.dname 'deptName', dept.address 'deptAddr' from emp, dept where emp.deptid = dept.id";
        List<EmpVO> list = q.queryRows(sql, EmpVO.class, null);
        for (EmpVO empVO : list) {
            //System.out.println(empVO.getEmpname() + " -> " + empVO.getDeptName() + " -> " + empVO.getTotalSalary());
        }
    }

}
