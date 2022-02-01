package longac.trees;

import longac.symbols.MethodSymbol;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

import java.util.ArrayList;

/** new(...) 表达式 */
public class JCNewClass extends JCExpression
{
    public JCExpression clazz;
    public ArrayList<JCExpression> args;

    public MethodSymbol constructorSymbol;

    public JCNewClass(JCExpression clazz, ArrayList<JCExpression> args)
    {
        this.clazz = clazz;
       this.args = new ArrayList<>();
        this.args.addAll(args);
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg){ v.visitNewClass(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateNewClass(this, d);
    }

}

