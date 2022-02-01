package longac.parse;

import jdk.nashorn.internal.ir.ExpressionStatement;
import longac.lex.Token;
import longac.lex.TokenKind;
import longac.trees.*;

import java.util.ArrayList;
import static longac.lex.TokenKind.*;

public class ExprTreeParser extends ParserBase{
    protected TermParser termParser;
    public ExprTreeParser(JCParser jcparser)
    {
       super(jcparser);
       this.termParser= jcparser.termParser;
    }

    /**
     *  {@literal
     *  Expression = Expression1 [ExpressionRest]
     *  ExpressionRest = [AssignmentOperator Expression1]
     *  Type = Type1
     *  TypeNoParams = TypeNoParams1
     *  StatementExpression = Expression
     *  ConstantExpression = Expression
     *  }
     */
    protected JCExpression parseExpression() {
        Token posToken = jcparser.token;
        JCExpression t;
        if(jcparser.isChainIdent() )
        {
            t = chain(posToken);
            return t;
        }

        if(jcparser.tkind==AT)
        {
            //Token colonToken = jcparser.token;
            //jcparser.nextToken();
            //if(jcparser.tkind== AT)
            //{
                t = chain(posToken);
                return t;
           // }
          //  else
           // {
          //      error(jcparser.token,"调用链缺少左括号");
          //  }
        }

        t = parseExprAndOr();
        return t;
    }

    JCExpressionStatement chainStmt()
    {
        Token posToken = jcparser.token;
        JCChain jcChain = chain(posToken);
        jcparser.accept(SEMI);
        JCExpressionStatement  expressionStatement = maker.at(posToken).Exec(jcChain);
        return expressionStatement;
    }

    JCChain chain(Token posToken)
    {
        if(jcparser.tkind== AT)
            jcparser.nextToken();
        if(jcparser.tkind== COLON)
            jcparser.nextToken();
        ArrayList<JCExpression> nodes = new ArrayList<>();
        JCIdent startIdent = maker.at(jcparser.token).Ident( jcparser.token.identName);
        nodes.add(startIdent);
        jcparser.nextToken();
       // jcparser.accept(AT);
       // jcparser.accept(COLON);

        while (jcparser.tkind!=EOF && jcparser.tkind!= SEMI && jcparser.tkind!= RBRACE )
        {
            JCExpression expression = parseExpression();
            if(expression!=null)
                nodes.add(expression);
            else
                break;
        }
        //jcparser.accept(RBRACE);
        return maker.at(posToken).Chain( nodes);
    }

    /** 分析与或逻辑表达式 */
    JCExpression parseExprAndOr()
    {
        Token posToken =  jcparser.token;
        JCExpression expression = parseExprCompare();
        while(jcparser.tkind == TokenKind.AND || jcparser.tkind == TokenKind.OR)
        {
            TokenKind op= jcparser.tkind;
            jcparser.nextToken();
            JCExpression rightExpr = parseExprCompare();
            expression = maker.at(posToken).Binary(op, expression , rightExpr);
        }
        return expression;
    }

    /** 分析比较达式 */
    JCExpression parseExprCompare()
    {
        Token posToken = jcparser.token;
        JCExpression expression = parseExprAddSub();
        while(jcparser.tkind == EQEQ || jcparser.tkind == NOTEQ || jcparser.tkind == LT
                || jcparser.tkind == GT|| jcparser.tkind == LTEQ|| jcparser.tkind == GTEQ)
        {
            TokenKind op= jcparser.tkind;
            jcparser.nextToken();
            JCExpression rightExpr = parseExprAddSub();
            expression = maker.at(posToken).Binary(op, expression , rightExpr);
        }
        return expression;
    }

    /** 分析加减表达式*/
    protected JCExpression parseExprAddSub()
    {
        Token posToken =  jcparser.token;
        JCExpression expression = parseExprMulDiv();
        while(jcparser.tkind == TokenKind.ADD || jcparser.tkind == TokenKind.SUB)
        {
            TokenKind op= jcparser.tkind;
            jcparser.nextToken();
            JCExpression rightExpr = parseExprMulDiv();
            expression = maker.at(posToken).Binary(op, expression , rightExpr);
        }
        return expression;
    }

    /** 分析乘除表达式*/
    JCExpression parseExprMulDiv()
    {
        Token posToken =  jcparser.token;
        JCExpression expression = parseExprNot();
        while(jcparser.tkind == TokenKind.MUL || jcparser.tkind == TokenKind.DIV)
        {
            TokenKind op= jcparser.tkind;
            jcparser.nextToken();
            JCExpression rightExpr = parseExprNot();
            expression = maker.at(posToken).Binary(op, expression , rightExpr);
        }
        return expression;
    }

    /** 分析否逻辑表达式*/
    JCExpression parseExprNot()
    {
        if(jcparser.tkind== TokenKind.NOT)
        {
            Token posToken =  jcparser.token;
            TokenKind op = jcparser.tkind;
            jcparser.nextToken();
            JCExpression rightExpr = parseComplex();
            return maker.at(posToken).Unary(op, rightExpr);
        }
        else
        {
            return parseComplex();
        }
    }

    /** 分析组合表达式 */
    JCExpression parseComplex()
    {
        Token posToken = jcparser.token;
        JCExpression expression = factor();
        while (jcparser.tkind!=EOF)
        {
            switch (jcparser.tkind) {
                case DOT:
                    expression = termParser.fieldAccess(expression,posToken);
                    break;
                case LPAREN:
                    expression = termParser.methodInvocation(expression,posToken);
                    break;
                //case LBRACKET:
                //    expression = termParser.arrayAccess(expression,posToken);
                default:
                    return expression;
            }
        }
        return expression;
    }

