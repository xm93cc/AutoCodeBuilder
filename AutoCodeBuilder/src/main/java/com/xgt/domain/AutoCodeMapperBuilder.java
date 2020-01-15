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
    public static void createPojoMapMapper(List<String> mapperStr,String createUser,String mapperPackageName,String suffix,String pojoPackName) throws Exception
    {
        if(mapperStr==null||mapperStr.size()<=0||createUser==null)
        {
            throw new RuntimeException("Incomplete parameters");
        }
        File file = new File("");
        if (mapperPackageName==null) {
            mapperPackageName="mapper";
        }
        if (suffix==null)
        {
            suffix="Mapper";
        }
        for (String mapperName : mapperStr) {
            String path=file.getCanonicalPath() + "\\src\\main\\java\\" + AutoCodePojoBuilder.class.getPackage().getName().replace(".", "\\")+"\\"+mapperPackageName ;
            File mapperDir=new File(path);
            if(!mapperDir.isDirectory()||!mapperDir.exists())
            {
                mapperDir.mkdirs();
            }
            if(suffix==null)
            {

               path = path+ "\\"+mapperName+"Mapper.java";
            }else
            {
                path = path+ "\\"+mapperName+suffix+".java";
            }

            String mapper="package "+AutoCodePojoBuilder.class.getPackage().getName()+"."+mapperPackageName+";\n"+
                    "import "+AutoCodePojoBuilder.class.getPackage().getName()+"."+pojoPackName+"."+mapperName+";\n" +
                    "import tk.mybatis.mapper.common.Mapper;\n" +
                    "\n" +
                    "/**\n" +
                    " * @author "+createUser+"\n" +
                    " */\n" +
                    "public interface "+mapperName+suffix+" extends Mapper<"+mapperName+"> {\n" +
                    "}";

            File mapperFile=new File(path);
            if (mapperFile.isFile())
            {
                continue;
            }
            if(!mapperFile.exists())
            {
                mapperFile.createNewFile();
            }
            FileOutputStream fos=new FileOutputStream(mapperFile);
            fos.write(mapper.getBytes());
            fos.close();




        }







    }
}
