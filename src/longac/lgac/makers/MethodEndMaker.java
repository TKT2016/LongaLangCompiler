package longac.lgac.makers;

import longa.lang.LongaSpecialNames;
import longac.attrs.ExprVisitContext;
import longac.lgac.makeModels.MethodEndModel;
import longac.lgac.trees.TypeChainTree;
import longac.trees.*;

import java.util.ArrayList;

public class MethodEndMaker {

    final TypeChainTree typeChainTree;
    final ExprVisitContext context;

    MethodEndModel methodCallModel;

    public MethodEndMaker(TypeChainTree typeChainTree, ExprVisitContext context)
    {
        this.typeChainTree =typeChainTree;
        this.context =context;
    }

    /** master.__end(); */
    public MethodEndModel make()
    {
        methodCallModel = new MethodEndModel();

        typeChainTree.methodEndModel = methodCallModel;

        makeMaster();
        makeMethod();
        makeMethodInvocation();

        return methodCallModel;
    }

    /**  master*/
    void makeMaster()
    {
        methodCallModel.master = typeChainTree.getMasterExpr();
    }

    /**  master.__end */
    void makeMethod()
    {
        JCFieldAccess jcFieldAccess = new JCFieldAccess(methodCallModel.master, LongaSpecialNames.endMthodName);
        initTree(jcFieldAccess);
        jcFieldAccess.setSymbol(typeChainTree.endMethodSymbol);
        methodCallModel.meth = jcFieldAccess;
    }

    /** master.method() */
    void makeMethodInvocation()
    {
        JCMethodInvocation jcMethodInvocation = new JCMethodInvocation( methodCallModel.meth,new ArrayList<>());
        initTree(jcMethodInvocation);
        jcMethodInvocation.setSymbol(typeChainTree.endMethodSymbol);
        methodCallModel.methodInvocation = jcMethodInvocation;
    }

    protected void initTree(JCTree jcTree) {
        JCTree chainTree = typeChainTree.startIdent;
        jcTree.line = chainTree.line;
        jcTree.pos = chainTree.pos;
        jcTree.log = chainTree.log;
        if (jcTree instanceof JCExpression) {
            JCExpression jcExpression=(JCExpression) jcTree;
            jcExpression.sourceExpr = typeChainTree.jcChain;
        }
        else if (jcTree instanceof JCStatement) {
            JCStatement jcStatement=(JCStatement) jcTree;
            jcStatement.sourceExpr = this.typeChainTree.jcChain;
        }
    }
}