    JCExpression factor() {
        switch (jcparser.tkind) {
            case INTLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
               return termParser.literal();
            case LPAREN:
                return parens();
            case LBRACKET:
                return newList();
            case NEW:
                return parseNew();
            case THIS:
                return termParser.parseIdent();
            case VOID:
            case INT:
            case BOOLEAN:
            case IDENTIFIER:
                return parseTypeOrArrayAccess();
            default:
                return null; //return jcparser.illegal();
        }
    }

    /** 括号表达式
     * ParExpression = "(" Expression ")"
     */
    JCParens parens() {
        int pos = jcparser.token.pos;
        int line =  jcparser.token.line;
        jcparser.accept(LPAREN,false);
        JCExpression t = parseExpression();
        jcparser.accept(RPAREN,false);
        return maker.at(pos,line).Parens(t);
    }

    JCExpression parseTypeNoArray()
    {
        Token posToken = jcparser.token;
        JCExpression expr;
        switch (jcparser.tkind) {
            case VOID:
            case INT:
            case BOOLEAN:
                expr = primitiveType();
                break;
            case IDENTIFIER:
                expr = termParser.parseIdent();
                break;
            default:
                error(posToken,"非法的类型");
                jcparser.nextToken();
                return null;
        }
        while (jcparser.tkind==DOT)
        {
            expr = termParser.fieldAccess(expr,posToken);
        }
        return expr;
    }

    JCExpression parseType() {
        Token posToken = jcparser.token;
        JCExpression expr = parseTypeNoArray();
        if(expr==null)
            expr = maker.at(posToken).Erroneous();

        /*if (jcparser.tkind == LBRACKET) {
            jcparser.nextToken();
            jcparser.accept(RBRACKET);
            expr = maker.at(posToken).TypeArray(expr);
        }*/
        return expr;
    }

    JCExpression parseTypeOrArrayAccess() {
        Token posToken = jcparser.token;
        JCExpression expr = parseTypeNoArray();
        if(expr==null)
            expr = maker.at(posToken).Erroneous();
/*
        if (jcparser.tkind == LBRACKET) {
            jcparser.nextToken();
            if (jcparser.tkind == RBRACKET) {
                jcparser.nextToken();
                expr = maker.at(posToken).TypeArray(expr);
            }
            else
            {
                JCExpression indexExp = parseExpression();
                jcparser.accept(RBRACKET);
                expr = maker.at(posToken).ArrayAccess(expr,indexExp);
            }
        }*/
        return expr;
    }

    /** 分析赋值表达式 */
    JCAssign assign(JCExpression leftExpr,Token startToken)
    {
        jcparser.nextToken();
        JCExpression right = parseExpression() ;
        return maker.at(startToken).Assign(leftExpr, right);
    }

    /** 分析基本类型
     * BasicType = INT |  BOOLEAN | VOID
     */
    JCPrimitiveTypeTree primitiveType() {
        JCPrimitiveTypeTree t = maker.at(jcparser.token).PrimitiveType(jcparser.tkind);
        jcparser.nextToken();
        return t;
    }

    /** 分析new表达式
     */
    JCExpression parseNew() {
        Token posToken = jcparser.token;
        jcparser.accept(NEW);
        JCExpression t = parseTypeNoArray();
        if (jcparser.tkind == LPAREN) {
            ArrayList<JCExpression> args = termParser.arguments();
            return maker.at(posToken).NewClass(t, args);
        } else {
            error(posToken,"需要 '(' ");
            ArrayList<JCExpression> args =  new ArrayList<>();
            return maker.at(posToken).NewClass(t, args);
        }
    }

    /** ArrayCreatorRest = "[" ( "]" BracketsOpt ArrayInitializer
     *                         | Expression "]" {[Annotations]  "[" Expression "]"} BracketsOpt )
     */
    JCExpression newList() {
        Token posToken = jcparser.token;
        jcparser.accept(LBRACKET);
        ArrayList<JCExpression> expressions  = arrayInitializerElements();
        jcparser.accept(RBRACKET);
        JCNewList newArrayExpr = maker.at(posToken).NewList(expressions);
        return newArrayExpr;
    }

    /** 数组初始化元素 */
    ArrayList<JCExpression> arrayInitializerElements() {
       // jcparser.accept(LBRACE,false);
        ArrayList<JCExpression> elems = new ArrayList<>();
        if (jcparser.tkind == COMMA) {
            jcparser.nextToken();
        } else if (jcparser.tkind != RBRACE) {
            JCExpression firstElement = parseExpression();
            if(firstElement!=null)
                elems.add(firstElement);
            while (jcparser.tkind == COMMA) {
                jcparser.nextToken();
                if (jcparser.tkind == RBRACE) break;
                JCExpression element = parseExpression();
                if(element!=null)
                    elems.add(element);
            }
        }
        //jcparser.accept(RBRACE,false);
        return elems;
    }

    /** 分析变量声明语句 */
    JCVariableDecl variableDecl(JCExpression type,Token posToken) {
        JCIdent name = termParser.parseIdent();
        JCVariableDecl statement = variableDecl(type,name,posToken);
        return statement;
    }

    /** 分析变量声明语句 */
    JCVariableDecl variableDecl(JCExpression type, JCIdent name,Token posToken)
    {
        if(!(type instanceof JCIdent
                ||type instanceof JCFieldAccess
               // || type instanceof JCArrayTypeTree
                || type instanceof JCPrimitiveTypeTree))
        {
            error(posToken,"表达式不能作为类型");
        }

        JCExpression init = null;
        if (jcparser.tkind == TokenKind.EQ) //如果是'=',变量有初始值
        {
            jcparser.nextToken();
            init = parseExpression();
        }
        JCVariableDecl result = maker.at(posToken).VarDef(type,name, init);
        return result;
    }
}
