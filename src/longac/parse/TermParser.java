package longac.parse;

import longac.lex.Token;
import longac.lex.TokenKind;
import longac.trees.JCExpression;
import longac.trees.JCIdent;
import longac.trees.JCLiteral;
import longac.trees.JCMethodInvocation;
import longac.utils.Assert;
import tools.jvmx.NamesTexts;
import java.util.ArrayList;
import static longac.lex.TokenKind.*;

public class TermParser extends ParserBase{

    public TermParser(JCParser jcparser)
    {
        super(jcparser);
    }

    /** 分项标识符 */
    JCExpression parseIdentName()
    {
        Token temp = jcparser.token;
        String name = identName();
        JCExpression t =jcparser.maker.at(temp).Ident(name);
        return t;
    }

    /** 分析标识符名称 */
    String identName( ) {
        if (jcparser.tkind == IDENTIFIER) {
            String name = jcparser.token.identName;
            jcparser.nextToken();
            return name;
        }
        else {
            jcparser.expectKind(IDENTIFIER);
            return NamesTexts.error;
        }
    }

    /** 分析标识符名称
     * Ident = IDENTIFIER
     */
    String parseName() {
        if (jcparser.tkind == IDENTIFIER) {
            String name = jcparser.token.identName;
            jcparser.nextToken();
            return name;
        }
        else if (jcparser.tkind == THIS) {
                String name = jcparser.token.identName;
                jcparser.nextToken();
                return name;
        }
        else {
            jcparser.accept(IDENTIFIER,false);
            return NamesTexts.error;
        }
    }

    JCIdent parseIdent()
    {
        Token tempToken = jcparser.token;
        if (jcparser.tkind == IDENTIFIER) {
            String name = jcparser.token.identName;
            jcparser.nextToken();
            JCIdent jcIdent =jcparser.maker.at(tempToken).Ident(name);
            return jcIdent;
        }
        else if (jcparser.tkind == THIS) {
            String name = jcparser.token.identName;
            jcparser.nextToken();
            JCIdent jcIdent = jcparser.maker.at(tempToken).Ident(name);
            return jcIdent;
        }
        else
        {
            log.error(tempToken.pos,tempToken.line,"期望标识符或者'this'");
            JCIdent jcIdent =jcparser.maker.at(tempToken).Ident(null);
            return jcIdent;
        }
    }

    JCExpression parseQualifiedName( ) {
        JCExpression t = parseIdent();
        while (jcparser.tkind == DOT) {
            jcparser.nextToken();
            String name = parseName();
            t = jcparser.maker.at(jcparser.token.pos,jcparser.token.line).FieldAccess(t, name);
        }
        return t;
    }

    JCExpression fieldAccess(JCExpression expr,Token posToken) {
        if(jcparser.tkind==DOT)
        {
            jcparser.nextToken();
            String name = parseName();
            expr = jcparser.maker.at(posToken).FieldAccess(expr,name);
        }
        return expr;
    }

    /** 分析常量表达式
     * Literal =
     *     INTLITERAL
     *   | STRINGLITERAL
     *   | TRUE
     *   | FALSE
     */
    JCExpression literal( ) {
        Token posToken =jcparser.token;
        TokenKind kind = jcparser.tkind;
        Object value = null;
        switch (kind) {
            case INTLITERAL: {
                String valStr = jcparser.token.valueString;
                try {
                    value = Integer.parseInt(valStr);
                } catch (NumberFormatException ex) {
                    log.error(posToken.pos,posToken.line,"整数数值错误或者过大");
                    value = 0;
                }
                break;
            }
            case STRINGLITERAL:
                value = jcparser.token.valueString;
                break;
            case TRUE:
                value = true;
                break;
            case FALSE:
                value = false;
                break;
            default:
                Assert.error();
        }
        JCLiteral literal = jcparser.maker.at(posToken).Literal(kind, value);
        jcparser.nextToken();
        return literal;
    }
/*
    JCExpression arrayAccess(JCExpression expr,Token posToken) {
        if (jcparser.tkind == LBRACKET) {
            jcparser.nextToken();
            JCExpression indexExp = jcparser.exprParser.parseExpression();
            jcparser.accept(RBRACKET);
            expr = jcparser.maker.at(posToken).ArrayAccess(expr, indexExp);
        }
        return expr;
    }*/

    /** 分析函数调用表达式 */
    JCMethodInvocation methodInvocation(JCExpression t, Token posToken) {
        ArrayList<JCExpression> args = arguments();
        JCMethodInvocation mi = jcparser.maker.at(posToken).Apply( t, args);
        return mi;
    }

    /** 分析函数参数
     *  Arguments = "(" [Expression { COMMA Expression }] ")"
     */
    protected ArrayList<JCExpression> arguments() {
        ArrayList<JCExpression> args = new ArrayList<>();
        jcparser.accept(LPAREN);
            if (jcparser.tkind != RPAREN) {
                JCExpression argExpr = jcparser.exprParser.parseExpression();
                if(argExpr!=null)
                    args.add(argExpr);
                while (jcparser.tkind == COMMA) {
                    jcparser.nextToken();
                    argExpr = jcparser.exprParser.parseExpression();
                    if(argExpr!=null)
                        args.add(argExpr);
                }
            }
            jcparser.accept(RPAREN,false);
        return args;
    }
}
