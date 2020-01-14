package com.xgt.domain;/*
*******
QDebug
*******
*******
2020/1/10 16:10
*******
*/

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.*;
import java.util.zip.Deflater;

/**
 * @author QDebug
 */
public class AutoCodePojoBuilder {


    /**
     * create tables map pojo class
     * @param driverClassPath databases driver
     * @param jdbcUrl   jdbc url
     * @param username database username
     * @param password database password
     * @param isHump code name is hump
     * @param pojoPackageName  pojo Package Name
     * @param isLomBox is lom box style
     * @throws Exception
     */
    public static void getTableInfos(String driverClassPath, String jdbcUrl, String username, String password, boolean isHump, String pojoPackageName, boolean isLomBox, String user) throws Exception {
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
            Map<String, String> stringStringMap = new HashMap<String, String>(6);
            StringBuffer classContent = new StringBuffer();
            StringBuffer getSet = new StringBuffer();
            StringBuffer classHead = new StringBuffer(AutoCodePojoBuilder.class.getPackage() + "." + pojoPackageName + ";\n");
            if (isLomBox) {
                if(user==null)
                {
                    user="admin";
                }
                classContent = new StringBuffer("\nimport lombok.Data;\n /**\n * @author "+user+"\n */\n@Data");
                System.out.println(classContent.toString());
            }/*else
            {
                classContent=new StringBuffer(AutoCodePojoBuilder.class.getPackage()+"."+pojoDirName+";\n");
            }*/
            String database = (jdbcUrl.split("/")[3]).split("\\?")[0];
            String sql = "SELECT COLUMN_NAME Field, COLUMN_TYPE Type,DATA_TYPE,IS_NULLABLE,CHARACTER_MAXIMUM_LENGTH , COLUMN_DEFAULT , COLUMN_COMMENT Info FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = '"+database+"' AND  table_name ='"+desc+"';";
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData data = resultSet.getMetaData();
            if (data == null) {
                System.out.println("table name:" + desc + " error!");
                continue;
            }
            File file = new File("./");
            String path = file.getCanonicalPath() + "\\src\\main\\java\\" + AutoCodePojoBuilder.class.getPackage().getName().replace(".", "\\") + "\\" + pojoPackageName;
            File file1 = new File(path);
            File classFile = null;
            if (file1.isDirectory()) {
                classFile = getClassFile(desc, classContent, file1,isLomBox,user);
                if (!classFile.isFile()) {
                    classFile.createNewFile();
                } else {
                    continue;
                }
            } else {
                System.out.println("mk package");
                file1.mkdirs();
                classFile = getClassFile(desc, classContent, file1,isLomBox,user);
                classFile.createNewFile();
            }
            StringBuffer sb = new StringBuffer();
            while (resultSet.next()) {
                String type = resultSet.getString("Type");
                Map<String, String> typeMap = getDataBaseDataType();
                //TINYINT, SMALLINT, MEDIUMINT, INT, BIGINT
                Set<String> strings = typeMap.keySet();
                for (String string : strings) {
                    if (type.contains(string)) {
                        if (typeMap.get(string).equals("BigDecimal")) {
                            classHead.append("\nimport java.math.BigDecimal;");
                        } else if (typeMap.get(string).equals("Date")) {
                            classHead.append("\nimport java.util.Date;");
                        }
                        Map<String, String> stringStringMap1 = toCode(isHump, resultSet, sb, typeMap.get(string), isLomBox);
                        if (stringStringMap1 != null) {

                            stringStringMap.putAll(stringStringMap);
                        }

                        break;
                    }
                }
            }
            classContent.append(sb.toString());
            classHead.append(classContent.toString());
            if (stringStringMap != null) {
                Set<String> strings = stringStringMap.keySet();
                for (String string : strings) {
                    String toUp = toUp(string);
                    getSet.append("\n\tpublic " + stringStringMap.get(string) + " get" + toUp + "()\n \t{ return " + string + ";  }\n" +
                            "\n" +
                            "\tpublic void set" + toUp + "(" + stringStringMap.get(string) + " " + string + ") \n\t{\n" +
                            "        this." + string + "=" + string + ";\n" +
                            "    }\n\n");
                }


            }
            FileOutputStream fos = new FileOutputStream(classFile);
            if (!isLomBox) {

                fos.write(classHead.append(getSet.toString() + "\n}").toString().getBytes());
            } else {

                fos.write(classHead.append("\n}").toString().getBytes());
            }
            fos.close();
        }
    }

    private static String toUp(String to) {
        if (to == null) {
            return null;
        }
        char[] chars1 = to.toCharArray();
        if ((((int) chars1[0]) - 32) <= 90 && (((int) chars1[0]) - 32) >= 65) {
            chars1[0] = (char) (((int) chars1[0]) - 32);
        }
        return new String(chars1);

    }


    private static File getClassFile(String desc, StringBuffer classContent, File file1 ,boolean isLomBok,String user) {
        File classFile;
        String[] className = desc.split("_");
        StringBuffer newt = new StringBuffer();
        reName(className, newt);
        if(isLomBok)
        {
            classContent.append("\npublic class " + newt + "{\n");
        }else
        {
            classContent.append("\n /**\n * @author "+user+"\n */\npublic class " + newt + "{\n");
        }

        newt.append(".java");
        classFile = new File(file1.getPath() + "\\" + newt.toString());
        return classFile;
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
        Map<String, String> typeMap = new HashMap<String, String>(6);
        typeMap.put("int", "Integer");
        typeMap.put("tinyint", "Integer");
        typeMap.put("smallint", "Integer");
        typeMap.put("mediumint", "Integer");
        typeMap.put("bigint", "Integer");
        typeMap.put("float", "Float");
        typeMap.put("double", "Double");
        typeMap.put("decimal", "BigDecimal");
        typeMap.put("varchar", "String");
        typeMap.put("char", "String");
        typeMap.put("datetime", "Date");
        typeMap.put("timestamp", "Date");
        return typeMap;
    }

    private static Map<String, String> toCode(boolean isHump, ResultSet resultSet, StringBuffer sb, String dataType, boolean isLomBox) throws SQLException {
        Map<String, String> map = null;
        if (!isLomBox) {
            map = new HashMap<String, String>(6);
        }
        String field = resultSet.getString("Field");
        String[] s = field.split("_");

        if (isHump) {

            if (s.length == 1) {
                infoNotNull(resultSet, sb);
                sb.append("\tprivate " + dataType + " " + s[0] + ";\n");
                if (map != null) {
                    map.put(s[0], dataType);
                }
            } else if (s.length > 1) {
                infoNotNull(resultSet, sb);
                StringBuffer newt = new StringBuffer(s[0]);
                reName(s, newt);
                sb.append("\tprivate " + dataType + "  " + newt.toString() + ";\n");
                if (map != null) {
                    map.put(newt.toString(), dataType);
                }
            }


        } else {
            infoNotNull(resultSet, sb);
            sb.append("\tprivate " + dataType + "  " + s[0] + ";\n");
            if (map != null) {
                map.put(s[0], dataType);
            }
        }
        return map;
    }

    private static void infoNotNull(ResultSet resultSet, StringBuffer sb) throws SQLException {
        String info = resultSet.getString("Info");
        if (info != null && info.length() > 0) {
            sb.append("\t/**\n" +
                    "\t * " + info + "\n" +
                    "\t */\n");
        }
    }

    public static void main(String[] args) throws Exception {
      getTableInfos(
                "com.mysql.jdbc.Driver",
                "jdbc:mysql://localhost:3306/health?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC",
                "root",
                "123",
                true,
                "bean",
                true,
                "QDebug"
              );


    }



}
