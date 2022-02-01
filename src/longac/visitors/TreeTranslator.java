package longac.visitors;

import tools.collectx.ArrayListUtil;
import longac.trees.*;
import java.util.ArrayList;

public abstract class TreeTranslator<A>
{
    public JCFileTree translate(JCFileTree compilationFile)
    {
        for(JCTree tree :compilationFile.defs )
        {
            tree.translate(this,null);
        }
        return compilationFile;
    }
/*
    public JCTree visitArrayType(JCArrayTypeTree tree,A arg)
    {
        return tree;
    }
*/
    public JCTree translatePrimitiveType(JCPrimitiveTypeTree tree, A arg)
    {
        return tree;
    }

    public JCTree translateCompilationUnit(JCFileTree compilationFile, A arg)
    {
        for(JCTree tree :compilationFile.defs )
        {
            tree.translate(this,arg);
        }
        return compilationFile;
    }

    public JCTree translateMethod(JCMethodDecl tree, A arg)
    {
        tree.body = (JCBlock) tree.body.translate(this,arg);
        return tree;
    }

    public JCTree translateBlock(JCBlock tree, A arg)
    {
        ArrayList<JCStatement> newStatements = new  ArrayList<JCStatement>();
        for (JCStatement statement : tree.stats)
        {
            if(statement.isEffective())
            {
                newStatements.add(statement);
            }
        }
        tree.stats = newStatements;
        return tree;

        //tree.stats = translates(tree.stats,arg);
        //return tree;
    }

    protected <T extends JCTree> T translate(T tree, A arg)
    {
        if(tree==null) return null;
        if(needTranslate(tree)) {
            JCTree newTree = tree.translate(this, arg);
            return (T) newTree;
        }
        else
        {
            return tree;
        }
    }
/*
    protected JCStatement translate(JCStatement tree, A arg)
    {
        if(tree==null) return null;
        if(needTranslate(tree)) {
            JCTree newTree = tree.translate(this, arg);
            return (JCStatement) newTree;
        }
        else
        {
            return tree;
        }
    }

    protected JCExpressionStatement translate(JCExpressionStatement tree, A arg)
    {
        JCTree newTree = tree.translate(this,arg);
        return (JCExpressionStatement)newTree;
    }

    protected JCExpression translate(JCExpression tree, A arg)
    {
        JCTree newTree = tree.translate(this,arg);
        return (JCExpression)newTree;
    }
*/
    public JCTree translateImport(JCImport tree, A arg)
    {
        return tree;
    }

    public JCTree translateAssign(JCAssign tree, A arg)
    {
        tree.left =translate(tree.left,arg);
        tree.right =translate(tree.right,arg);
        /*
        if(needTranslate(tree.left))
            tree.left =translate(tree.left,arg);// (JCExpression) tree.left.translate(this,arg);
        if(needTranslate(tree.right))
            tree.right =translate(tree.right,arg);// (JCExpression) tree.right.translate(this,arg);*/
        return tree;
    }

    public JCTree translateNewArray(JCNewList tree, A arg)
    {
        //if(needTranslate(tree.elemtype))
        //    tree.elemtype = translate(tree.elemtype,arg);//(JCExpression) tree.elemtype.translate(this,arg);
       // if(tree.dims!=null&& needTranslate(tree.dims))
      //      tree.dims =  translate(tree.dims,arg);// (JCExpression) tree.dims.translate(this,arg);
        if(ArrayListUtil.nonEmpty(tree.elems))
            tree.elems = translates(tree.elems,arg);
        return tree;
    }

    public JCTree translateNewClass(JCNewClass tree, A arg)
    {
        if(needTranslate(tree.clazz))
            tree.clazz =translate(tree.clazz,arg);// (JCExpression) tree.clazz.translate(this,arg);
        if(tree.args!=null)
            tree.args = translates(tree.args,arg);
        return tree;
    }

    public JCTree translatePackage(JCPackage tree, A arg)
    {
        return tree;
    }
/*
    public JCTree visitArrayAccess(JCArrayAccess tree, A arg)
    {
        //if(needTranslate(tree.indexed))
            tree.indexed = (JCExpression) tree.indexed.translate(this,arg);
        //if(needTranslate(tree.index))
            tree.index = (JCExpression) tree.index.translate(this,arg);
        return tree;
    }*/

    public JCTree translateFieldAccess(JCFieldAccess tree, A arg)
    {
        tree.selected = translate(tree.selected,arg);
        return tree;
    }

