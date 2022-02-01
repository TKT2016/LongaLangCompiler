package longac.trees;

import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** 导入包或类型语句 */
public class JCImport extends JCTree
{
    public final JCTree qualid;
    public final boolean isPackageStar;
    public final boolean isStatic;
    public final boolean isImportChain;

    public JCImport(JCTree qualid,boolean isPackageStar, boolean isStatic,boolean isImportChain) {
        this.qualid = qualid;
        this.isPackageStar = isPackageStar;
        this.isStatic = isStatic;
        this.isImportChain = isImportChain;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitImport(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateImport(this, d);
    }

    @Override
    public boolean isEffective()
    {
        return true;
    }
}
