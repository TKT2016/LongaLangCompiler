package longac.optimizers;

import longac.visitors.TreeTranslator;
import longac.trees.*;

import java.util.ArrayList;

public class ReturnAfterOptimizer extends TreeTranslator<Object> {

    public JCTree translateBlock(JCBlock tree, Object arg)
    {
        ArrayList<JCStatement> newStatements = new  ArrayList<JCStatement>();
        boolean isReturned = false;
        for (JCStatement statement : tree.stats)
        {
           if(!isReturned)
           {
               if(statement instanceof  JCIf || statement instanceof JCWhile) {
                   JCStatement newStatement = (JCStatement) statement.translate(this, arg);
                   newStatements.add(newStatement);
                   isReturned = newStatement instanceof JCReturn;
               }
               else
               {
                   newStatements.add(statement);
                   isReturned = statement instanceof JCReturn;
               }
           }
        }
        tree.stats = newStatements;
        return tree;
    }

    @Override
    public JCTree translateIf(JCIf tree, Object arg)
    {
        if(tree.thenpart instanceof JCBlock)
        {
            tree.thenpart = (JCStatement) tree.thenpart.translate(this,arg);
        }
        if(tree.elsepart !=null)
            tree.elsepart = (JCStatement) tree.elsepart.translate(this,arg);
        return tree;
    }

    @Override
    public JCTree translateWhile(JCWhile tree, Object arg)
    {
        if(tree.body instanceof JCBlock)
        {
            tree.body = (JCStatement) tree.body.translate(this,arg);
        }
        return tree;
    }

    @Override
    protected boolean needTranslate(JCTree tree)
    {
        return (tree instanceof JCFileTree || tree instanceof JCClassDecl ||tree instanceof JCMethodDecl
                ||tree instanceof JCBlock || tree instanceof JCWhile ||  tree instanceof JCIf );
    }
}