    public JCTree translateChain(JCChain tree, A arg)
    {
        return tree;
    }

    public JCTree translateBinary(JCBinary tree, A arg)
    {
        tree.left =translate(tree.left,arg);
        tree.right =translate(tree.right,arg);
        return tree;
    }

    public JCTree translateIdent(JCIdent tree, A arg)
    {
        return tree;
    }

    public JCTree translateLiteral(JCLiteral tree, A arg)
    {
        return tree;
    }

    public JCTree translateParens(JCParens tree, A arg)
    {
       // if(needTranslate(tree.expr))
            tree.expr = translate( tree.expr,arg);
        return tree;
    }

    public JCTree translateUnary(JCUnary tree, A arg)
    {
       // if(needTranslate(tree.expr))
            tree.expr = translate( tree.expr,arg);
        return tree;
    }

    public JCTree translateReturn(JCReturn tree, A arg)
    {
       // if(tree.expr!=null && needTranslate(tree.expr))
            tree.expr = translate( tree.expr,arg);
        return tree;
    }

    public JCTree translateIf(JCIf tree, A arg)
    {
        //if(needTranslate(tree.cond))
            tree.cond =  translate( tree.cond,arg);//(JCExpression)tree.cond.translate (this,arg);
       // if(needTranslate(tree.thenpart))
            tree.thenpart = translate( tree.thenpart,arg);//(JCStatement)tree.thenpart.translate (this,arg);
       // if(tree.elsepart!=null &&  needTranslate(tree.elsepart))
       // {
            tree.elsepart =  translate(tree.elsepart,arg);//(JCStatement)tree.elsepart.translate (this,arg);
       // }
        return tree;
    }

    public JCTree translateForLoop(JCForLoop tree, A arg){
        //if(tree.init!=null)
        //     tree.init = translates(tree.init,arg) ;
        //if(tree.init!=null && needTranslate(tree.init))
            tree.init =translate (tree.init,arg);// (JCStatement) tree.init.translate(this,arg);

       // if(tree.cond!=null && needTranslate(tree.cond))
            tree.cond  = translate (tree.cond,arg);//JCExpression)tree.cond.translate (this,arg);
        //if(tree.step!=null)
        //    tree.step =translates(tree.step,arg) ;
        //if(tree.step!=null && needTranslate(tree.step))
            tree.step =translate (tree.step,arg);// (JCStatement) tree.init.translate(this,arg);
       // if(tree.body!=null)
            tree.body = (JCStatement)tree.body.translate (this,arg);
        return tree;
    }

    public JCTree translateWhile(JCWhile tree, A arg)
    {
       // if(needTranslate(tree.cond))
            tree.cond  = translate( tree.cond,arg);//(JCExpression)tree.cond.translate (this,arg);

       // if(needTranslate(tree.body))
            tree.body = translate( tree.body,arg);// (JCStatement)tree.body.translate (this,arg);
        return tree;
    }

    public JCTree translateVariable(JCVariableDecl tree, A arg)
    {
        //if(tree.init!=null&&needTranslate(tree.init))
            tree.init = translate( tree.init,arg);//(JCExpression)tree.init.translate (this,arg);
        return tree;
    }

    public JCTree translateMethodInvocation(JCMethodInvocation tree, A arg)
    {
        tree.args = translates(tree.args,arg);
        return tree;
    }

    public JCTree translateBreak(JCBreak tree, A arg){ return tree;  }

    public JCTree translateClass(JCClassDecl tree, A arg)
    {
        tree.defs = translates(tree.defs,arg);
        return tree;
    }

    public JCTree translateContinue(JCContinue tree, A arg){ return tree;  }

    public JCTree translateErroneous(JCErroneous tree, A arg){ return tree;  }

    public JCTree translateExpressionStatement(JCExpressionStatement tree, A arg)
    {
        tree.expr = translate(tree.expr,arg);
        //if(tree.expr==null)
        //    return null;
        return tree;
    }

    protected <R extends JCTree> ArrayList<R> translates(ArrayList<R> trees, A arg)
    {
        ArrayList<R> defs = new  ArrayList<R>();
        for (R deftree : trees)
        {
            //JCTree tree = (JCTree)deftree;
            if(needTranslate(deftree)) {
                JCTree newTree = deftree.translate(this, arg);
                if (newTree != null) {
                    R nr = (R) newTree;
                    defs.add(nr);
                }
            }
            else
            {
                defs.add(deftree);
            }
        }
        return defs;
    }

    protected boolean needTranslate(JCTree tree)
    {
        return true;
    }
}
