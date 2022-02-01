package tools.asmx;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import longac.symbols.TypeSymbol;
import longac.symbols.VarSymbol;
import longac.utils.Debuger;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;

import static org.objectweb.asm.Opcodes.*;

public class ASMEmitlus {

    public static void emitStoreField(MethodVisitor mv , VarSymbol varSymbol )
    {
        TypeSymbol typeSymbol = varSymbol.getTypeSymbol();
        int op = varSymbol.isStatic() ? PUTSTATIC : PUTFIELD;
        mv.visitFieldInsn(op, varSymbol.owner.getSignature(false), varSymbol.name, typeSymbol.getSignature(true));
    }

    // private static int debug_g=0;
    public static void visitVarInsn(MethodVisitor mv,final int opcode, final int adr) {
       /* Debuger.outln("17 visitVarInsn:"+ opcode+" "+ adr+" "+debug_g );
        if( adr==2 && opcode==25)
            debug_g++;
        //else
        //    debug_g=0;
      //  Debuger.outln("24 visitVarInsn:"+ opcode+" "+ adr+" "+debug_g );

        if(debug_g== 6)
            Debuger.outln("24**** visitVarInsn:"+ opcode+" "+ adr );
*/
        mv.visitVarInsn(opcode, adr);
    }

    public static void loadConstFloat(MethodVisitor methodVisitor, float dvalue) {
        methodVisitor.visitLdcInsn(new Float(dvalue));
    }

    public static void loadConstInteger(MethodVisitor methodVisitor, int ivalue)
    {
        if(ivalue>=0 && ivalue<=5)
        {
            int opcode = JVMOpCodeSelecter.loadConst(ivalue);
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

    /**
     *  输出取负数
     */
    public static void emitNEG(MethodVisitor methodVisitor,Class<?> type)
    {
        int opcode = NOP;
        if(type.equals(int.class)||type.equals(Integer.class))
        {
            opcode=INEG;
        }
        else if(type.equals(long.class)||type.equals(Long.class))
        {
            opcode=LNEG;
        }
        else if(type.equals(float.class)||type.equals(Float.class))
        {
            opcode=FNEG;
        }
        else if(type.equals(double.class)||type.equals(Double.class))
        {
            opcode=DNEG;
        }
        methodVisitor.visitInsn(opcode);
    }

/*
    public static void emitEmptyConstructor(ClassWriter classWriter, Class<?> superClass)
    {
        String owner = JavaSignCodeUtil.getDesc(superClass,false);
        emitEmptyConstructor(classWriter,owner);
    }
*/
   /* public static void emitEmptyConstructor(ClassWriter classWriter,String superClassSign)
    {
        MethodVisitor mv= classWriter.visitMethod(Opcodes.ACC_PUBLIC,"<init>","()V", null,null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superClassSign, "<init>", "()V",false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }*/
/*
    public static void emitInnerEmptyConstructor(ClassWriter classWriter, Class<?> superClass,String outClassSign,String innerClassSign )
    {
        outClassSign=outClassSign.replace(".","/");
        String lmethodSign = "(L"+outClassSign+";)V";
        String ownerSign = JavaSignCodeUtil.getDesc(superClass,false);
        MethodVisitor mv= classWriter.visitMethod(0,"<init>",lmethodSign, null,null);
        mv.visitCode();
        //Label l0 = new Label();
        //mv.visitLabel(l0);
        //mv.visitLineNumber(28, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, innerClassSign, "this$0", "L"+outClassSign+ ";");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, ownerSign, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
*/
    public static void ret(MethodVisitor mv,Class<?> type)
    {
        if(type==Void.class)
        {
            mv.visitInsn(RETURN);
        }
        else if(type==boolean.class)
        {
            mv.visitInsn(IRETURN);
        }
        else if(type==byte.class)
        {
            mv.visitInsn(IRETURN);
        }
        else if(type==char.class)
        {
            mv.visitInsn(IRETURN);
        }
        else if(type==short.class)
        {
            mv.visitInsn(IRETURN);
        }
        else if(type==int.class)
        {
            mv.visitInsn(IRETURN);
        }
        else if(type==long.class)
        {
            mv.visitInsn(LRETURN);
        }
        else if(type==float.class)
        {
            mv.visitInsn(FRETURN);
        }
        else if(type==double.class)
        {
            mv.visitInsn(DRETURN);
        }
        else
        {
            mv.visitInsn(ARETURN);
        }
    }
}
