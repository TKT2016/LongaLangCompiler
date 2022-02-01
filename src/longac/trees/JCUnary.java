package longac.trees;


import longac.lex.TokenKind;

import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** 一元运算表达式 */
public class JCUnary extends JCExpression
{
    public TokenKind opcode;
    public JCExpression expr;
    public JCUnary(TokenKind opcode, JCExpression arg) {
        this.opcode = opcode;
        this.expr = arg;
    }
    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitUnary(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateUnary(this, d);
    }
}
