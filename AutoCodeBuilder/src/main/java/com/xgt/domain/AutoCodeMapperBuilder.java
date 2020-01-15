package com.xgt.domain;/*
*******
QDebug
*******
*******
2020/1/15 16:59
*******
*/

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @author QDebug
 */
public class AutoCodeMapperBuilder {
    /**
     * @param mapperStr         需要生成的Mapper列表
     * @param createUser        mapper创建用户名
     * @param mapperPackageName mapper的包名
     * @param suffix            mapper后缀 如：UserSuffix
     * @param pojoPackName      当前包下的pojo包
     * @throws Exception
     */
    public static void createPojoMapMapper(List<String> mapperStr, String createUser, String mapperPackageName, String suffix, String pojoPackName) throws Exception {
        if (mapperStr == null || mapperStr.size() <= 0 || createUser == null) {
            throw new RuntimeException("Incomplete parameters");
        }
        File file = new File("");
        if (mapperPackageName == null) {
            mapperPackageName = "mapper";
        }
        if (suffix == null) {
            suffix = "Mapper";
        }
        for (String mapperName : mapperStr) {
            String path = file.getCanonicalPath() + "\\src\\main\\java\\" + AutoCodePojoBuilder.class.getPackage().getName().replace(".", "\\") + "\\" + mapperPackageName;
            File mapperDir = new File(path);
            if (!mapperDir.isDirectory() || !mapperDir.exists()) {
                mapperDir.mkdirs();
            }
            if (suffix == null) {

                path = path + "\\" + mapperName + "Mapper.java";
            } else {
                path = path + "\\" + mapperName + suffix + ".java";
            }

            String mapper = "package " + AutoCodePojoBuilder.class.getPackage().getName() + "." + mapperPackageName + ";\n" +
                    "import " + pojoPackName + "." + mapperName + ";\n" +
                    "import tk.mybatis.mapper.common.Mapper;\n" +
                    "\n" +
                    "/**\n" +
                    " * @author " + createUser + "\n" +
                    " */\n" +
                    "public interface " + mapperName + suffix + " extends Mapper<" + mapperName + "> {\n" +
                    "}";

            File mapperFile = new File(path);
            if (mapperFile.isFile()) {
                continue;
            }
            if (!mapperFile.exists()) {
                mapperFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(mapperFile);
            fos.write(mapper.getBytes());
            fos.close();
        }


    }

    /**
     * @param pojoPackageName   pojo在当前工程的包
     * @param createUser        创建作者
     * @param mapperPackageName mapper的包名
     * @param suffix            添加后缀，默认Mapper
     * @throws Exception
     */

    public static void createPojoMapperByPojoPackageName(String pojoPackageName, String createUser, String mapperPackageName, String suffix) throws Exception {
        pojoPackageName = pojoPackageName.replace(".", "\\");
        File file = new File(new File("").getCanonicalPath() + "\\src\\main\\java\\" + pojoPackageName);
        File[] files = file.listFiles();
        if (suffix == null) {
            suffix = "Mapper";
        }
        if (mapperPackageName == null) {
            mapperPackageName = "mapper";
        }
        for (File file1 : files) {

            String mapperName = file1.getName().split("\\.")[0] + suffix;
            String path = new File("").getCanonicalPath() + "\\src\\main\\java\\" + AutoCodePojoBuilder.class.getPackage().getName().replace(".", "\\") + "\\" + mapperPackageName;
            File mapperDir = new File(path);
            if (!mapperDir.isDirectory()) {
                mapperDir.mkdirs();
            }
            path = path + "\\" + mapperName + ".java";

            String mapper = "package " + AutoCodePojoBuilder.class.getPackage().getName() + "." + mapperPackageName + ";\n" +
                    "import " + pojoPackageName.replace("\\", ".") + "." + file1.getName().split("\\.")[0] + ";\n" +
                    "import tk.mybatis.mapper.common.Mapper;\n" +
                    "\n" +
                    "/**\n" +
                    " * @author " + createUser + "\n" +
                    " */\n" +
                    "public interface " + mapperName + " extends Mapper<" + file1.getName().split("\\.")[0] + "> {\n" +
                    "}";
            File classPath = new File(path);
            if (classPath.exists()) {
                continue;
            }
            FileOutputStream fos = new FileOutputStream(classPath);
            fos.write(mapper.getBytes());
            fos.close();

        }
        pojoPackageName.replace(".", "\\");

    }

}
