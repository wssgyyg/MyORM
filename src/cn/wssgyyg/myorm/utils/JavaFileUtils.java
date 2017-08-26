package cn.wssgyyg.myorm.utils;

import cn.wssgyyg.myorm.bean.ColumnInfo;
import cn.wssgyyg.myorm.bean.JavaFieldGetSet;
import cn.wssgyyg.myorm.bean.TableInfo;
import cn.wssgyyg.myorm.core.DBManager;
import cn.wssgyyg.myorm.core.MysqlTypeConverter;
import cn.wssgyyg.myorm.core.TableContext;
import cn.wssgyyg.myorm.core.TypeConvertor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装了生成Java文件（source code）常用操作
 */
public class JavaFileUtils {

    /**
     * 根据字段信息生成Java属性信息，如varchar username --> private String username,
     * 以及相应的set get方法
     *
     * @param columnInfo 字段信息
     * @param convertor  类型转换器
     * @return java属性和setget方法源码
     */
    public static JavaFieldGetSet createFieldGetSetSrc(ColumnInfo columnInfo, TypeConvertor convertor) {
        JavaFieldGetSet jfgs = new JavaFieldGetSet();
        String javaFieldType = convertor.databaseType2JavaType(columnInfo.getDataType());
        jfgs.setFieldInfo("\tprivate " + javaFieldType + " " + columnInfo.getName() + ";\n");

        //public String getUserName(){return userName;}
        //generate get method;
        StringBuilder getSrc = new StringBuilder();
        getSrc.append("\tpublic " + javaFieldType + " get" + StringUtils.firstChar2UpperCase(columnInfo.getName()) + "(){\n");
        getSrc.append("\t\treturn " + columnInfo.getName() + ";\n");
        getSrc.append("\t}\n");
        jfgs.setGetInfo(getSrc.toString());

        //publi
        //generate set method;
        StringBuilder setSrc = new StringBuilder();
        setSrc.append("\tpublic void set" + StringUtils.firstChar2UpperCase(columnInfo.getName()) + "(");
        setSrc.append(javaFieldType + " " + columnInfo.getName() + "){\n");
        setSrc.append("\t\tthis." + columnInfo.getName() + " = " + columnInfo.getName() + ";\n");
        setSrc.append("\t}\n");
        jfgs.setSetInfo(setSrc.toString());

        return jfgs;
    }

    /**
     * 根据表信息生成java类的源代码
     *
     * @param tableInfo 表信息
     * @param convertor 数据类型转化器
     * @return
     */
    public static String createJavaSrc(TableInfo tableInfo, TypeConvertor convertor) {
        StringBuilder src = new StringBuilder();

        Map<String, ColumnInfo> columns = tableInfo.getColumns();
        List<JavaFieldGetSet> javaFields = new ArrayList<>();

        for (ColumnInfo c : columns.values()) {
            javaFields.add(createFieldGetSetSrc(c, convertor));
        }

        //生成package语句
        src.append("package " + DBManager.getConf().getPoPackage() + ";\n\n");

        //生成import语句
        src.append("import java.sql.*;\n");
        src.append("import java.util.*;\n\n");

        //生成类声明语句
        src.append("public class " + StringUtils.firstChar2UpperCase(tableInfo.getTname()) + " {\n\n");

        //生成属性列表
        for (JavaFieldGetSet jfgs : javaFields) {
            src.append(jfgs.getFieldInfo());
        }
        //生成get方法列表
        for (JavaFieldGetSet jfgs : javaFields) {
            src.append(jfgs.getGetInfo());
        }
        //生成set方法列表
        for (JavaFieldGetSet jfgs : javaFields) {
            src.append(jfgs.getSetInfo());
        }
        //生成类结束
        src.append("\n}\n");
        return src.toString();
    }

    public static void createJavaPOFile(TableInfo tableInfo, TypeConvertor convertor) {
        String src = createJavaSrc(tableInfo, convertor);

        String srcPath = DBManager.getConf().getSrcPath() + "/";
        String packagePath = DBManager.getConf().getPoPackage().replaceAll("\\.", "/");
        File file = new File(srcPath + packagePath);
        if (!file.exists()){
            file.mkdirs();
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file.getAbsolutePath() + "/" + StringUtils.firstChar2UpperCase(tableInfo.getTname()) + ".java"));
            bw.write(src);
            bw.flush();
            System.out.println("建立表" + tableInfo.getTname() +
                    "对应的Java类: " + StringUtils.firstChar2UpperCase(tableInfo.getTname()) + ".java");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                bw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }


    }

}
