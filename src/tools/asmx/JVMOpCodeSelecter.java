package tools.asmx;


import static org.objectweb.asm.Opcodes.*;

public class JVMOpCodeSelecter {


    public static int arrayLoad(Class<?> type)
    {
        if(type==boolean.class)
        {
            return BALOAD;
        }
        if(type==byte.class)
        {
            return BALOAD;
        }
        else if(type==char.class)
        {
            return CALOAD;
        }
        else if(type==short.class)
        {
            return SALOAD;
        }
        if(type==int.class)
        {
            return IALOAD;
        }
        else if(type==long.class)
        {
            return LALOAD;
        }
        else if(type==float.class)
        {
            return FALOAD;
        }
        else if(type==double.class)
        {
            return DALOAD;
        }
        return AALOAD;
    }

    public static int arrayStorm(Class<?> type)
    {
        if(type==boolean.class)
        {
            return BASTORE;
        }
        if(type==byte.class)
        {
            return BASTORE;
        }
        else if(type==char.class)
        {
            return CASTORE;
        }
        else if(type==short.class)
        {
            return SASTORE;
        }
        else if(type==int.class)
        {
            return IASTORE;
        }
        else if(type==long.class)
        {
            return LASTORE;
        }
        else if(type==float.class)
        {
            return FASTORE;
        }
        else if(type==double.class)
        {
            return DASTORE;
        }
        return AASTORE;
    }

    public static int newArray(Class<?> type)
    {
        if(type==boolean.class)
        {
            return T_BOOLEAN;
        }
        if(type==byte.class)
        {
            return T_BYTE;
        }
        else if(type==char.class)
        {
            return T_CHAR;
        }
        else if(type==short.class)
        {
            return T_SHORT;
        }
        if(type==int.class)
        {
            return T_INT;
        }
        else if(type==long.class)
        {
            return T_LONG;
        }
        else if(type==float.class)
        {
            return T_FLOAT;
        }
        else if(type==double.class)
        {
            return T_DOUBLE;
        }
        return -1;
    }

    public static int rem(Class<?> type)
    {
        /*if(type==Void.class)
        {
            return -1;
        }
        else if(type==boolean.class)
        {
            return (IRETURN);
        }*/
        if(type==byte.class)
        {
            return IREM;
        }
        /*else if(type==char.class)
        {
            return (IRETURN);
        }*/
        else if(type==short.class)
        {
            return IREM;
        }
        if(type==int.class)
        {
            return IREM;
        }
        else if(type==long.class)
        {
            return LREM;
        }
        else if(type==float.class)
        {
            return FREM;
        }
        else if(type==double.class)
        {
            return DREM;
        }
        return -1;
    }

    public static int div(Class<?> type)
    {
        /*if(type==Void.class)
        {
            return -1;
        }
        else if(type==boolean.class)
        {
            return (IRETURN);
        }*/
        if(type==byte.class)
        {
            return IDIV;
        }
        /*else if(type==char.class)
        {
            return (IRETURN);
        }*/
        else if(type==short.class)
        {
            return IDIV;
        }
        if(type==int.class)
        {
            return IDIV;
        }
        else if(type==long.class)
        {
            return LDIV;
        }
        else if(type==float.class)
        {
            return FDIV;
        }
        else if(type==double.class)
        {
            return DDIV;
        }
        return -1;
    }

    public static int mul(Class<?> type)
    {
        /*if(type==Void.class)
        {
            return -1;
        }
        else if(type==boolean.class)
        {
            return (IRETURN);
        }*/
        if(type==byte.class)
        {
            return IMUL;
        }
        /*else if(type==char.class)
        {
            return (IRETURN);
        }*/
        else if(type==short.class)
        {
            return IMUL;
        }
        if(type==int.class)
        {
            return IMUL;
        }
        else if(type==long.class)
        {
            return LMUL;
        }
        else if(type==float.class)
        {
            return FMUL;
        }
        else if(type==double.class)
        {
            return DMUL;
        }
        return -1;
    }

    public static int sub(Class<?> type)
    {
        /*if(type==Void.class)
        {
            return -1;
        }
        else if(type==boolean.class)
        {
            return (IRETURN);
        }*/
        if(type==byte.class)
        {
            return ISUB;
        }
        /*else if(type==char.class)
        {
            return (IRETURN);
        }*/
        else if(type==short.class)
        {
            return ISUB;
        }
        if(type==int.class)
        {
            return ISUB;
        }
        else if(type==long.class)
        {
            return LSUB;
        }
        else if(type==float.class)
        {
            return FSUB;
        }
        else if(type==double.class)
        {
            return DSUB;
        }
        return -1;
    }

