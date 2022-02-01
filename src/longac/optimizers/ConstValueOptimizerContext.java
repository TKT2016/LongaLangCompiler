package longac.optimizers;

import longac.symbols.VarSymbol;
import longac.trees.JCAssign;
import longac.trees.JCLiteral;
import longac.trees.JCTree;
import longac.trees.JCVariableDecl;

import java.util.HashMap;

public class ConstValueOptimizerContext {
    public HashMap<VarSymbol, Object> valueMap = new HashMap<>();
    public HashMap<VarSymbol, JCTree> assignMap = new HashMap<>();

    public void clear()
    {
        valueMap.clear();
        assignMap.clear();
    }

    public void setAssignMap(VarSymbol varSymbol,JCTree tree)
    {
        if(assignMap.containsKey(varSymbol))
        {
            JCTree tree2 = assignMap.replace(varSymbol,tree);
            setAssignEffective(tree2,false);
        }
        else
        {
            assignMap.put(varSymbol,tree);
        }
    }

    public static void setAssignEffective(JCTree tree,boolean assignEffective)
    {
        if(tree instanceof JCAssign)
        {
            ((JCAssign) tree).assignEffective=assignEffective;
        }
        else if(tree instanceof JCVariableDecl)
        {
            ((JCVariableDecl) tree).assignEffective=assignEffective;
        }
    }

    public static boolean getAssignEffective(JCTree tree)
    {
        if(tree instanceof JCAssign)
        {
           return  ((JCAssign) tree).assignEffective ;
        }
        else if(tree instanceof JCVariableDecl)
        {
         return    ((JCVariableDecl) tree).assignEffective;
        }
        return true;
    }

    public Object getConstValue(JCTree tree)
    {
        if(!OptimizerUtil.isVar(tree))
            return null;
        if(tree instanceof JCLiteral)
        {
            return ((JCLiteral) tree).value;
        }
        else if(OptimizerUtil.isVar(tree))
        {
            VarSymbol varSymbol = OptimizerUtil.getVarSymbol(tree);
            return valueMap.get(varSymbol);
        }
        return null;
    }

}
