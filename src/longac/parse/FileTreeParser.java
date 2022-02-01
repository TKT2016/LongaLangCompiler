package longac.parse;

import longa.lang.LongaSpecialNames;
import longac.lex.Token;
import longac.lex.TokenKind;
import longac.trees.*;

import java.util.ArrayList;
import java.util.HashMap;

/** 文件package,import,class 语法树分析器 */
public class FileTreeParser extends ParserBase
{
    public FileTreeParser(JCParser jcparser)
    {
        super(jcparser);
    }

    /** CompilationUnit = [ PACKAGE Qualident ";"] {ImportDeclaration} {TypeDeclaration}
     */
    public JCFileTree parseCompilationUnit() {
        Token firstToken = jcparser.token;
        ArrayList<JCTree> defs = new ArrayList<>();
        try {
            /* 分析 包定义 */
            if (jcparser.tkind == TokenKind.PACKAGE) {
                JCPackage packageDecl = parsePackageDecl();
                defs.add(packageDecl);
            }

            /* 分析 import */
            while (jcparser.tkind == TokenKind.IMPORT) {
                JCTree importTree = importDeclaration();
                if (importTree != null)
                    defs.add(importTree);
            }
            /* 分析 class */
            while (jcparser.tkind != TokenKind.EOF) {
                JCTree def = classDeclaration();
                if (def != null) {
                    defs.add(def);
                }
            }
        }
        catch (ParseEOFError ex){

        }
        JCFileTree toplevel = jcparser.maker.at(firstToken).TopLevel(defs);
        return toplevel;
    }

    /** 分析包语句 PACKAGE ...;
     * JCPackage = PACKAGE Ident { "." Ident } ";"
     */
    JCPackage parsePackageDecl()
    {
        Token packageToken = jcparser.token;//记录PACKAGE词法标记
        jcparser.nextToken();//移过PACKAGE，读取下一个词法标记
        JCExpression packageName = jcparser.termParser.parseQualifiedName();//分析包名
        jcparser.accept(TokenKind.SEMI); //验证末尾是否是分号
        //创建PACKAGE语法树实例
        JCPackage pd = jcparser.maker.at(packageToken).PackageDecl(packageName);
        return pd;
    }

    /** 分析导入语句 import ...;
     * ImportDeclaration = IMPORT Ident { "." Ident } [ "." "*" ] ";"
     * */
    JCImport importDeclaration() {
        Token importToken = jcparser.token;//记录IMPORT词法标记
        jcparser.nextToken();//读取下一个
        boolean isStatic =false;
        boolean isImportChain =false;
        if(jcparser.tkind==TokenKind.STATIC)
        {
            isStatic=true;
            jcparser.nextToken();//读取下一个
        }
        else if(jcparser.tkind==TokenKind.IDENTIFIER &&jcparser.token.identName.equals("chain" ))
        {
            isImportChain=true;
            jcparser.nextToken();//读取下一个
        }
        JCExpression pid = this.parseQualifiedNameStar();//分析导入的名称
        jcparser.accept(TokenKind.SEMI);//验证末尾是否是分号
        JCImport jcImport = jcparser.maker.at(importToken).Import(pid,isStatic,isImportChain);
        if(isImportChain)
        {
           JCFieldAccess fieldAccess = (JCFieldAccess)jcImport.qualid;
           String chainName = fieldAccess.name;
           if(chainName.endsWith(LongaSpecialNames.chainTypeSuffix))
           {
               chainName = chainName.substring(0,chainName.length()-LongaSpecialNames.chainTypeSuffix.length());
               jcparser.importedChainMap.put(chainName,jcImport);
           }
        }
        return jcImport;
    }

    /**
     * 分析导入的名称
     *  Ident { "." Ident } [ "." "*" ] */
     JCExpression parseQualifiedNameStar() {
         Token tempToken = jcparser.token; //记录首个token
         JCExpression t =jcparser.termParser.parseIdentName();//分析开头名称
        /* 当前token是'*'时循环 */
        while (jcparser.tkind == TokenKind.DOT) {
            jcparser.nextToken();
            if(jcparser.tkind == TokenKind.MUL) {
                t = jcparser.maker.at(tempToken).FieldAccess(t, "*");
                jcparser.nextToken();
                break;//如果当前是'*',表明已经到了结尾
            }
            else {
                String name = jcparser.termParser.identName();
                t = jcparser.maker.at(tempToken).FieldAccess(t, name);
            }
        }
        return t;
    }

    /**
     * 分析类(class)语法树
     * ClassDeclaration = CLASS Ident
     *                      ClassBody
     */
    protected JCClassDecl classDeclaration() {
        /* 如果当前token不是CLASS,报告错误,一直移到到CLASS词法标记 */
        if(jcparser.tkind!=TokenKind.CLASS)
        {
            jcparser.expectKind(TokenKind.CLASS);
            jcparser.skipTo(TokenKind.CLASS);
        }
        Token tempToken = jcparser.token; //记录类开始词法标记
        jcparser.accept(TokenKind.CLASS);//验证关键字'class'
        String name = jcparser.termParser.identName();//分析类名称
        ArrayList<JCTree> defs = classBody(name);//分析类结构
        JCClassDecl result = jcparser.maker.at(tempToken).ClassDef(name, defs);
        return result;
    }

    /** 分析类结构
     * ClassBody = "{" {classMemberDeclaration} "}"
     */
    ArrayList<JCTree> classBody(String className) {
        jcparser.accept(TokenKind.LBRACE);//验证左花括号'{'
        ArrayList<JCTree> defs = new ArrayList<>();
        while (jcparser.tkind != TokenKind.RBRACE
                && jcparser.tkind != TokenKind.EOF) {
            JCTree tree = classMemberDeclaration(className);//分析类成员
            defs.add(tree);
        }
        jcparser.accept(TokenKind.RBRACE);//验证右花括号'}'
        return defs;
    }

    /** 分析类成员
     *  ClassMemberDeclaration =
     *    Contructor
     *    Method
     *    Field
     */
    JCTree classMemberDeclaration(String className) {
        /** 忽略多余的分号 ';' */
        if (jcparser.tkind == TokenKind.SEMI)
            jcparser.skipSEMI();

        Token firstToken = jcparser.token;//记录开始token
        boolean isClassName =(jcparser.tkind == TokenKind.IDENTIFIER && jcparser.token.identName.equals(className));
        JCExpression type = jcparser.exprParser.parseType();

        /* 判断是否是构造函数 */
        if (jcparser.tkind == TokenKind.LPAREN &&isClassName && type instanceof JCIdent)
        {
           return jcparser.methodTreeParser.contructorDeclarator(firstToken);
        }

        JCIdent nameExpr = jcparser.termParser.parseIdent(); //分析成员名称
        if (jcparser.tkind == TokenKind.LPAREN)
        {
            /* 如果当前token类型是'(',表明它是方法 */
            return jcparser.methodTreeParser.methodDeclarator(firstToken, type, nameExpr.name);
        }
        else
        {
            /* 否则是字段 */
            JCTree def = jcparser.exprParser.variableDecl( type, nameExpr,firstToken);
            return def;
        }
    }
}
