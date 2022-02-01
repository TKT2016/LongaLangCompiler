package longac.trees;

import longac.symbols.MethodSymbol;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

import java.util.ArrayList;

/** 方法调用表达式 */
public class JCMethodInvocation extends JCExpression
{
    public JCExpression meth;
    public ArrayList<JCExpression> args;
    public JCMethodInvocation(JCExpression meth, ArrayList<JCExpression> args)
    {
        this.meth = meth;
        this.args = args;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitMethodInvocation(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateMethodInvocation(this, d);
    }

    public MethodSymbol getMethodSymbol()
    {
        if(this.symbol instanceof MethodSymbol)
            return (MethodSymbol) symbol;
        else
            return null;
    }
}
