package cn.wssgyyg.myorm.utils;

import cn.wssgyyg.myorm.bean.TableInfo;
import cn.wssgyyg.myorm.core.TableContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 分装了反射常用的操作
 */
public class ReflectUtils {

    /**
     * 调用Object对象的fieldName属性的get方法
     * @param object 对象
     * @param fieldName 属性
     * @return get方法返回值
     */
    public static Object invokeGet(Object object, String fieldName) {
        Class c = object.getClass();
        try {
            String getMethod = "get" + StringUtils.firstChar2UpperCase(fieldName);
            Method m = c.getDeclaredMethod(getMethod, null);
            Object priKeyValue = m.invoke(object, null);

            return priKeyValue;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void invokeSet(Object object, String fieldName, Object fieldValue) {
        //TODO 当fieldValue为null时，该方法会报错，待解决
        Class clazz = object.getClass();
        Method m = null;
        try {
            m = clazz.getDeclaredMethod("set" + StringUtils.firstChar2UpperCase(fieldName), fieldValue.getClass());
            m.invoke(object, fieldValue);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

}
