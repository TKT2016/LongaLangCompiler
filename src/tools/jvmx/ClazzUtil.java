package tools.jvmx;

public class ClazzUtil {
    public static int getExtendsDeep(Class<?> clazz)
    {
        int i=0;
        Class<?> temp = clazz;
        while (temp!=null)
        {
            i++;
            temp=temp.getSuperclass();
        }
        return i;
    }
}
