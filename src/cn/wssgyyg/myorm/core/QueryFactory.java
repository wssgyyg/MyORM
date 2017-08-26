package cn.wssgyyg.myorm.core;

/**
 * 创建Query对象的工厂类
 */
public class QueryFactory {

    private static QueryFactory factory = new QueryFactory();
    private static Query prototypeObj;
    static {
        try {
            String clazzString =  DBManager.getConf().getQueryClass();
            Class clazz = Class.forName(clazzString);
            prototypeObj = (Query) clazz.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QueryFactory() {

    }

    public Query createQuery(){
        try {
            return (Query)prototypeObj.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static QueryFactory getFactory() {
        return factory;
    }
}
