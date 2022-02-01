package longac._core;

import longac.diagnostics.SimpleLog;
import tools.collectx.ArrayListUtil;
import longac.lex.TokenKind;
import longac.utils.CompileContext;
import longac.visitors.TreeScanner;
import longac.trees.*;
public class CompletenessScanner extends TreeScanner<Object>
{
    SimpleLog log;
    CompileContext context;

    public CompletenessScanner(CompileContext context) {
        this.context = context;
        log =context.log;
    }

    @Override
    public void visitBlock(JCBlock tree,  Object arg) {
        for(JCTree stmt:tree.stats)
        {
            if(stmt instanceof JCBlock)
            {
                stmt.error("代码块内禁止嵌套代码块");
            }
            stmt.scan(this,context);
        }
    }

    @Override
    public void visitWhileLoop(JCWhile tree, Object arg)
    {
        if(tree.cond==null)
            tree.error("while循环语句缺少条件表达式");
        else
            tree.cond.scan(this,arg);

        if(tree.body==null)
            tree.error("while循环语句缺少循环体");
        else
            tree.body.scan(this,arg);
    }

    @Override
    public void visitForLoop(JCForLoop tree, Object arg)
    {
        if(tree.init!=null) {
            if(tree.init instanceof JCReturn)
                tree.init.error("for循环语句初始化部分禁止使用return语句");
            tree.init.scan(this,arg);
        }

        /*if(tree.init!=null) {
            for (JCStatement statement : tree.init) {
                statement.scan(this, arg);
            }
        }*/
        if (tree.cond != null) {
            tree.cond.scan(this,arg);
        }
        if(tree.step!=null) {
            tree.step.scan(this,arg);
        }

        if(tree.body==null)
            tree.init.error("for循环语句缺少循环体");
        else
            tree.body.scan(this,arg);
    }

    @Override
    public void visitIf(JCIf tree, Object arg)
    {
        if(tree.cond==null)
            tree.error("if语句缺少条件表达式");
        else
            tree.cond.scan(this,arg);

        if(tree.thenpart==null)
            tree.error("if语句缺少执行语句");
        else
            tree.thenpart.scan(this,arg);

        if(tree.elsepart!=null)
            tree.elsepart.scan(this,arg);
    }

    @Override
    public void visitExec(JCExpressionStatement tree, Object arg)
    {
        tree.expr.scan(this,arg);
    }

    @Override
    public void visitReturn(JCReturn tree, Object arg)
    {
        if(tree.expr!=null)
            tree.expr.scan(this,arg);
    }

    @Override
    public void visitSelect(JCFieldAccess tree, Object arg)
    {
        return;
    }

    @Override
    public void visitMethodInvocation(JCMethodInvocation tree, Object arg) {
        return;
    }

    @Override
    public void visitNewClass(JCNewClass tree, Object arg)
    {
        return;
    }

    @Override
    public void visitNewArray(JCNewList tree, Object arg)
    {
        visitExprs(  tree.elems,arg);
    }

    @Override
    public void visitParens(JCParens tree, Object arg)
    {
        if(tree.expr==null)
            tree.error("括号内缺少表达式");
        else
            tree.expr.scan(this,arg);
    }

    @Override
    public void visitAssign(JCAssign tree, Object arg)
    {
        if(tree.left==null)
            tree.error("赋值语句左边缺少表达式");
        else
            tree.left.scan(this,arg);
        if(tree.right==null)
            tree.error("赋值语句右边缺少表达式");
        else
            tree.right.scan(this,arg);
    }

    @Override
    public void visitUnary(JCUnary tree, Object arg)
    {
        TokenKind opcode = tree.opcode;
        if(!(opcode== TokenKind.ADD ||opcode== TokenKind.SUB ||opcode== TokenKind.NOT))
            tree.error("单目表达式前缀只能为'+','-','!'");
        if(tree.expr==null)
            tree.error("单目后边缺少表达式");
        else
            tree.expr.scan(this,arg);
    }

    @Override
    public void visitBinary(JCBinary tree, Object arg)
    {
        if(tree.left==null)
            tree.error("计算表达式左边缺少表达式");
        else
            tree.left.scan(this,arg);
        if(tree.right==null)
            tree.error("计算表达式右边缺少表达式");
        else
            tree.right.scan(this,arg);
    }
/*
    @Override
    public void visitIndexed(JCArrayAccess tree, Object arg)
    {
        if(tree.indexed==null)
            tree.error("数组访问表达式左边缺少数组");
        else
            tree.indexed.scan(this,arg);
        if(tree.index==null)
            tree.error("数组访问表达式左边缺少索引");
        else
            tree.index.scan(this,arg);
    }*/

    @Override
    public void visitIdent(JCIdent tree, Object arg)
    {
        if(tree.name==null)
            tree.error("标识符不能为空");
    }

    @Override
    public void visitLiteral(JCLiteral tree, Object arg)
    {
        return;
    }

    @Override
    public void visitTypeIdent(JCPrimitiveTypeTree tree, Object arg) {
        return;
    }
/*
    @Override
    public void visitTypeArray(JCArrayTypeTree tree, Object arg)
    {
        if(tree.elemtype==null)
            tree.error("数组声明缺少类型");
        else
            tree.elemtype.scan(this,arg);
    }*/
}
