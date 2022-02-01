package tools.asmx;

import longac.symbols.JavaClassSymbol;
import longac.symbols.SymbolUtil;
import longac.symbols.TypeSymbol;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ICONST_5;

public class JAsmUtil {

    public static ClassWriter newClassWriter()
    {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        return classWriter;
    }

    public static String saveClassFile(ClassWriter classWriter, String saveFilePath, String packageName, String className)
    {
        byte[] data = classWriter.toByteArray();
        String packagePath = packageName.replace(".","/");
        String folderPath =  saveFilePath+"/"+packagePath+"/";

        File folder = new File(folderPath);
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        String fullpath =folderPath+ className+".class";
        File file = new File(fullpath);
        try {
            FileOutputStream fout = new FileOutputStream(file);
            fout.write(data);
            fout.close();
            return file.getAbsolutePath();
        }
        catch (Exception e)
        {
            //throw new VisitException("ASMUtilsForJavaH.getDesc 2",e);
            System.out.println(e.getMessage());
            return null;
        }
    }
/*
    public static void invokeConstructor(MethodVisitor mv, Symbol member, Types types, Names names) {
        //MethodType mtype = (MethodType)member.externalType(types);
        TypeSymbol ownerTypeSymbol= (TypeSymbol) member.getOwner();
        String ownerSign = ownerTypeSymbol.getNameSignature()+ CompilerConfig.nameEnds;;
        String desc = SignatureGenUtil.typeSig( member.type ,types,names).toString();// String mdesc = poolWriter.typeSig( member.type).toString();// member.type.toString();
        mv.visitMethodInsn(INVOKESPECIAL, ownerSign, "<init>", desc, false);
    }
*/
    /*
    public static void invokeMethod(MethodVisitor mv, MethodSymbol member, Types types, Names names, boolean isStatic,boolean nonvirtual)
    {
        TypeSymbol ownerTypeSymbol= (TypeSymbol) member.getOwner();
        String ownerSign = ownerTypeSymbol.getNameSignature()+ CompilerConfig.nameEnds;;
        String mdesc = SignatureGenUtil.typeSig( member.type ,types,names).toString();// String mdesc = poolWriter.typeSig( member.type).toString();

        if(isStatic)
            mv.visitMethodInsn(INVOKESTATIC, ownerSign, member.name.toString() , mdesc, false);
        else if ((member.getOwner().flags() & Flags.INTERFACE) != 0 && !nonvirtual) {
           // code.emitInvokeinterface(member, mtype);
            mv.visitMethodInsn(INVOKEVIRTUAL, ownerSign, member.name.toString() , mdesc, true);
        } else if (nonvirtual) {
            //code.emitInvokespecial(member, mtype);
            mv.visitMethodInsn(INVOKESPECIAL, ownerSign, member.name.toString() , mdesc, false);
        } else {
            mv.visitMethodInsn(INVOKEVIRTUAL, ownerSign, member.name.toString() , mdesc, false);
           /// code.emitInvokevirtual(member, mtype);
        }
    }
*/
    /*
    public static void loadNull(MethodVisitor mv)
    {
        mv.visitInsn(Opcodes.ACONST_NULL);
    }*/

    public static void ret(MethodVisitor mv )
    {
        mv.visitInsn(RETURN);
    }

    public static void newClass(MethodVisitor mv, TypeSymbol type)
    {
        if(mv==null) return;
        String fullNameSign = type.getSignature(false);//"tests/javas/TestAsmEmit"
        mv.visitTypeInsn(NEW,fullNameSign );
        mv.visitInsn(DUP);
    }


public static void ret(MethodVisitor mv, TypeSymbol retype)
{
    if(SymbolUtil.isVoid(retype))
    {
        mv.visitInsn(RETURN);
        return;
    }

    if(retype instanceof JavaClassSymbol)
    {
        JavaClassSymbol javaClassSymbol = (JavaClassSymbol)retype;
        Class<?> type  = javaClassSymbol.clazz;
        if (int.class.equals(type)) {
            mv.visitInsn(IRETURN);
        }
        else if (Void.class.equals(type)||void.class.equals(type)) {
            mv.visitInsn(RETURN);
        }
        else if (boolean.class.equals(type)) {
            mv.visitInsn(IRETURN);
        }
        else if (char.class.equals(type)) {
            mv.visitInsn(IRETURN);
        }
        else  if (byte.class.equals(type)) {
            mv.visitInsn(IRETURN);
        }
        else if (short.class.equals(type)) {
            mv.visitInsn(IRETURN);
        }
        else if (float.class.equals(type)) {
            mv.visitInsn(FRETURN);
        }
        else  if (long.class.equals(type)) {
            mv.visitInsn(LRETURN);
        }
        else  if (double.class.equals(type)) {
            mv.visitInsn(DRETURN);
        }
        else
        {
            mv.visitInsn(ARETURN);
        }
    }
    else {
        mv.visitInsn(ARETURN);
    }
}

/*
    public static void ret(MethodVisitor mv, Symbol retype)
    {
        if(retype instanceof JavaClassSymbol)
        {
            JavaClassSymbol javaClassSymbol = (JavaClassSymbol)retype;
            Class<?> type  = javaClassSymbol.clazz;
            if (Integer.class.equals(type)||int.class.equals(type)) {
                mv.visitInsn(IRETURN);
            }
            else if (Void.class.equals(type)||void.class.equals(type)) {
                mv.visitInsn(RETURN);
            }
            else if (Boolean.class.equals(type)||boolean.class.equals(type)) {
                mv.visitInsn(IRETURN);
            }
            else if (Character.class.equals(type)||char.class.equals(type)) {
                mv.visitInsn(IRETURN);
            }
            else  if (Byte.class.equals(type)||byte.class.equals(type)) {
                mv.visitInsn(IRETURN);
            }
            else if (Short.class.equals(type)||short.class.equals(type)) {
                mv.visitInsn(IRETURN);
            }
            else if (Float.class.equals(type)||float.class.equals(type)) {
                mv.visitInsn(FRETURN);
            }
            else  if (Long.class.equals(type)||long.class.equals(type)) {
                mv.visitInsn(LRETURN);
            }
            else  if (Double.class.equals(type)||double.class.equals(type)) {
                mv.visitInsn(DRETURN);
            }
            else
            {
                mv.visitInsn(ARETURN);
            }
        }
        else {
            mv.visitInsn(ARETURN);
        }
    }*/


