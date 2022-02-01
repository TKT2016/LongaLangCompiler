package longac.trees;

import longac.symbols.DeclClassSymbol;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

import java.util.ArrayList;

/** 类定义 */
public class JCClassDecl extends JCStatement
{
    public String name;
    public ArrayList<JCTree> defs;

    public JCClassDecl(String name, ArrayList<JCTree> defs)
    {
        this.name = name;
        this.defs = defs;
    }
    
    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitClassDef(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateClass(this, d);
    }

    public DeclClassSymbol getDeclClassSymbol()
    {
        return (DeclClassSymbol) symbol;
    }
}
