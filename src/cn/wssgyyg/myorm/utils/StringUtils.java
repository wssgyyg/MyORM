package cn.wssgyyg.myorm.utils;

/**
 * 分装了字符串常用的操作
 */
public class StringUtils {

    public static String firstChar2UpperCase(String string) {
        string.toUpperCase();
        return string.toUpperCase().substring(0, 1) + string.substring(1);
    }

}
