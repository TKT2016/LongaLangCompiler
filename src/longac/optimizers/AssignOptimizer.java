package longac.optimizers;


import longac.symbols.VarSymbol;
import longac.visitors.TreeTranslator;

import longac.trees.*;
public class AssignOptimizer extends TreeTranslator {

    @Override
    public JCTree translateVariable(JCVariableDecl tree, Object arg) {
        if (tree.init != null) {
            tree.init = (JCExpression) tree.init.translate(this, arg);
            VarSymbol varSymbol = tree.getDeclVarSymbol();
           // varSymbol.lastAssignTree = tree;
        }
        return tree;
    }

    @Override
    public JCTree translateAssign(JCAssign tree, Object arg)
    {
        VarSymbol varSymbol = OptimizerUtil.getVarSymbol(tree.left);
        /*if(varSymbol!=null) {
            if (varSymbol.lastAssignTree == null)
                varSymbol.lastAssignTree = tree;
            else
            {
                varSymbol.lastAssignTree.effectiveAssign = false;
                varSymbol.lastAssignTree = tree;
            }
        }*/

        return tree;
    }

    @Override
    protected boolean needTranslate(JCTree tree)
    {
        return (tree instanceof JCFileTree || tree instanceof JCClassDecl ||tree instanceof JCMethodDecl
                ||tree instanceof JCBlock || tree instanceof JCWhile ||  tree instanceof JCIf
                ||  tree instanceof JCForLoop ||  tree instanceof JCAssign ||  tree instanceof JCVariableDecl
        );
    }
}
