package longac.trees;

import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** break 语句 */
public class JCBreak extends JCStatement
{
    public JCBreak(  ) {

    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitBreak(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateBreak(this, d);
    }

    public LoopStatement loopTree;
}