package longac.trees;

import longac.symbols.BlockFrame;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** for循环 */
public class JCForLoop extends LoopStatement
{
    /** 初始化语句 */
    public JCStatement init;
    /** 条件表达式 */
    public JCExpression cond;
    /** 更新语句 */
    public JCExpression step;
    /** 函数体 */
    public JCStatement body;

    public JCForLoop()
    {

    }

    public JCForLoop(JCStatement init, JCExpression cond, JCExpression step, JCStatement body )
    {
        this.init = init;
        this.cond = cond;
        this.step = step;
        this.body = body;
    }

    public BlockFrame frame;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitForLoop(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateForLoop(this, d);
    }
}
