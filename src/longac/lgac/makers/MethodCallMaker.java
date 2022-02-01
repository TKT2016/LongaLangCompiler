package longac.lgac.makers;

import longac.attrs.ExprVisitContext;
import longac.lgac.makeModels.MethodCallModel;
import longac.lgac.trees.MethodChainTree;
import longac.symbols.ErrorSymbol;
import longac.trees.*;
import longac.utils.Assert;

public class MethodCallMaker {

    final MethodChainTree methodChainTree;
    final ExprVisitContext context;

    MethodCallModel methodCallModel;

    public MethodCallMaker(MethodChainTree methodChainTree,ExprVisitContext context)
    {
        this.methodChainTree =methodChainTree;
        this.context =context;
    }

    /** master.method(args); */
    public MethodCallModel make()
    {
        methodCallModel = new MethodCallModel();

        methodChainTree.methodCallModel = methodCallModel;

        makeMaster();
        makeMethod();
        makeMethodInvocation();
        makeStatement();

        return methodCallModel;
    }

    /**  master*/
    void makeMaster()
    {
        methodCallModel.master = methodChainTree.typeChainTree.getMasterExpr();
    }

    /**  master.method */
    void makeMethod()
    {
        JCFieldAccess jcFieldAccess = new JCFieldAccess(methodCallModel.master,methodChainTree.getMethodName() );
        initTree(jcFieldAccess);
        if(methodChainTree.methodSymbol!=null)
            jcFieldAccess.setSymbol(methodChainTree.methodSymbol);
        else
            jcFieldAccess.setSymbol(new ErrorSymbol());
        methodCallModel.meth = jcFieldAccess;
    }

    /** master.method(args) */
    void makeMethodInvocation()
    {
        JCMethodInvocation jcMethodInvocation = new JCMethodInvocation( methodCallModel.meth,methodChainTree.args);
        initTree(jcMethodInvocation);
        jcMethodInvocation.setSymbol(methodChainTree.methodSymbol);
        methodCallModel.methodInvocation = jcMethodInvocation;
    }

    /**
     *  master.method(args);
     */
    void makeStatement( )
    {
        JCExpressionStatement declVarStmt = new JCExpressionStatement(methodCallModel.methodInvocation);
        initTree(declVarStmt);
        methodCallModel.invokeStmt =declVarStmt;
    }

    protected void initTree(JCTree jcTree) {
        JCTree  chainTree = methodChainTree.methodIdent;
        jcTree.line = chainTree.line;
        jcTree.pos = chainTree.pos;
        jcTree.log = chainTree.log;
        if(jcTree.log==null)
            Assert.error();
        if (jcTree instanceof JCExpression) {
            JCExpression jcExpression=(JCExpression) jcTree;
            jcExpression.sourceExpr = methodChainTree.typeChainTree.jcChain;
        }
        else if (jcTree instanceof JCStatement) {
            JCStatement jcStatement=(JCStatement) jcTree;
            jcStatement.sourceExpr = this.methodChainTree.typeChainTree.jcChain;
        }
    }
}
