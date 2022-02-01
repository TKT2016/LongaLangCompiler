package longac.trees;

import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** 赋值表达式 "=" */
public class JCAssign extends JCExpression
{
    public JCExpression left;
    public JCExpression right;

    public JCAssign(JCExpression lhs, JCExpression rhs) {
        this.left = lhs;
        this.right = rhs;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitAssign(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateAssign(this, d);
    }

    public boolean assignEffective = true;
}
