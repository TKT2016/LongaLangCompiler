package longac.trees;

import longac.symbols.*;
import longac.symbols.JavaVarSymbol;
import longac.utils.Assert;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** 标识符表达式 */
public class JCIdent extends JCExpression
{
    public final String name;

    public JCIdent(String name) {
        this.name = name;
    }
    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitIdent(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateIdent(this, d);
    }

    public IdentKind identKind = IdentKind.ERROR;

    public static enum IdentKind
    {
        THIS,
        TYPE,
        MethodStatic,
        MethodInstace,
        FieldStatic,
        FieldInstance,
        VAR,
        ERROR
    }

    @Override
    public void setSymbol(Symbol symbol)
    {
        this.symbol = symbol;
        if (symbol instanceof TypeSymbol) {
            identKind = IdentKind.TYPE;
        }
        else if (symbol instanceof MethodSymbol) {
            MethodSymbol methodSymbol = (MethodSymbol) symbol;
            identKind = methodSymbol.isStatic()?IdentKind.MethodStatic:IdentKind.MethodInstace;
        }
        else if (symbol instanceof JavaVarSymbol) {
            JavaVarSymbol javaVarSymbol = (JavaVarSymbol) symbol;
            if(javaVarSymbol.isField())
                identKind = javaVarSymbol.isStatic()?IdentKind.FieldStatic:IdentKind.FieldInstance;
        }
        else if (symbol instanceof DeclVarSymbol) {
            DeclVarSymbol declVarSymbol = (DeclVarSymbol) symbol;
            if(declVarSymbol.varSymbolKind == VarKind._this)
                identKind = IdentKind.FieldInstance;
            else if(declVarSymbol.varSymbolKind == VarKind.field)
                identKind = IdentKind.THIS;
            else if(declVarSymbol.varSymbolKind == VarKind.field)
                identKind = IdentKind.VAR;
        }
        else if (symbol instanceof ErrorSymbol)
        {
            identKind = IdentKind.ERROR;
        }
        else if (symbol instanceof MutilSymbol)
        {
            identKind = IdentKind.ERROR;
        }
        else
            Assert.error();
    }
}