    public static void ret(MethodVisitor mv,  Class<?> type) {
        if (Integer.class.equals(type) || int.class.equals(type)) {
            mv.visitInsn(IRETURN);
        } else if (Void.class.equals(type) || void.class.equals(type)) {
            mv.visitInsn(RETURN);
        } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            mv.visitInsn(IRETURN);
        } else if (Character.class.equals(type) || char.class.equals(type)) {
            mv.visitInsn(IRETURN);
        } else if (Byte.class.equals(type) || byte.class.equals(type)) {
            mv.visitInsn(IRETURN);
        } else if (Short.class.equals(type) || short.class.equals(type)) {
            mv.visitInsn(IRETURN);
        } else if (Float.class.equals(type) || float.class.equals(type)) {
            mv.visitInsn(FRETURN);
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            mv.visitInsn(LRETURN);
        } else if (Double.class.equals(type) || double.class.equals(type)) {
            mv.visitInsn(DRETURN);
        } else {
            mv.visitInsn(ARETURN);
        }
    }
/*
    public static void loadConst(MethodVisitor mv, Object value, Type type)
    {
        TypeTag typeTag =type.getTag();
        switch (typeTag)
        {
            case BYTE:
                loadConstInteger(mv,Integer.parseInt(value.toString()));
                break;
            case SHORT:
                loadConstInteger(mv,Integer.parseInt(value.toString()));
                break;
            case CHAR:
                loadConstInteger(mv,(int)(value.toString().charAt(0)));
                break;
            case INT:
                loadConstInteger(mv,Integer.parseInt(value.toString()));
                break;
            case LONG:
                loadConstLong(mv,Long.parseLong(value.toString()));
                break;
            case FLOAT:
                loadConstFloat(mv,Float.parseFloat(value.toString()));
                break;
            case DOUBLE:
                loadConstDouble(mv,Double.parseDouble(value.toString()));
                break;
            case BOOLEAN:
                loadConstLong(mv,Boolean.parseBoolean(value.toString()));
                break;
            //case VOID: return VOIDcode;
            case CLASS:
                mv.visitLdcInsn(value);
                break;
           // case ARRAY:
           // case METHOD:
            case BOT:
                loadNull(mv);
                break;
            //case TYPEVAR:
            //case UNINITIALIZED_THIS:
            //case UNINITIALIZED_OBJECT:
            //    return OBJECTcode;
            default: throw new AssertionError("typecode " + type.getTag());
        }
    }*/

    public static void loadConstLong(MethodVisitor methodVisitor, boolean dvalue) {
        if(dvalue)
            methodVisitor.visitInsn(ICONST_0);
        else
            methodVisitor.visitInsn(ICONST_1);
    }

    public static void loadConstLong(MethodVisitor methodVisitor, long dvalue) {
        methodVisitor.visitLdcInsn(new Long(dvalue));
    }

    public static void loadConstFloat(MethodVisitor methodVisitor, float dvalue) {
        methodVisitor.visitLdcInsn(new Float(dvalue));
    }

    public static void loadConstDouble(MethodVisitor methodVisitor, double dvalue) {
        methodVisitor.visitLdcInsn(new Double(dvalue));
    }

    public static void loadConstInteger(MethodVisitor methodVisitor, int ivalue)
    {
        if(ivalue>=0 && ivalue<=5)
        {
            int opcode = loadConstOpCode(ivalue);
            methodVisitor.visitInsn(opcode);
        }
        else if(ivalue>=-128 && ivalue<127)
        {
            methodVisitor.visitIntInsn(BIPUSH,ivalue);
        }
        else if(ivalue>=-32768 && ivalue<32767)
        {
            methodVisitor.visitIntInsn(SIPUSH,ivalue);
        }
        else
        {
            methodVisitor.visitLdcInsn(ivalue);
        }
    }

    public static int loadConstOpCode(int ivalue)
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
}
