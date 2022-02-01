package longac.trees;


import longac.symbols.DeclMethodSymbol;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/**
 * A return statement.
 */
public class JCReturn extends JCStatement
{
    public JCExpression expr;
    public JCReturn(JCExpression expr) {
        this.expr = expr;
    }
    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitReturn(this, arg); }

    public JCExpression getExpression() { return expr; }
    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateReturn(this, d);
    }

    public DeclMethodSymbol declMethodSymbol;
}