package longac.lgac;

import longac.attrs.ExprVisitContext;
import longac.attrs.StmtExpAttrTranslator;
import longac.lgac.trees.ChainTree;
import longac.lgac.trees.MethodChainTree;
import longac.lgac.trees.TypeChainTree;
import longac.symbols.*;
import longac.trees.JCExpression;
import longac.trees.JCIdent;

import java.util.ArrayList;

import static longa.lang.LongaSpecialNames.endMthodName;

public class ChainAdjustment {

    final TypeChainTree typeChainTree;

    StmtExpAttrTranslator expVisitor;
    ExprVisitContext context;

    public ChainAdjustment(TypeChainTree typeChainTree, StmtExpAttrTranslator expVisitor, ExprVisitContext context)
    {
        this.typeChainTree = typeChainTree;
        this.expVisitor = expVisitor;
        this.context = context;
    }

    public void parse(){

        parseTypeChainTree(typeChainTree);
    }

    void parseTypeChainTree(TypeChainTree typeChainTree)
    {
        for(ChainTree tree:typeChainTree.calls)
        {
            if(tree instanceof TypeChainTree)
            {
                parseTypeChainTree((TypeChainTree)tree);
            }
            else if(tree instanceof MethodChainTree)
            {
                parseMethodChainTree((MethodChainTree)tree);
            }
        }

    }

    void parseMethodChainTree(MethodChainTree methodChainTree)
    {
        if(methodChainTree.nextMethod!=null)
        {
            int prepCount= methodChainTree.nextMethod.prepCount();
            if(prepCount>0)
            {
                for(int i=0;i<prepCount;i++)
                {
                    JCExpression expression = methodChainTree.args.remove( methodChainTree.args.size()-1);
                    methodChainTree.nextMethod.args.add(0,expression);
                }
            }
        }
    }
}
