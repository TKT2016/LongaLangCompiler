package longac.lgac;

import longac.attrs.ExprVisitContext;
import longac.attrs.StmtExpAttrTranslator;
import longac.lgac.makers.FieldTypeMaker;
import longac.lgac.makers.MethodCallMaker;
import longac.lgac.makers.MethodEndMaker;
import longac.lgac.makers.HeadTypeMaker;
import longac.lgac.makeModels.FieldTypeMakeModel;
import longac.lgac.makeModels.MethodCallModel;
import longac.lgac.makeModels.MethodEndModel;
import longac.lgac.makeModels.HeadTypeMakeModel;
import longac.lgac.trees.ChainTree;
import longac.lgac.trees.MethodChainTree;
import longac.lgac.trees.TypeChainTree;

public class ChainTreeMaker {

    final TypeChainTree typeChainTree;

    StmtExpAttrTranslator expVisitor;
    ExprVisitContext context;

    public ChainTreeMaker(TypeChainTree typeChainTree, StmtExpAttrTranslator expVisitor, ExprVisitContext context)
    {
        this.typeChainTree = typeChainTree;
        this.expVisitor = expVisitor;
        this.context = context;
    }

    public void make(){

        makeTypeChainTree(typeChainTree);
    }

    void makeTypeChainTree(TypeChainTree typeChainTree)
    {
        int start = makeMaster(typeChainTree);

        for(int i= start;i<typeChainTree.calls.size();i++)
        {
            ChainTree tree = typeChainTree.calls.get(i);
            if(tree instanceof TypeChainTree)
            {
                makeTypeChainTree((TypeChainTree)tree);
            }
            else if(tree instanceof MethodChainTree)
            {
                makeMethodCall((MethodChainTree)tree);
            }
        }

        makeEndMethod(typeChainTree);
    }

    int makeMaster(TypeChainTree typeChainTree)
    {
        if(typeChainTree.parent ==null)
        {
            HeadTypeMaker topTypeMaker = new HeadTypeMaker(typeChainTree,context);
            HeadTypeMakeModel topMakeModel= topTypeMaker.make();
            context.insertStatements.add(topMakeModel.declVarStmt);
            return 0;
        }
        else
        {
            FieldTypeMaker fieldTypeMaker = new FieldTypeMaker(typeChainTree,context);
            FieldTypeMakeModel topMakeModel= fieldTypeMaker.make();
            context.insertStatements.add(topMakeModel.assignStatement);
            return 1;
        }
    }

    void makeMethodCall(MethodChainTree methodChainTree)
    {
        MethodCallMaker methodCallMaker = new MethodCallMaker(methodChainTree,context);
        MethodCallModel methodCallModel = methodCallMaker.make();
        context.insertStatements.add(methodCallModel.invokeStmt);
    }

    void makeEndMethod(TypeChainTree typeChainTree)
    {
        MethodEndMaker methodEndMaker = new MethodEndMaker(typeChainTree,context);
        MethodEndModel methodEndModel = methodEndMaker.make();
        typeChainTree.methodEndModel = methodEndModel;
    }
}
