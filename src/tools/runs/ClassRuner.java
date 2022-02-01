package tools.runs;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassRuner
{
    public Object runBytes(ClassRunArg arg) throws Exception
    {
        if(arg.checkBytes)
          checkJVMBytes(arg.bytes);

        DynamicClassLoader cl = new DynamicClassLoader(arg.classPaths);
        Class<?> clazz = cl.defineClass(arg.className, arg.bytes);
        Method main = null;
        try {
            main = clazz.getMethod(arg.method, arg.argTypes);
        }catch (NoSuchMethodException exception)
        {
            String msg = "类"+arg.className+"的"+arg.method+"方法不存在或者参数错误";
            System.err.println(msg);
            return null;
        }
        /* 调用默认构造函数反射生成实例，并调用其中的方法 */
        boolean isStatic = Modifier.isStatic(main.getModifiers());
        if(isStatic) {
            Object result =  main.invoke(null, new Object[]{new String[]{}});
            return result;
        }
        else
        {
            Object obj = clazz.newInstance();
            Object result =  main.invoke(obj,arg.args);
            return result;
        }
    }

    /* 检查class文件正确性 */
    public static void checkJVMBytes(byte[] bytes)
    {
        PrintWriter pw = new PrintWriter(System.out);
        CheckClassAdapter.verify(new ClassReader(bytes), true, pw);
    }
}
