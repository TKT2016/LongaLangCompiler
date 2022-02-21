package longac.emits;

import longac.emits.CodeEmit;
import longac.emits.gens.EmitContext;
import longac.symbols.Symbol;
import longac.symbols.SymbolUtil;
import longac.trees.JCBinary;
import longac.trees.JCExpression;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class StringConcatEmit {
    MethodVisitor mv;
    JCBinary jcBinary;
    public StringConcatEmit(MethodVisitor mv, JCBinary jcBinary)
    {
        this.mv=mv;
        this.jcBinary=jcBinary;
    }

    public void start()
    {
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
    }

    public void append(JCExpression expression, CodeEmit emitor, EmitContext arg)
    {
        emitor.emitExpr(expression,arg);
        Symbol symbol = expression.getSymbol().getTypeSymbol();
        if(SymbolUtil.isString(symbol))
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        /*else if(SymbolUtil.isBoolean(symbol))
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;", false);
        else if(SymbolUtil.isInt(symbol))
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
        else if(SymbolUtil.isDouble(symbol))
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;", false);*/
        else
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
       // mv.visitInsn(POP);
    }

    public void end()
    {
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
    }
}