    public static int add(Class<?> type)
    {
        /*if(type==Void.class)
        {
            return -1;
        }
        else if(type==boolean.class)
        {
            return (IRETURN);
        }*/
        if(type==byte.class)
        {
            return (IADD);
        }
        /*else if(type==char.class)
        {
            return (IRETURN);
        }*/
        else if(type==short.class)
        {
            return IADD;
        }
        if(type==int.class)
        {
            return IADD;
        }
        else if(type==long.class)
        {
            return LADD;
        }
        else if(type==float.class)
        {
            return FADD;
        }
        else if(type==double.class)
        {
            return DADD;
        }
        return -1;
    }

    public static int neg(Class<?> type)
    {
        /*if(type==Void.class)
        {
            return -1;
        }
        else if(type==boolean.class)
        {
            return (IRETURN);
        }
        else if(type==byte.class)
        {
            return (IRETURN);
        }
        else if(type==char.class)
        {
            return (IRETURN);
        }
        else if(type==short.class)
        {
            return INEG;
        }*/
        if(type==int.class)
        {
            return INEG;
        }
        else if(type==long.class)
        {
            return LNEG;
        }
        else if(type==float.class)
        {
            return FNEG;
        }
        else if(type==double.class)
        {
            return DNEG;
        }
        return -1;
    }

    public static int ret(Class<?> type)
    {
        if(type==Void.class)
        {
            return (RETURN);
        }
        else if(type==boolean.class)
        {
            return (IRETURN);
        }
        else if(type==byte.class)
        {
            return (IRETURN);
        }
        else if(type==char.class)
        {
            return (IRETURN);
        }
        else if(type==short.class)
        {
            return (IRETURN);
        }
        else if(type==int.class)
        {
            return (IRETURN);
        }
        else if(type==long.class)
        {
            return (LRETURN);
        }
        else if(type==float.class)
        {
            return (FRETURN);
        }
        else if(type==double.class)
        {
            return (DRETURN);
        }
        else
        {
            return (ARETURN);
        }
    }

    public static int loadConst(int ivalue)
    {
        switch (ivalue)
        {
            case 0:
                return ICONST_0;
            case 1:
                return ICONST_1;
            case 2:
                return ICONST_2;
            case 3:
                return ICONST_3;
            case 4:
                return ICONST_4;
            case 5:
                return ICONST_5;
            default:
                if(ivalue>=-128 && ivalue<127)
                {
                    return  BIPUSH;
                }
                if(ivalue>=-32768 && ivalue<32767)
                {
                    return  SIPUSH;
                }
                /*if(ivalue>=-2147483648 && ivalue<2147483647)
                {
                    return  SIPUSH;
                }*/
                return -1;
        }
    }

    public static int load(Class<?> clazz)
    {
        if(JavaTypeUtil.isPrimaryInt(clazz))
        {
            return ILOAD;
        }
        else if(JavaTypeUtil.isPrimaryBoolean(clazz))
        {
            return ILOAD;
        }
        else if(JavaTypeUtil.isPrimaryFloat(clazz))
        {
            return FLOAD;
        }
        else if(JavaTypeUtil.isPrimaryDouble(clazz))
        {
            return DLOAD;
        }
        return ALOAD;
    }

    public static int store(Class<?> clazz)
    {
        /*if(clazz.equals(int.class))
        {
            return ISTORE;
        }*/

       /* if(clazz instanceof java.lang.Class)
        {
            java.lang.Class jclass = (java.lang.Class)clazz;
            String tname = jclass.getName();
            if(tname.equals("int"))
            {
                return ISTORE;
            }
        }*/
        if(JavaTypeUtil.isPrimaryInt(clazz))
        {
            return ISTORE;
        }
        else if(JavaTypeUtil.isPrimaryBoolean(clazz))
        {
            return ISTORE;
        }
        else if(JavaTypeUtil.isPrimaryFloat(clazz))
        {
            return FSTORE;
        }
        else if(JavaTypeUtil.isPrimaryDouble(clazz))
        {
            return DSTORE;
        }
        return ASTORE;
    }
}
