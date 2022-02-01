package longac.trees;

import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

import java.util.ArrayList;

public class JCErroneous extends JCExpression
{
    public ArrayList<? extends JCTree> errs;
    public JCErroneous(ArrayList<? extends JCTree> errs) {
        super();
        this.errs = errs;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitErroneous(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateErroneous(this, d);
    }

    @Override
    public boolean isEffective()
    {
        return false;
    }
}