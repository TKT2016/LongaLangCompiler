package longac.trees;

import longac.symbols.PackageSymbol;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/**
 * java 包
 */
public class JCPackage extends JCTree
{
    /* 包名称表达式 */
    public JCExpression packageName;

    public JCPackage(JCExpression pid) {
        this.packageName = pid;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitPackageDef(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translatePackage(this, d);
    }

    @Override
    public boolean isEffective()
    {
        return true;
    }

    public PackageSymbol packge;
}
