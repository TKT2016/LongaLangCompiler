package longac.parse;

import longac.lex.Token;
import tools.jvmx.NamesTexts;
import longac.trees.*;

import java.util.ArrayList;

import static longac.lex.TokenKind.*;

/** 方法树分析器 */
public class MethodTreeParser extends ParserBase{

   public MethodTreeParser(JCParser jcparser)
   {
       super(jcparser);
   }

    /** 分析方法*/
    protected JCMethodDecl methodDeclarator(Token firstToken, JCExpression type, String name)
    {
        ArrayList<JCVariableDecl> params = formalParameterList(); //参数列表
        JCBlock body = methodBody(); //函数体
        JCMethodDecl result =  jcparser.maker.at(firstToken).MethodDef( name, type, params, body);
        return result;
    }

    /** 分析构造函数 */
    protected JCMethodDecl contructorDeclarator(Token firstToken)
    {
        ArrayList<JCVariableDecl> params = formalParameterList();//参数列表
        JCBlock body = methodBody(); //函数体
        JCMethodDecl result =jcparser.maker.at(firstToken).MethodDef(NamesTexts.init, null, params, body);
        return result;
    }

    /** 分析参数列表 */
    ArrayList<JCVariableDecl> formalParameterList( ) {
        ArrayList<JCVariableDecl> params = new ArrayList<>();
        jcparser.accept(LPAREN);
        if ( jcparser.tkind != RPAREN) {
            addParsedParameter(params);
            while ( jcparser.tkind == COMMA) {
                jcparser.nextToken();
                addParsedParameter(params);
            }
        }
        jcparser.accept(RPAREN);
        return params;
    }

    /** 添加参数 */
    private void addParsedParameter(ArrayList<JCVariableDecl> params)
    {
        JCVariableDecl param = formalParameter();
        if (param.nameExpr != null) {
            params.add(param);
        }
    }

    /** 分析参数
     *  FormalParameter = Type name
     */
    JCVariableDecl formalParameter() {
        Token temp = jcparser.token;
        JCExpression type = jcparser.exprParser.parseType();//分析类型
        JCIdent name = jcparser.termParser.parseIdent(); //分析参数名称
        return jcparser.maker.at(temp).VarDef(type, name, null);
    }

    /** 分析函数体 */
    JCBlock methodBody()
    {
        JCBlock body = null;
        if ( jcparser.tkind == LBRACE) {
            body = jcparser.stmtTreeParser.block();
        } else {
            /* 友好提示 */
            log.error( jcparser.token.pos,jcparser.token.line,"缺少函数体");
        }
        return body;
    }
}
