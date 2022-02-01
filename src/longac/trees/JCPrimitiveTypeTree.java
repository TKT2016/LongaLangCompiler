package longac.trees;

import longac.lex.TokenKind;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

public class JCPrimitiveTypeTree extends JCExpression
{
    public TokenKind kind;

    public JCPrimitiveTypeTree(TokenKind kind) {
        this.kind = kind;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitTypeIdent(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translatePrimitiveType(this, d);
    }

    @Override
    public boolean isEffective()
    {
        return true;
    }
}