package longac.emits.gens;

import tools.asmx.JAsmUtil;
import tools.asmx.NameUtil;
import longac.symbols.*;
import longac.trees.JCClassDecl;
import longac.trees.JCMethodDecl;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static jdk.internal.org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.RETURN;

public class MjcGenEmit {

    public static org.objectweb.asm.ClassWriter emitClass(JCClassDecl cdef) {
        DeclClassSymbol classSymbol =(DeclClassSymbol)( cdef.getSymbol());
        org.objectweb.asm.ClassWriter classWriter = JAsmUtil.newClassWriter();
        String name =classSymbol.getSignature(false);
        String genClassNmae = NameUtil.nameToSign(name);
        String superClass = "java/lang/Object";
        String[] interfacesSigns = new String[]{};
        classWriter.visit(Opcodes.V1_8, ACC_PUBLIC + ACC_SUPER , genClassNmae, null, superClass , interfacesSigns);
        return classWriter;
    }

    public static String writeFile(JCClassDecl classDecl, ClassWriter classWriter)
    {
        DeclClassSymbol classSymbol = classDecl.getDeclClassSymbol();
        classWriter.visitEnd();
        String packageName = classSymbol.packge().getSignature(false);
        String fileName = classSymbol.name;
        return JAsmUtil.saveClassFile(classWriter, "out", packageName,fileName) ;
        //return packageName+"/"+ fileName+".class";
    }

    public static MethodVisitor emitMethod(JCMethodDecl tree, org.objectweb.asm.ClassWriter classWriter)
    {
        DeclMethodSymbol meth =(DeclMethodSymbol) tree.getSymbol();
        String methodName = meth.name.toString();
        String desc = meth.getSignature(true);
        MethodVisitor methodWriter = classWriter.visitMethod(ACC_PUBLIC, methodName, desc, null, null);
        return methodWriter;
    }

    public static void emitReturn(JCMethodDecl tree,MethodVisitor methodWriter)
    {
        if(tree.isContructor())
        {
            methodWriter.visitInsn(RETURN);
        }
        else {
            DeclMethodSymbol meth = (DeclMethodSymbol) tree.getSymbol();
            TypeSymbol retType = meth.returnTypeSymbol;
            JAsmUtil.ret(methodWriter, retType);
        }
    }
/*
    public static boolean visitEnd(JCMethodDecl tree,MethodVisitor methodWriter)
    {
        try {
            methodWriter.visitMaxs(0, 0);//设置COMPUTE_MAXS时必须调用此方法触发计算
            methodWriter.visitEnd();
        }catch (Exception e)
        {
            System.err.println("visitMaxs visitEnd:发生错误");
            //Debuger.outln(e.getMessage());
            return false;
        }
        return true;
    }*/
/*
    public static void visitEnd4init(MethodVisitor mv)
    {
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);//设置COMPUTE_MAXS时必须调用此方法触发计算
        mv.visitEnd();
    }*/
}
