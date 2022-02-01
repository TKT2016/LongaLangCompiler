package longac.trees;


import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** 括号表达式 */
public class JCParens extends JCExpression
{
    public JCExpression expr;
    public JCParens(JCExpression expr) {
        this.expr = expr;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitParens(this, arg); }

    public JCExpression getExpression() { return expr; }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateParens(this, d);
    }

    @Override
    public boolean isEffective()
    {
        return expr.isEffective;
    }
}