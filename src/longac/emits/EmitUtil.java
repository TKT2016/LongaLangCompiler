package longac.emits;

import longac.utils.Debuger;
import org.objectweb.asm.Label;
import tools.asmx.NameUtil;
import longac.symbols.DeclClassSymbol;
import longac.trees.JCClassDecl;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static jdk.internal.org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class EmitUtil {
    public static ClassWriter emitClass(JCClassDecl cdef  ,boolean COMPUTE_FRAMES, boolean COMPUTE_MAXS) {
        int flag = 0 ;
        if(COMPUTE_FRAMES)
            flag |= ClassWriter.COMPUTE_FRAMES;
        if(COMPUTE_MAXS)
            flag |= ClassWriter.COMPUTE_MAXS;

        DeclClassSymbol classSymbol = cdef.getDeclClassSymbol();
        ClassWriter classWriter = new ClassWriter(flag );
        String name =classSymbol.getSignature(false);
        String genClassName = NameUtil.nameToSign(name);
        String superClass = "java/lang/Object";
        String[] interfacesSigns = new String[]{};
        classWriter.visit(Opcodes.V1_8, ACC_PUBLIC + ACC_SUPER , genClassName, null, superClass , interfacesSigns);
        return classWriter;
    }

    public static void visitMaxs(MethodVisitor mv)
    {
        try {
             mv.visitMaxs(0, 0);
        }catch (Exception ex)
        {
            System.err.println("MethodVisitor.visitMaxs exception:"+ex.getMessage());
        }
    }

    public static void visitLabel(MethodVisitor mv, Label label)
    {
        //if(label.toString().startsWith("L495053715"))
        //    Debuger.outln("45 visitLabel:"+label);
        mv.visitLabel(label);
    }
}
