package longac.lgac.trees;

import longa.lang.LongaSpecialNames;
import longac.diagnostics.SourceLog;
import longac.lgac.makeModels.MethodCallModel;
import longac.symbols.JavaMethodSymbol;
import longac.symbols.MethodSymbol;
import longac.trees.JCExpression;
import longac.trees.JCIdent;
import longac.utils.Assert;

import java.util.ArrayList;

public class MethodChainTree extends ChainTree {

    public final JCIdent methodIdent;

    public ArrayList<JCExpression> args = new ArrayList<>();

    public final TypeChainTree typeChainTree;
    public MethodChainTree nextMethod;
    public MethodChainTree preMethod;

    public ArrayList<MethodSymbol> methodSymbols;

    public MethodChainTree(TypeChainTree typeNode, JCIdent methodIdent,ArrayList<MethodSymbol> methodSymbols)
    {
        this.typeChainTree = typeNode;
        this.methodIdent = methodIdent;
        this.methodSymbols = methodSymbols;

        if(methodIdent.log==null)
            Assert.error();
    }

    public String getMethodName()
    {
        if(methodIdent==null)
            return LongaSpecialNames.startMthodName;
        else
            return methodIdent.name;
    }

    public JavaMethodSymbol methodSymbol;

    public MethodCallModel methodCallModel;

    public void setMethodSymbol(JavaMethodSymbol methodSymbol)
    {
        this.methodSymbol=methodSymbol;
        this.methodIdent.setSymbol(methodSymbol);
    }

    public int prepCount()
    {
        MethodSymbol tempSymbol = methodSymbols.get(0);
        if(tempSymbol instanceof JavaMethodSymbol) {
            JavaMethodSymbol javaMethodSymbol = (JavaMethodSymbol) tempSymbol;
            if (javaMethodSymbol.lgaNode != null)
                return javaMethodSymbol.lgaNode.prepCount();
            return 0;
        }
        return 0;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getMethodName());
        builder.append(" (");
        for (JCExpression expression:this.args)
        {
            builder.append(expression);
            builder.append(" ");
        }
        builder.append(") ");
        return builder.toString();
    }

}
