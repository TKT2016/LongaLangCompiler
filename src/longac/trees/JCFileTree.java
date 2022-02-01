package longac.trees;

import longac.symbols.SourceFileSymbol;
import longac.symbols.Symbol;
import longac.utils.Assert;

import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

import java.util.ArrayList;

public class JCFileTree extends JCTree
{
    public ArrayList<JCTree> defs;

    public JCFileTree(ArrayList<JCTree> defs) {
        this.defs = defs;
    }

    public JCPackage getPackage() {
        // PackageDecl must be the first entry if it exists
        if (!defs.isEmpty() && defs.get(0) instanceof JCPackage)
            return (JCPackage)defs.get(0);
        return null;
    }

    public JCExpression getPackageName() {
        JCPackage pd = getPackage();
        return pd != null ? pd.packageName : null;
    }

    public ArrayList<JCImport> getImports() {
        ArrayList<JCImport> imports = new ArrayList<>();
        for (JCTree tree : defs) {
            if (tree instanceof JCImport)
                imports.add((JCImport)tree);
            else if (!(tree instanceof JCPackage))
                break;
        }
        return imports;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitCompilationUnit(this,  arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateCompilationUnit(this, d);
    }

    public void setSymbol(Symbol sym)
    {
        if(sym instanceof SourceFileSymbol)
            this.symbol = sym;
        else
            Assert.error();
    }

    @Override
    public boolean isEffective()
    {
        return true;
    }
}
