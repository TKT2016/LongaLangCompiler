package longac.optimizers;

import longac.lex.TokenKind;
import longac.symbols.Symbol;
import longac.symbols.VarSymbol;
import longac.trees.JCIdent;
import longac.trees.JCLiteral;
import longac.trees.JCTree;

public final class OptimizerUtil {
    public static boolean isTrue(Object obj)
    {
        if(obj==null) return false;
        if(obj instanceof Boolean)
            return ((Boolean)obj).booleanValue();
        return false;
    }

    public static boolean isFalse(Object obj)
    {
        if(obj==null) return false;
        if(obj instanceof Boolean)
            return !((Boolean)obj).booleanValue();
        return false;
    }

    public static boolean isInt(Object obj)
    {
        if(obj==null) return false;
        return  (obj instanceof Integer);
    }

    public static boolean isIntZero(Object obj)
    {
        if(!isInt(obj)) return false;
        return  ((Integer)obj).intValue()==0;
    }

    public static boolean isIntOne(Object obj)
    {
        if(!isInt(obj)) return false;
        return  ((Integer)obj).intValue()==1;
    }

    public static boolean isString(Object obj)
    {
        if(obj==null) return false;
        return  (obj instanceof String);
    }

    public static JCLiteral newLiteral(JCIdent tree, Object newValue)
    {
        JCLiteral newLiteral = new JCLiteral(newValue);
        newLiteral.log = tree.log;
        newLiteral.pos = tree.pos;
        newLiteral.line = tree.line;
        newLiteral.setSymbol(tree.getSymbol());
        return newLiteral;
    }

    public static JCLiteral newIntLiteral(JCTree jcLiteral, int newValue)
    {
        JCLiteral newLiteral = new JCLiteral(TokenKind.INTLITERAL ,newValue);
        newLiteral.log = jcLiteral.log;
        newLiteral.pos = jcLiteral.pos;
        newLiteral.line = jcLiteral.line;
        newLiteral.setSymbol(jcLiteral.getSymbol());
        return newLiteral;
    }

    public static JCLiteral newIntLiteral(JCTree tree, Object newValue, TokenKind literalKind)
    {
        JCLiteral newLiteral = new JCLiteral(literalKind,newValue);
        newLiteral.log = tree.log;
        newLiteral.pos = tree.pos;
        newLiteral.line = tree.line;
        newLiteral.setSymbol(tree.getSymbol());
        return newLiteral;
    }

    public static JCLiteral newBooleanLiteral(JCTree tree, boolean newValue)
    {
        JCLiteral newLiteral = new JCLiteral(newValue?TokenKind.TRUE:TokenKind.FALSE,newValue);
        newLiteral.log = tree.log;
        newLiteral.pos = tree.pos;
        newLiteral.line = tree.line;
        newLiteral.setSymbol(tree.getSymbol());
        return newLiteral;
    }

    public static JCLiteral newStringLiteral(JCTree tree, String newValue)
    {
        JCLiteral newLiteral = new JCLiteral(TokenKind.STRINGLITERAL,newValue);
        newLiteral.log = tree.log;
        newLiteral.pos = tree.pos;
        newLiteral.line = tree.line;
        newLiteral.setSymbol(tree.getSymbol());
        return newLiteral;
    }

    public static boolean isVar(JCTree tree)
    {
        Symbol symbol= tree.getSymbol();
        if(symbol instanceof VarSymbol)
            return true;
        return false;
    }

    public static VarSymbol getVarSymbol(JCTree tree)
    {
        Symbol symbol= tree.getSymbol();
        if(symbol instanceof VarSymbol)
            return (VarSymbol) symbol;
        return null;
    }
}
