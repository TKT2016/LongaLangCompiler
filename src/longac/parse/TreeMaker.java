
package longac.parse;

import longac.diagnostics.SourceLog;
import longac.lex.Token;
import longac.trees.*;
import longac.lex.TokenKind;

import java.util.ArrayList;

import longac.trees.JCTree;

/** 抽象语法树工厂 */
class TreeMaker
{
    public SourceLog log;

    /** 当前位置 */
    public int pos= SourceLog.NOPOS;

    /* 当前行号*/
    public int line = 1 ;

    public TreeMaker(SourceLog log ) {
        this.log = log;
    }

    /** 给pos和line赋值并返回自己以便连续调用 */
    public TreeMaker at(int pos,int line) {
        this.pos = pos;
        this.line = line;
        return this;
    }

    public TreeMaker at(Token token) {
        return at(token.pos,token.line);
    }

    void initTree(JCTree tree)
    {
        tree.log = log;
        tree.pos = pos;
        tree.line = line;
    }

    public JCFileTree TopLevel(ArrayList<JCTree> defs) {
        JCFileTree tree = new JCFileTree(defs);
        initTree(tree);
        return tree;
    }

    /** 创建JCPackage */
    public JCPackage PackageDecl(JCExpression pid) {
        JCPackage tree = new JCPackage( pid);
        initTree(tree);
        return tree;
    }

    /** 创建JCImport,需要判断是否有'*' */
    public JCImport Import(JCExpression qualid, boolean isStatic,boolean isImportChain)
    {
        boolean isPackageStar =false;
        JCExpression expression2 = qualid;
        /* 需要判断是否以导入'*'结尾 */
        if(qualid instanceof JCFieldAccess)
        {
            JCFieldAccess jcFieldAccess=(JCFieldAccess)qualid;
            if(jcFieldAccess.name=="*")
            {
                isPackageStar = true;
                expression2 = jcFieldAccess.selected;
            }
        }
        return Import(expression2,isPackageStar,isStatic,isImportChain);
    }

    /** 创建JCImport */
    public JCImport Import(JCTree qualid,boolean isPackageStar, boolean isStatic,boolean isImportChain) {
        JCImport tree = new JCImport(qualid,isPackageStar,isStatic,isImportChain);
        initTree(tree);
        return tree;
    }

    /* 创建JCClassDecl */
    public JCClassDecl ClassDef(String name, ArrayList<JCTree> defs)
    {
        JCClassDecl tree = new JCClassDecl(name, defs);
        initTree(tree);
        return tree;
    }

    /* 创建JCMethodDecl */
    public JCMethodDecl MethodDef(String name, JCExpression restype, ArrayList<JCVariableDecl> params, JCBlock body)
    {
        JCMethodDecl tree = new JCMethodDecl(name, restype, params, body);
        initTree(tree);
        return tree;
    }

    /** 创建类型声明 JCVariableDecl */
    public JCVariableDecl VarDef( JCExpression vartype,JCIdent name,  JCExpression init) {
        JCVariableDecl tree = new JCVariableDecl(  vartype, name,init);
        initTree(tree);
        return tree;
    }

    /** 创建语句块JCBlock */
    public JCBlock Block( ArrayList<JCStatement> stats,int endLine) {
        JCBlock tree = new JCBlock( stats);
        initTree(tree);
        tree.endLine = endLine;
        return tree;
    }

    /** 创建while循环 */
    public JCWhile WhileLoop(JCExpression cond, JCStatement body) {
        JCWhile tree = new JCWhile(cond, body);
        initTree(tree);
        return tree;
    }

    /** 创建JCForLoop循环 */
    public JCForLoop ForLoop()
    {
        JCForLoop tree = new JCForLoop();
        initTree(tree);
        return tree;
    }

    /** 创建JCForLoop循环 */
    public JCForLoop ForLoop(JCStatement init,
                             JCExpression cond,
                             JCExpression step,
                             JCStatement body)
    {
        JCForLoop tree = new JCForLoop(init, cond, step, body);
        initTree(tree);
        return tree;
    }

    /** 创建JCIf */
    public JCIf If(JCExpression cond, JCStatement thenpart, JCStatement elsepart) {
        JCIf tree = new JCIf(cond, thenpart, elsepart);
        initTree(tree);
        return tree;
    }

