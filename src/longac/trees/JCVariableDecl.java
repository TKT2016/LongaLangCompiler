package longac.trees;

import longac.symbols.DeclVarSymbol;
import longac.symbols.VarKind;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** 声明变量表达式 */
public class JCVariableDecl extends JCExpression // JCOtherStatment
{
    public JCExpression vartype;
    public final String name;
    public final JCIdent nameExpr;
    public JCExpression init;

    public JCVariableDecl(JCExpression vartype, JCIdent nameexpr, JCExpression init)
    {
        this.vartype = vartype;
        this.nameExpr = nameexpr;
        this.init = init;
        this.name = nameexpr.name;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitVarDef(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateVariable(this, d);
    }

    public VarKind dimKind = VarKind.localVar;

    public DeclVarSymbol getDeclVarSymbol()
    {
        return (DeclVarSymbol) symbol;
    }

    public boolean assignEffective = true;
}
