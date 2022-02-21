package longac;

import longac._core.MainCompiler;
import tools.io.FileUtil;

import java.io.File;

public class MainTest {
    static  final   String ext =".lga";

    public static void main(String[] args)
    {
        testone("SQLDomainTest");
        //testone("GUIDSLTest");
    }

    static void testBat(int index)
    {
       // String classPath0 = "";
        String baseDir = FileUtil.getCurrentPath()+"\\samples\\";
        File file = new File(baseDir);

        File[] tempList = file.listFiles();
        System.out.println("共有文件:"+tempList.length);
        if(index!=-1)
        {
            compileRun(tempList[index],index);
            return;
        }

        for (int i = 0; i < tempList.length; i++) {
            File item = tempList[i];
            compileRun(item,i);
        }
    }

    static void compileRun( File item,int i)
    {
        String src = item.getAbsolutePath();
        if (src.endsWith(ext)) {
            System.out.println("编译运行第"+i+":"+ src);
            MainCompiler.compileRun(src,false);
        }
    }

    static void testone(String srcName)
    {
        // String classPath1= "D:\\FerySrc\\JminusB\\target\\classes";
        // String classPath2 ="D:\\FerySrc\\JminusB\\lib";
        //String classPath0 = classPath1+";"+classPath2;
       // String classPath0 = "";
        String baseDir = FileUtil.getCurrentPath()+"\\samples\\";
        //String outPath =baseDir+"out\\";
        String feoSrc=srcName;

        feoSrc+=ext;
        // String Cmd ;//= "-classpath "+classPath0 +" -g -d "+outPath+" "+baseDir+feoSrc;
        // Cmd = "-I -i "+baseDir+(feoSrc+ext);
        //  System.out.println(Cmd);
        //args =Cmd.split(" ");
        // MainCompiler.main(args);
        System.out.println(baseDir+feoSrc);
        MainCompiler.compileRun(baseDir+feoSrc,false);
    }
}
