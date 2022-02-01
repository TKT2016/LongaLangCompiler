package longac.trees;

import longac.lex.TokenKind;

import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** 二元运算表达式 */
public class JCBinary extends JCExpression
{
    public TokenKind opcode;
    public JCExpression left;
    public JCExpression right;

    public JCBinary(TokenKind opcode, JCExpression lhs, JCExpression rhs)
    {
        this.opcode = opcode;
        this.left = lhs;
        this.right = rhs;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitBinary(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateBinary(this, d);
    }

    public boolean isStringAppend = false;

}
