package cn.wssgyyg.test;

import cn.wssgyyg.myorm.core.Query;
import cn.wssgyyg.myorm.core.QueryFactory;
import cn.wssgyyg.vo.EmpVO;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * 客户端调用的测试类
 */
public class Test {
    public static void main(String[] args) {
        Query q = QueryFactory.getFactory().createQuery();

        String sql = "select emp.id, emp.empname, salary+bonus 'totalSalary', age, dept.dname 'deptName', dept.address 'deptAddr' from emp, dept where emp.deptid = dept.id";
        List<EmpVO> list = q.queryRows(sql, EmpVO.class, null);
        for (EmpVO empVO : list) {
            System.out.println(empVO.getEmpname() + " -> " + empVO.getDeptName() + " -> " + empVO.getTotalSalary());
        }
    }

}
