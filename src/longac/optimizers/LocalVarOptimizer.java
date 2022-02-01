package longac.optimizers;

import longac.visitors.TreeScanner;
import longac.trees.*;

public class LocalVarOptimizer  extends TreeScanner
{

    @Override
    public void visitAssign(JCAssign tree, Object arg)
    {
        /*tree.left =  translate(tree.left,arg);
        tree.right =  translate(tree.right,arg);
        if(OptimizerUtil.isVar(tree.left))
        {
            VarSymbol varSymbol = OptimizerUtil.getVarSymbol(tree.left);
            arg.setAssignMap(varSymbol,tree);
            Object constValue = arg.getConstValue(tree.right);
            arg.valueMap.replace(varSymbol,constValue);
        }*/

        //return tree;
    }

    /*@Override
    public JCTree translateIdent(JCIdent tree, Object arg)
    {
        Symbol symbol = tree.getSymbol();
        if(symbol instanceof DeclVarSymbol)
        {
            DeclVarSymbol declVarSymbol = (DeclVarSymbol) symbol;
            declVarSymbol.hasGot = true;
        }
        return tree;
    }

    @Override
    public JCTree translateVariable(JCVariableDecl tree, Object arg) {*/
        /*if (tree.init != null) {
            VarSymbol varSymbol = tree.getDeclVarSymbol();
            arg.setAssignMap(varSymbol,tree);
            tree.init = (JCExpression) tree.init.translate(this, arg);
            Object constValue = arg.getConstValue(tree.init);
            arg.valueMap.replace(varSymbol,constValue);
        }
        return tree;*/
   /* }*/
}
