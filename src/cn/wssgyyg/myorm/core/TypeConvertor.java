package cn.wssgyyg.myorm.core;

/**
 * 负责java的数据类型和数据库的数据类型的互相转化
 */
public interface TypeConvertor {
    /**
     * 将数据库的数据类型转化成Java的数据类型
     * @param columnType 数据库字段的数据类型
     * @return Java的数据类型
     */
    String databaseType2JavaType(String columnType);

    /**
     * 将Java数据类型转化成数据库数据类型
     * @param javaType Java数据类型
     * @return 返回的数据库类型
     */
    String javaType2DatabaseType(String javaType);
}
