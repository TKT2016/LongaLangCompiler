package longac.trees;

import longac.symbols.*;
import longac.utils.Assert;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** 访问字段表达式 */
public class JCFieldAccess extends JCExpression
{
    public JCExpression selected;

    public String name;

    public JCFieldAccess(JCExpression selected, String name ) {
        this.selected = selected;
        this.name = name;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg){ v.visitSelect(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateFieldAccess(this, d);
    }

    public AccessKind accessKind = AccessKind.ERROR;

    @Override
    public void setSymbol(Symbol symbol)
    {
        this.symbol = symbol;

        if (symbol instanceof MethodSymbol) {
             accessKind = AccessKind.METHOD;
        }
        else if (symbol instanceof VarSymbol) {
            VarSymbol varSymbol = (VarSymbol) symbol;
            if(varSymbol.isField())
                accessKind = AccessKind.FIELD;
            else
                accessKind = AccessKind.ERROR;
        }
        else if (symbol instanceof ErrorSymbol)
        {
            accessKind = AccessKind.ERROR;
        }
        else if (symbol instanceof MutilSymbol)
        {
            MutilSymbol mutilSymbol = (MutilSymbol) symbol;
            boolean isField = false;
            boolean isMethod = false;
            for(Symbol symbol1:mutilSymbol.symbols)
            {
                if(symbol1 instanceof MethodSymbol)
                    isMethod = true;
                else if (symbol1 instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) symbol1;
                    if(varSymbol.isField())
                        isField = true;
                }
            }
            if(isField&&isMethod)
                accessKind = AccessKind.ALL;
            else if(isField)
                accessKind = AccessKind.FIELD;
            else if(isMethod)
                accessKind = AccessKind.METHOD;
        }
        else
            Assert.error();
    }

    public enum AccessKind
    {
        FIELD,
        METHOD,
        ALL,
        ERROR
    }
}