    /* 创建表达式语句 JCExpressionStatement */
    public JCExpressionStatement Exec(JCExpression expr) {
        JCExpressionStatement tree = new JCExpressionStatement(expr);
        initTree(tree);
        return tree;
    }

    /** 创建JCBreak语法树 */
    public JCBreak Break( ) {
        JCBreak tree = new JCBreak();
        initTree(tree);
        return tree;
    }

    /** 创建JCContinue语法树 */
    public JCContinue Continue() {
        JCContinue tree = new JCContinue();
        initTree(tree);
        return tree;
    }

    /** 创建JCReturn语法树 */
    public JCReturn Return(JCExpression expr) {
        JCReturn tree = new JCReturn(expr);
        initTree(tree);
        return tree;
    }

    public JCMethodInvocation Apply(JCExpression fn, ArrayList<JCExpression> args)
    {
        JCMethodInvocation tree = new JCMethodInvocation(fn, args);
        initTree(tree);
        return tree;
    }

    public JCNewClass NewClass(JCExpression clazz, ArrayList<JCExpression> args)
    {
        JCNewClass tree =new JCNewClass(  clazz, args) ;
        initTree(tree);
        return tree;
    }

    public JCNewList NewList(ArrayList<JCExpression> elems)
    {
        JCNewList tree = new JCNewList(elems);
        initTree(tree);
        return tree;
    }

    public JCParens Parens(JCExpression expr) {
        JCParens tree = new JCParens(expr);
        initTree(tree);
        return tree;
    }

    public JCAssign Assign(JCExpression lhs, JCExpression rhs) {
        JCAssign tree = new JCAssign(lhs, rhs);
        initTree(tree);
        return tree;
    }
/*
    public JCChain Chain( JCExpression master, ArrayList<JCExpression> nodes) {
        JCChain tree = new JCChain(master, nodes);
        initTree(tree);
        return tree;
    }*/

    public JCChain Chain( ArrayList<JCExpression> nodes) {
        JCChain tree = new JCChain( nodes);
        initTree(tree);
        return tree;
    }

    public JCUnary Unary(TokenKind opcode, JCExpression arg) {
        JCUnary tree = new JCUnary(opcode, arg);
        initTree(tree);
        return tree;
    }

    public JCExpression Binary(TokenKind opcode, JCExpression lhs, JCExpression rhs) {
        if(lhs!=null) {
            JCBinary tree = new JCBinary(opcode, lhs, rhs);
            initTree(tree);
            return tree;
        }
        else
        {
            return Unary(opcode,rhs);
        }
    }
/*
    public JCArrayAccess ArrayAccess(JCExpression indexed, JCExpression index) {
        JCArrayAccess tree = new JCArrayAccess(indexed, index);
        initTree(tree);
        return tree;
    }*/

    public JCFieldAccess FieldAccess(JCExpression selected, String selector) {
        JCFieldAccess tree = new JCFieldAccess(selected, selector);
        initTree(tree);
        return tree;
    }

    public JCIdent Ident(String name) {
        JCIdent tree = new JCIdent(name);
        initTree(tree);
        return tree;
    }

    public JCLiteral Literal( TokenKind literalKind, Object value) {
        JCLiteral tree = new JCLiteral(literalKind, value);
        initTree(tree);
        return tree;
    }

    /** 创建基本类型语法树实例 */
    public JCPrimitiveTypeTree PrimitiveType(TokenKind kind) {
        JCPrimitiveTypeTree tree = new JCPrimitiveTypeTree(kind);
        initTree(tree);
        return tree;
    }
/*
    public JCArrayTypeTree TypeArray(JCExpression elemtype) {
        JCArrayTypeTree tree = new JCArrayTypeTree(elemtype);
        initTree(tree);
        return tree;
    }*/

    public JCErroneous Erroneous() {
        return Erroneous(new ArrayList<>());
    }

    public JCErroneous Erroneous(ArrayList<? extends JCTree> errs) {
        JCErroneous tree = new JCErroneous(errs);
        tree.log= log;
        tree.pos = pos;
        tree.line = line;
        return tree;
    }
}
