package longac.visitors;

import tools.collectx.ArrayListUtil;
import longac.trees.*;

public class ExprScanner<T> extends TreeScanner<T> {

    public void visitCompilationUnit(JCFileTree compilationUnit, T arg)
    {
        for(JCTree tree :compilationUnit.defs )
        {
            tree.scan(this,arg);
        }
    }

    public void visitPackageDef(JCPackage tree, T arg)      { return; }
    public void visitImport(JCImport tree, T arg)               { return; }
    public void visitClassDef(JCClassDecl tree, T arg)
    {
        for(int i=0;i<tree.defs.size();i++)
        {
            tree.defs.get(i).scan(this, arg);
        }

        /*
        for (List<JCTree> l = tree.defs; l.nonEmpty(); l = l.tail) {
            l.head.accept(this, arg);
        }*/
    }
    public void visitMethodDef(JCMethodDecl tree, T arg)
    {
        tree.body.scan(this,arg);
    }

    public void visitVarDef(JCVariableDecl tree, T arg)
    {
       if(tree.init!=null)
           tree.init.scan(this,arg);
    }

    public void visitBlock(JCBlock tree, T arg)
    {
        for(JCTree stmt:tree.stats)
        {
            stmt.scan(this,arg);
        }
    }

    public void visitWhileLoop(JCWhile tree, T arg)
    {
        tree.cond.scan(this,arg);
        tree.body.scan(this,arg);
    }

    public void visitForLoop(JCForLoop tree, T arg)
    {
       /* if (tree.init != null) {
            for(JCStatement statement:tree.init)
                statement.scan(this,arg);
        }*/
        if(tree.init!=null)
            tree.init.scan(this,arg);

        if (tree.cond != null) {
            tree.cond.scan(this,arg);
        }

        tree.body.scan(this,arg);

        if(tree.step!=null)
            tree.step.scan(this,arg);

        /*for(JCExpressionStatement jcExpressionStatement:tree.step)
        {
            jcExpressionStatement.scan(this,arg);
        }*/

    }

    public void visitIf(JCIf tree, T arg)
    {
        tree.cond.scan(this,arg);
        tree.thenpart.scan(this,arg);
        if(tree.elsepart!=null)
            tree.elsepart.scan(this,arg);
    }

    public void visitExec(JCExpressionStatement tree, T arg)    { tree.expr.scan(this,arg); }
    public void visitBreak(JCBreak tree, T arg)                 { return; }
    public void visitContinue(JCContinue tree, T arg)           { return; }

    public void visitReturn(JCReturn tree, T arg)
    {
        if(tree.expr!=null)
            tree.expr.scan(this,arg);
    }

    public void visitMethodInvocation(JCMethodInvocation tree, T arg)
    {
        tree.meth.scan(this,arg);
        for (JCExpression argitem :tree.args)
        {
            argitem.scan(this,arg);
        }
    }

    public void visitNewClass(JCNewClass tree, T arg)
    {
        for (JCExpression argitem :tree.args)
        {
            argitem.scan(this,arg);
        }
    }

    public void visitNewArray(JCNewList tree, T arg)
    {
        if(ArrayListUtil.nonEmpty(tree.elems)) {
            for (JCExpression argitem : tree.elems) {
                argitem.scan(this, arg);
            }
        }
    }

    public void visitParens(JCParens tree, T arg)
    {
        tree.expr.scan(this,arg);
    }

    public void visitAssign(JCAssign tree, T arg)
    {
        tree.left.scan(this,arg);
        tree.right.scan(this,arg);
    }

    public void visitUnary(JCUnary tree, T arg)
    {
        tree.expr.scan(this,arg);
    }

    public void visitBinary(JCBinary tree, T arg)
    {
        tree.left.scan(this,arg);
        tree.right.scan(this,arg);
    }

   // public void visitTypeCast(JCTypeCast tree, Object arg)           { visitTree(tree,arg); }
    //public void visitBindingPattern(JCBindingPattern tree, Object arg) { visitTree(tree,arg); }
   /* public void visitIndexed(JCArrayAccess tree, T arg)
    {
        tree.indexed.scan(this,arg);
        tree.index.scan(this,arg);
    }*/

    public void visitSelect(JCFieldAccess tree, T arg)
    {
        tree.selected.scan(this,arg);
    }

    public void visitIdent(JCIdent tree, T arg)                 { return; }
    public void visitLiteral(JCLiteral tree, T arg)             { return; }
    public void visitTypeIdent(JCPrimitiveTypeTree tree, T arg) { return; }
   // public void visitTypeArray(JCArrayTypeTree tree, T arg)     {return; }
    //public void visitTypeApply(JCTypeApply tree, Object arg)         { visitTree(tree,arg); }
   // public void visitModifiers(JCModifiers tree, Object arg)         { return; }
    public void visitErroneous(JCErroneous tree, T arg)         { return; }

}
