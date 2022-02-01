package longac.optimizers;

import longac.visitors.TreeTranslator;
import longac.trees.*;

import java.util.ArrayList;

public class DeadCodeOptimizer extends TreeTranslator<Object> {

    @Override
    public JCTree translateBlock(JCBlock tree, Object arg) {
        ArrayList<JCStatement> newStatements = new ArrayList<JCStatement>();

        for (JCStatement statement : tree.stats) {
            if (statement instanceof JCIf || statement instanceof JCWhile) {
                JCStatement newStatement = (JCStatement) statement.translate(this, arg);
                newStatements.add(newStatement);
            }
            else {
                newStatements.add(statement);
            }
        }
        tree.stats = newStatements;
        return tree;
    }


    @Override
    public JCTree translateIf(JCIf tree, Object arg)
    {
        //Object value = OptimizerUtil.getConstValue(tree.cond);
        Boolean value = getBooleanValue(tree.cond);
        if(value!=null) {
            if (value.booleanValue())
                return tree.thenpart;
            else
                return tree.elsepart;
        }
        return tree;
    }

    @Override
    public JCTree translateWhile(JCWhile tree, Object arg)
    {
        //tree.cond   = translate (tree.cond,arg);
        //Object value = OptimizerUtil.getConstValue(tree.cond);
        Boolean value = getBooleanValue(tree.cond);
        if(value!=null) {
            if (value.booleanValue()==false)
                return null;
        }
        //if(OptimizerUtil.isFalse(value))
        //    return null;
        return tree;
    }

    private Boolean getBooleanValue(JCTree tree)
    {
        if(tree instanceof JCLiteral)
        {
            JCLiteral jcLiteral = (JCLiteral) tree;
            if(jcLiteral.value instanceof Boolean)
                return (Boolean) jcLiteral.value;
        }
        return null;
    }

    @Override
    protected boolean needTranslate(JCTree tree)
    {
        return (tree instanceof JCFileTree || tree instanceof JCClassDecl ||tree instanceof JCMethodDecl
                ||tree instanceof JCBlock || tree instanceof JCWhile ||  tree instanceof JCIf );
    }
}
