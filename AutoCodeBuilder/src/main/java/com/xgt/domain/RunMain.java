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
        AutoCodeMapperBuilder.createPojoMapMapper(tableInfos,"Admin","mapper",null,"pojo");

    }
}
