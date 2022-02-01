package longac.trees;

import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** 表达式语句 */
public class JCExpressionStatement extends JCStatement // JCOtherStatment
{
    public JCExpression expr;
    public JCExpressionStatement(JCExpression expr)
    {

        this.expr = expr;
    }
    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitExec(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateExpressionStatement(this, d);
    }

    @Override
    public boolean isEffective()
    {
        if(expr==null) return false;
        else
        return expr.isEffective();
    }
}
