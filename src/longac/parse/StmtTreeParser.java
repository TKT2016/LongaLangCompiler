package longac.parse;

import longac.lex.Token;
import longac.lex.TokenKind;
import longac.trees.*;

import java.util.ArrayList;
import static longac.lex.TokenKind.*;

/** 语句分析器 */
public class StmtTreeParser extends ParserBase{
    protected ExprTreeParser exprParser;
    public StmtTreeParser(JCParser jcparser)
    {
        super(jcparser);
        this.exprParser = jcparser.exprParser;
    }

    /** 分析语句 ,从简单到复杂排列*/
    protected JCStatement parseStatement()
    {
        switch (jcparser.tkind) {
            case BREAK:
                return parseBreak();
            case CONTINUE:
                return parseContinue();
            case RETURN:
                return parseReturn();
            case WHILE:
                return parseWhile();
            case IF:
                return parseIf();
            case FOR:
                return parseFor();
            case LBRACE:
                return block();//分析语句块
            case INT:
            case BOOLEAN:
            case THIS:
           // case IDENTIFIER:
                return expressionStatement();//分析其它语句
            case SEMI:
                jcparser.nextToken();
                return parseStatement();
            case ELSE:
                log.error( jcparser.token.pos,jcparser.token.line,"ELSE缺少IF" );
                jcparser.nextToken();
                return parseStatement();
           case RBRACE:
               log.error( jcparser.token.pos,jcparser.token.line,"右大括号没有匹配的左大括号" );
               jcparser.nextToken();
               return parseStatement();
            case AT:
                return exprParser.chainStmt();
            case EOF:
                return null;
            default:
                if(jcparser.isChainIdent() )
                {
                    return exprParser.chainStmt();
                }
                else if(jcparser.tkind== IDENTIFIER)
                {
                    return expressionStatement();//分析其它语句
                }

                log.error( jcparser.token.pos,jcparser.token.line,"非法的表达式语句成分" );
                jcparser.nextToken();
                return parseStatement();
        }
    }


    /** 分析break语句
     * BREAK
     * */
    JCBreak parseBreak()
    {
        Token breakToken = jcparser.token; //记录关键字token
        jcparser.nextToken();//移到下一个
        jcparser.accept(SEMI); //验证分号
        JCBreak t = maker.at(breakToken).Break();
        return t;
    }

    /** 分析continue语句
     * CONTINUE
     * */
    JCContinue parseContinue()
    {
        Token continueToken = jcparser.token;//记录关键字token
        jcparser.nextToken();//移到下一个
        jcparser.accept(SEMI);//验证分号
        JCContinue t =  maker.at(continueToken).Continue();
        return t;
    }

    /**
     * 分析语句块
     * Block = "{" Statements "}"
     */
    protected JCBlock block( ) {
        Token tokenLBRACE = jcparser.token;
        jcparser.accept(LBRACE);//验证左大括号

        ArrayList<JCStatement> stats= new ArrayList<>();
        while (true)
        {
            TokenKind tokenKind=jcparser.tkind;
            if(tokenKind== EOF) break; //文件末尾,跳出循环
            if(tokenKind== RBRACE) break; //终止符'}',跳出循环
            JCStatement statement = parseStatement();//分析语句
            if(statement!=null)
            {
                stats.add(statement);
            }
        }
        JCBlock t = maker.at(tokenLBRACE).Block( stats, jcparser.token.line);
        t.endpos = jcparser.token.pos;
        jcparser.accept(RBRACE);//验证右大括号
        return t;
    }
    

    /** 分析if语句
     * IF ParExpression Statement [ELSE Statement]
     * */
    JCIf parseIf()
    {
        Token ifToken = jcparser.token;//记录if关键字token
        jcparser.nextToken();
        JCExpression cond =  exprParser.parens();//分析条件表达式
        JCStatement thenpart = parseStatement();//if条件表达式后面的语句
        JCStatement elsepart = null;//else部分可选
        if (jcparser.tkind == ELSE) {
            jcparser.nextToken();
            elsepart = parseStatement();//分项else部分语句
        }
        return maker.at(ifToken).If(cond, thenpart, elsepart);
    }

    /** 分析for循环
     * FOR "(" ForInitOpt ";" [Expression] ";" ForUpdateOpt ")" Statement
     * */
    JCForLoop parseFor()
    {
        /** 因为for结构比较复杂,首先创建一个For语法树实例 */
        Token forToken = jcparser.token;
        JCForLoop jcForLoop = maker.at(forToken).ForLoop();
        jcparser.nextToken();
        jcparser.accept(LPAREN);
        forControl(jcForLoop); //分析for循环控制部分
        jcparser.accept(RPAREN);
        jcForLoop.body = parseStatement();
        return jcForLoop;
    }

    /** 分析for控制部分 */
    void forControl(JCForLoop jcForLoop)
    {
        /* 分析初始化语句，它是以';'结尾 */
        if(jcparser.tkind!=SEMI)
            jcForLoop.init = parseStatement ();
        else
            jcparser.nextToken();
        /** 分项条件表达式，也是以';'结尾 */
        if(jcparser.tkind!=SEMI)
            jcForLoop.cond =  exprParser.parseExpression();
        jcparser.accept(SEMI);
        /** 分项更新语句,是以')'结尾 */
        if( jcparser.tkind != RPAREN)
            jcForLoop.step = exprParser.parseExpression();
    }

    /** 分析while循环
     * WHILE ParExpression Statement
     * */
    JCWhile parseWhile()
    {
        Token whileToken = jcparser.token;//记录while关键字token
        jcparser.nextToken();
        JCParens cond = exprParser.parens(); //分析括号表达式
        JCStatement body = parseStatement(); //分析循环体
        return maker.at(whileToken).WhileLoop(cond.expr, body);
    }

    /** 分析return语句
     * RETURN [Expression] ";"
     * */
    JCReturn parseReturn()
    {
        Token returnToken = jcparser.token; //记录关键字token
        jcparser.nextToken();// 读取下一个token
        JCExpression expr = null;
        /* 判断当前是否是分号,如果不是，读取表达式 */
        if(jcparser.tkind != SEMI)
            expr = exprParser.parseExpression();
        jcparser.accept(SEMI); //验证分号
        JCReturn t = maker.at(returnToken).Return(expr);
        return t;
    }

    /** 分析表达式语句
     * JCExpressionStatement ->
     *  expression ';'
     *  expression = expression ';'
     *  expression NAME ';'
     * */
    JCStatement expressionStatement()
    {
        //if(jcparser.token.line==16)
        //    Debuger.outln("215 :"+jcparser.token);
        Token posToken = jcparser.token;
        /** 分析开头表达式 */
        JCExpression expression = exprParser.parseExpression();
        if(jcparser.tkind== EQ)
        {
            /* 当前token是'='分析赋值表达式  */
            expression = exprParser.assign(expression,posToken);
        }
        else if (jcparser.tkind == IDENTIFIER)
        {
            /* 当前token是标识符分析变量声明表达式  */
            expression = exprParser.variableDecl(expression,posToken);
        }
        jcparser.accept(SEMI); //验证分号
        /* 检测表达式是否属于语句中正确类型 */
        if (expression instanceof JCAssign
                || expression instanceof JCMethodInvocation
                || expression instanceof JCNewClass
                || expression instanceof JCVariableDecl
        )
        {
            JCExpressionStatement statement = maker.at(posToken).Exec(expression);
            return statement;
        }
        else {
            error(posToken, "不是正确的表达式语句");
            return null;
        }
    }
}
