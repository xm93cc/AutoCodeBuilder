package com.xgt.domain;/*
*******
QDebug
*******
*******
2020/1/10 16:10
*******
*/
import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * @author QDebug
 */
public class AutoCodePojoBuilder {
    private static String property = System.getProperty("user.dir")+"\\src\\main\\java\\"+AutoCodePojoBuilder.class.getPackage().getName().replace('.','\\')+"\\";


    /**
     * get tables
     *
     * @param driverClassPath 驱动包
     * @param jdbcUrl         连接url
     * @param username        用户名
     * @param password        密码
     * @return DataSource
     */
    public static void getTableInfos(String driverClassPath, String jdbcUrl, String username, String password,boolean isHump,String pojoDirName) throws Exception {
        if (driverClassPath == null || jdbcUrl == null || username == null || password == null) {
            throw new RuntimeException("jdbc pram Missing!");
        }
        //加载MYSQL JDBC驱动程序
        Class.forName(driverClassPath);
        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        Statement statement = conn.createStatement();
        ResultSet show_tables = statement.executeQuery("show tables");
        ResultSetMetaData metaData = show_tables.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<String> ta = new ArrayList();
        //得到所有表信息
        while (show_tables.next()) {

            for (int i = 1; i <= columnCount; i++) {
                ta.add((String) show_tables.getObject(i));
            }

        }


        for (String desc : ta) {
            String sql = "desc " + desc;
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData data = resultSet.getMetaData();
            if (data==null)
            {
                System.out.println("table name:"+desc+" error!");
                continue;
            }
            File file = new File(pojoDirName);
            if(file.isDirectory())
            {
                String[] className = desc.split("_");
                StringBuffer newt=new StringBuffer();
                reName(className, newt);
                newt.append(".java");
                File classFile=new File(file.getPath()+newt.toString());


            }
            StringBuffer sb = new StringBuffer();
            while (resultSet.next()) {
                String type = resultSet.getString("Type");
                Map<String,String> typeMap=getDataBaseDataType();
                //TINYINT, SMALLINT, MEDIUMINT, INT, BIGINT
                Set<String> strings = typeMap.keySet();
                for (String string : strings) {
                    if(type.contains(string))
                    {
                        toCode(isHump, resultSet, sb,typeMap.get(string));
                    }

                }


            }

            System.out.println(sb.toString());


        }


    }

    private static void reName(String[] className, StringBuffer newt) {
        for (int i = 1; i < className.length; i++) {
            char[] chars = className[i].toCharArray();
            if ((((int) chars[0]) - 32) <= 90 && (((int) chars[0]) - 32) >= 65) {
                chars[0] = (char) (((int) chars[0]) - 32);
            }
            newt.append(new String(chars));
        }
    }

    private static Map<String, String> getDataBaseDataType() {
        Map<String, String> typeMap=new HashMap<String, String>(6);
        typeMap.put("int","Integer");
        typeMap.put("tinyint","Integer");
        typeMap.put("smallint","Integer");
        typeMap.put("mediumint","Integer");
        typeMap.put("bigint","Integer");
        typeMap.put("float","Float");
        typeMap.put("double","Double");
        typeMap.put("decimal", "BigDecimal");
        typeMap.put("varchar", "String");
        typeMap.put("char", "String");
        typeMap.put("datetime", "Date");
        typeMap.put("timestamp", "Date");
        return typeMap;
    }

    private static void toCode(boolean isHump, ResultSet resultSet, StringBuffer sb,String dataType) throws SQLException {
        String field = resultSet.getString("Field");
        String[] s = field.split("_");
        if (isHump)
        {

            if(s.length==1)
            {
                sb.append("private "+dataType +s[0]+";\n");
            }else
            {
                StringBuffer newt=new StringBuffer(s[0]);
                reName(s, newt);
                sb.append("private "+dataType +"  "+newt.toString()+";\n");
            }


        }else
        {
            sb.append("private "+dataType +"  "+s[0]+";\n");
        }
    }





    public static void main(String[] args) throws Exception {
     getTableInfos(
             "com.mysql.jdbc.Driver",
             "jdbc:mysql://localhost:3306/health?characterEncoding=UTF-8",
             "root",
             "123",
             true,
             "pojo");
    }

}
