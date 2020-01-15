package com.xgt.domain;/*
*******
QDebug
*******
*******
2020/1/15 16:49
*******
*/

import java.util.ArrayList;
import java.util.List;

/**
 * @author QDebug
 */
public class RunMain {
    public static void main(String[] args) throws Exception {
        //生成pojo后得到生成的pojo类名
        List<String> tableInfos = AutoCodePojoBuilder.getTableInfos(
                "com.mysql.jdbc.Driver",
                "jdbc:mysql://localhost:3306/health?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC",
                "root",
                "123",
                true,
                "pojo",
                false,
                "QDebug"
        );
        //更具得到的类名生成通用Mapper
        AutoCodeMapperBuilder.createPojoMapMapper(tableInfos, "Admin", "mapper", null, "com.xgt.domain.pojo");
        //通过当前已有的pojo包来生成Mapper
        // AutoCodeMapperBuilder.createPojoMapperByPojoPackageName("com.xgt.domain.pojo","admin",null,null);
    }
}
