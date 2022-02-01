package longac.trees;

import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** while循环语句  */
public class JCWhile extends LoopStatement
{
    public JCExpression cond;
    public JCStatement body;

    public JCWhile(JCExpression cond, JCStatement body) {
        this.cond = cond;
        this.body = body;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitWhileLoop(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateWhile(this, d);
    }

}