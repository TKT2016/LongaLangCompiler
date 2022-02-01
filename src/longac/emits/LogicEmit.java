package longac.emits;

import longac.emits.gens.EmitContext;
import longac.trees.JCBinary;
import longac.trees.JCExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ICONST_0;

public class LogicEmit {
    private CodeEmit binaryEmit;
    private JCBinary tree;
    private EmitContext arg;
    private MethodVisitor mv;

    public LogicEmit(CodeEmit binaryEmit, JCBinary tree, EmitContext arg)
    {
        this.binaryEmit=binaryEmit;
        this.tree=tree;
        this.arg=arg;
        this.mv= arg.mv;
    }

    public void emitAND()
    {
        //emit(tree,arg,IFEQ,IFEQ);
       // MethodVisitor mv = arg.mv;
        emitExpr(tree.left);
        Label l1 = new Label();
        mv.visitJumpInsn(IFEQ, l1);
        emitExpr(tree.right);
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitInsn(ICONST_1);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);
    }

    public void emitOR()
    {
       // MethodVisitor mv = arg.mv;
        emitExpr(tree.left);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        emitExpr(tree.right);

        Label l2 = new Label();
        mv.visitJumpInsn(IFEQ, l2);
        mv.visitLabel(l1);
        // mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_1);
        Label l3 = new Label();
        mv.visitJumpInsn(GOTO, l3);
        mv.visitLabel(l2);
        //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l3);
    }

    public void emitGT()
    {
        Label l0 = new Label();
        mv.visitLabel(l0);
        //mv.visitLineNumber(59, l0);
        emitExpr(tree.left);
        emitExpr(tree.right);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPLE, l1);
        mv.visitInsn(ICONST_1);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
      //  mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);
        //mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
    }

    public void emitLT()
    {
        Label l0 = new Label();
        mv.visitLabel(l0);
        //mv.visitLineNumber(64, l0);
        emitExpr(tree.left);
        emitExpr(tree.right);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPGE, l1);
        mv.visitInsn(ICONST_1);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
       // mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);
    }

    public void emitEQ()
    {
        Label l0 = new Label();
        mv.visitLabel(l0);
       // mv.visitLineNumber(44, l0);
        emitExpr(tree.left);
        emitExpr(tree.right);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPNE, l1);
        mv.visitInsn(ICONST_1);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);
    }

    public void emitGE()
    {
        Label l0 = new Label();
        mv.visitLabel(l0);
       // mv.visitLineNumber(49, l0);
        emitExpr(tree.left);
        emitExpr(tree.right);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPLT, l1);
        mv.visitInsn(ICONST_1);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
       // mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);
       // mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
    }

    public void emitLE()
    {
        Label l0 = new Label();
        mv.visitLabel(l0);
       // mv.visitLineNumber(54, l0);
        emitExpr(tree.left);
        emitExpr(tree.right);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPGT, l1);
        mv.visitInsn(ICONST_1);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
       // mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);
        //mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
    }

    public void emitNE()
    {
        Label l0 = new Label();
        mv.visitLabel(l0);
       // mv.visitLineNumber(39, l0);
        emitExpr(tree.left);
        emitExpr(tree.right);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, l1);
        mv.visitInsn(ICONST_1);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);
       // mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
    }

    private void emitExpr(JCExpression tree)
    {
        binaryEmit.emitExpr(tree,arg);
    }

}
