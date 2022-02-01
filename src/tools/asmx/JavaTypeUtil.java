package tools.asmx;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class JavaTypeUtil {

    public static Class<?> getClassObject(Class<?> type)
    {
        if(isVoid(type)) return Void.class;
        if(isPrimaryInt(type)) return Integer.class;
        if(isPrimaryBoolean(type)) return Boolean.class;
        if(isPrimaryFloat(type)) return Float.class;
        if(isPrimaryDouble(type)) return Double.class;
        if(isPrimaryByte(type)) return Byte.class;
        if(isPrimaryLong(type)) return Long.class;
        if(isPrimaryShort(type)) return Short.class;
        return type;
    }

    public static boolean isVoid(Class<?> type)
    {
        if(type.isPrimitive()&&type.getName().equals("void"))return  true;
        if(type.equals(Void.class))return true;
        return false;
    }

    public static boolean isPrimaryType(Class<?> clazz,String typename)
    {
        if(clazz instanceof Class)
        {
            Class jclass = (Class)clazz;
            String tname = jclass.getName();
            if(tname.equals(typename))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isPrimaryBoolean(Class<?> clazz)
    {
        return isPrimaryType(clazz,"boolean");
    }

    public static boolean isPrimaryByte(Class<?> clazz)
    {
        return isPrimaryType(clazz,"byte");
    }

    public static boolean isPrimaryLong(Class<?> clazz)
    {
        return isPrimaryType(clazz,"long");
    }

    public static boolean isPrimaryShort(Class<?> clazz)
    {
        return isPrimaryType(clazz,"short");
    }

    public static boolean isPrimaryFloat(Class<?> clazz)
    {
        return isPrimaryType(clazz,"float");
    }

    public static boolean isPrimaryDouble(Class<?> clazz)
    {
        return isPrimaryType(clazz,"double");
    }

    public static boolean isPrimaryInt(Class<?> clazz)
    {
        return isPrimaryType(clazz,"int");
    }

    public static boolean hasInterfaces(Class<?> clazz,Class<?> interfaceClazz) {
        //Class<?> d = DemoImport.class;
        Class<?>[] interfacesArray = clazz.getInterfaces();//获取这个类的所以接口类数组
        for (Class<?> item : interfacesArray) {
            if (item == interfaceClazz) { //判断是否有继承的接口
                return true;    //Log.e("调试_临时_log", "this_true");
            }
        }
        return false;
    }
/*
    public static boolean isPrimary(Class<?> clazz)
    {
        return clazz.isPrimitive();
    }*/
/*
    public static boolean isExtends(Class<?> subTclass,Class<?> superClass)
    {
        try {
            Class classA = subTclass.asSubclass(superClass);
        } catch (ClassCastException e) {
            return  false;
        }
        return true;
    }*/
/*
    public static Class<?> toWarped(Class<?> clazz)
    {
        if(!clazz.isPrimitive()) return clazz;
        String name = clazz.getName();
        if(name.equals("byte")) return Byte.class;
        if(name.equals("char")) return char.class;
        if(name.equals("short")) return Short.class;
        if(name.equals("boolean")) return Boolean.class;
        if(name.equals("int")) return Integer.class;
        if(name.equals("long")) return Long.class;
        if(name.equals("float")) return Float.class;
        if(name.equals("double")) return Double.class;
        return null;
    }*/
/*
    public static boolean isBoolean(Class<?> clazz)
    {
        return clazz.equals(boolean.class)||clazz.equals(Boolean.class);
    }
*/
    /*
    public static Field getField(Class<?> clazz,String fieldName)
    {
        try {
            Field field = clazz.getField(fieldName);
            return field;
        }
        catch (NoSuchFieldException ex)
        {
            return null;
        }
        catch (SecurityException ex)
        {
            return null;
        }
    }
*/
    /*
    public static ArrayList<Method> getMethods(Class<?> clazz, String name )
    {
        ArrayList<Method> founds = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        for(Method method :methods)
        {
            if(method.getName().equals(name))
            {
                founds.add(method);
            }
        }
        return founds;
    }*/
}
