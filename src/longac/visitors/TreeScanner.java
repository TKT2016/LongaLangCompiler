package longac.visitors;

import longac.utils.Assert;
import longac.trees.*;
import java.util.ArrayList;

public class TreeScanner<T>
{
    public void visit(ArrayList<JCFileTree> trees) {
        for(JCFileTree tree : trees)
        {
            tree.scan(this, null);
        }
    }

    // @Override
    public void visitCompilationUnit(JCFileTree compilationUnit , T arg) {
        for(JCTree tree : compilationUnit.defs )
        {
            tree.scan(this,arg);
        }
    }

   // @Override
    public void visitPackageDef(JCPackage jcPackageDecl, T arg)
    {
        return;
    }

   // @Override
    public void visitImport(JCImport tree, T arg)
    {
        return;
    }

   // @Override
    public void visitClassDef(JCClassDecl tree, T arg) {
        for(int i=0;i<tree.defs.size();i++)
        {
            tree.defs.get(i).scan(this, arg);
        }
    }

   // @Override
    public void visitMethodDef(JCMethodDecl tree, T arg)
    {
        tree.body.scan(this,arg );
    }

   // @Override
    public void visitVarDef(JCVariableDecl tree, T arg) {
            if(tree.init!=null) {
                tree.init.scan(this,arg);
            }
    }

   // @Override
    public void visitBlock(JCBlock tree,  T arg) {
        for(JCTree stmt:tree.stats)
        {
            stmt.scan(this,arg);
        }
    }

   // @Override
    public void visitWhileLoop(JCWhile tree, T arg)
    {
        tree.cond.scan(this,arg);
        tree.body.scan(this,arg);
    }

   // @Override
    public void visitForLoop(JCForLoop tree, T arg)
    {
        if(tree.init!=null)
            tree.init.scan(this,arg);
        if (tree.cond != null) {
            tree.cond.scan(this,arg);
        }
        if(tree.step!=null)
            tree.step.scan(this,arg);

        tree.body.scan(this,arg);
    }

   // @Override
    public void visitIf(JCIf tree, T arg)
    {
        tree.cond.scan(this,arg);
        tree.thenpart.scan(this,arg);
        if(tree.elsepart!=null)
            tree.elsepart.scan(this,arg);
    }

   // @Override
    public void visitExec(JCExpressionStatement tree, T arg)
    {
        visitExpr(tree.expr,arg);
    }

   // @Override
    public void visitBreak(JCBreak tree, T arg)    {
        return;
    }

   // @Override
    public void visitContinue(JCContinue tree, T arg)
    {
       return;
    }

   // @Override
    public void visitReturn(JCReturn tree, T arg)
    {
        visitExpr(tree.expr,arg);
    }

   // @Override
    public void visitMethodInvocation(JCMethodInvocation tree, T arg)
    {
        visitExprs(tree.args,arg);
    }

   // @Override
    public void visitNewClass(JCNewClass tree, T arg)
    {
        visitExprs(tree.args,arg);
    }

   // @Override
    public void visitNewArray(JCNewList tree, T arg)
    {
        visitExprs(tree.elems,arg);
    }

   // @Override
    public void visitParens(JCParens tree, T arg)
    {
        visitExpr(tree.expr,arg);
    }

   // @Override
    public void visitAssign(JCAssign tree, T arg)
    {
        visitExpr(tree.left,arg);
        visitExpr(tree.right,arg);
    }

   // @Override
    public void visitUnary(JCUnary tree, T arg)
    {
        visitExpr(tree.expr,arg);
    }

   // @Override
    public void visitBinary(JCBinary tree, T arg)
    {
        visitExpr(tree.left,arg);
        visitExpr(tree.right,arg);
    }

   // @Override
    public void visitSelect(JCFieldAccess tree, T arg)
    {
        return;
    }

    public void visitChain(JCChain tree, T arg)
    {
        return;
    }

   // @Override
    public void visitIdent(JCIdent tree, T arg)
    {
        return;
    }

   // @Override
    public void visitLiteral(JCLiteral tree, T arg)
    {
       return;
    }

   // @Override
    public void visitTypeIdent(JCPrimitiveTypeTree tree, T arg) {
        return;
    }
/*
   // @Override
    public void visitTypeArray(JCArrayTypeTree tree, T arg)
    {
        return;
    }*/

   // @Override
    public void visitErroneous(JCErroneous tree, T arg)
    {
        return;
    }

    public void visitTree(JCTree tree, T arg)
    {
        Assert.error();
    }

    protected void visitExpr(JCExpression expression,T arg)
    {
        if(expression==null) return;
        expression.scan(this,arg);
    }

    protected void visitExprs(ArrayList<JCExpression> expressions,T arg)
    {
        if(expressions==null) return;
        if(expressions.size()==0) return;
        for(JCExpression item:expressions)
            item.scan(this,arg);
    }
}
