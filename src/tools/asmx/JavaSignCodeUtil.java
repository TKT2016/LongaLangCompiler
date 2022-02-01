package tools.asmx;

public class JavaSignCodeUtil {
    public static String getDesc(final Class<?> returnType,boolean isReturn) {
        if (returnType.isPrimitive()) {
            return getPrimitiveLetter(returnType);
        }
        if (returnType.isArray()) {
            return "[" + getDesc(returnType.getComponentType(),isReturn);
        }
        if(isReturn)
            return "L" + getType(returnType) + ";";
        else
            return  getType(returnType);
    }

    public static String getType(final Class<?> parameterType) {
        if (parameterType.isArray()) {
            return "[" + getDesc(parameterType.getComponentType(),false);
        }
        if (!parameterType.isPrimitive()) {
            final String clsName = parameterType.getName();
            return clsName.replaceAll("\\.", "/");
        }
        return getPrimitiveLetter(parameterType);
    }

    public static String getPrimitiveLetter(final Class<?> type) {
        if (Integer.class.equals(type)||int.class.equals(type)) {
            return "I";
        }
        if (Void.class.equals(type)||void.class.equals(type)) {
            return "V";
        }
        if (Boolean.class.equals(type)||boolean.class.equals(type)) {
            return "Z";
        }
        if (Character.class.equals(type)||char.class.equals(type)) {
            return "C";
        }
        if (Byte.class.equals(type)||byte.class.equals(type)) {
            return "B";
        }
        if (Short.class.equals(type)||short.class.equals(type)) {
            return "S";
        }
        if (Float.class.equals(type)||float.class.equals(type)) {
            return "F";
        }
        if (Long.class.equals(type)||long.class.equals(type)) {
            return "J";
        }
        if (Double.class.equals(type)||double.class.equals(type)) {
            return "D";
        }
        throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
    }

}
