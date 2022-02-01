package longac.trees;

import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** continue 语句 */
public class JCContinue extends JCStatement
{
    public JCContinue( ) {

    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitContinue(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateContinue(this, d);
    }

    public LoopStatement loopTree;
}