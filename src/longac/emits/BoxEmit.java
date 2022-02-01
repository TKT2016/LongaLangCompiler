package longac.emits;

import longac.symbols.SymbolUtil;
import longac.symbols.TypeSymbol;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class BoxEmit {
    public static boolean box(MethodVisitor mv,TypeSymbol toType,TypeSymbol curType)
    {
        if(SymbolUtil.isBoolean(curType))
        {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            return true;
        }
        if(SymbolUtil.isInt(curType))
        {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            return true;
        }
      /*  if(SymbolUtil.isDouble(curType))
        {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            return true;
        }*/
        return false;
    }

    public static boolean unbox(MethodVisitor mv,TypeSymbol toType,TypeSymbol curType)
    {
        if (SymbolUtil.isBoolean(toType) && SymbolUtil.isBoolean(curType)) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            return true;
        }
        if (SymbolUtil.isInt(toType) && SymbolUtil.isInt(curType)) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            return true;
        }
        /*if (SymbolUtil.isDouble(toType) && SymbolUtil.isDouble(curType)) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            return true;
        }*/
        return false;
    }
}
