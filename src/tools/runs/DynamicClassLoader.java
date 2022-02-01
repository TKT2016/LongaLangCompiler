package tools.runs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class DynamicClassLoader extends ClassLoader
{
    private static final String SUFFIX = ".class";
    public String[] paths;

    public DynamicClassLoader(String[] paths) {
        this.paths = paths;
    }

    public DynamicClassLoader(ClassLoader parent,String[] paths){
        super(parent);
        this.paths = paths;
    }

    /* defineClass是抽象类ClassLoader中 protected修饰的方法
       需要在子类中将这个方法公开可调用 */
    public Class<?> defineClass(String name, byte[] b) {
        return super.defineClass(name, b, 0, b.length);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        //System.out.println("findClass:"+className);
        String classPath = getClassPath(className);
        if(classPath != null){
            byte[] clazz = loadClazz(classPath);
            return defineClass(clazz, 0, clazz.length);
        }else{
            System.out.println("class is not found :"+className);
            return null;
        }
    }

    public byte[] loadClazz(String classPath) {
        try {
            File file = new File(classPath);
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b;
            while((b = in.read()) != -1){
                baos.write(b);
            }
            in.close();
            return baos.toByteArray();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public String getClassPath(String className){
        String classNamePath = className;
        if(classNamePath.contains(".")){
            classNamePath = classNamePath.replace(".", "/");
        }
        for(String path : paths){
            String classPath = path+"\\" + classNamePath + SUFFIX;
            File classFile = new File(classPath);
            if(classFile.exists()){
                return classPath;
            }
        }
        return null;
    }
}
