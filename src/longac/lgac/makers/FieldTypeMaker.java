package longac.lgac.makers;

import longa.lang.LongaSpecialNames;
import longac.attrs.ExprVisitContext;
import longac.lgac.makeModels.FieldTypeMakeModel;
import longac.lgac.trees.MethodChainTree;
import longac.lgac.trees.TypeChainTree;
import longac.trees.*;
import longac.utils.Assert;

public class FieldTypeMaker {
    static int chain_anonymous_var_index=0;
    final TypeChainTree typeChainTree;
    final ExprVisitContext context;

    FieldTypeMakeModel fieldTypeMakeModel;

    public FieldTypeMaker(TypeChainTree typeChainTree, ExprVisitContext context)
    {
        this.typeChainTree =typeChainTree;
        this.context =context;
    }

    /** select.join = select.join.__start() */
    public FieldTypeMakeModel make()
    {
        fieldTypeMakeModel = new FieldTypeMakeModel();
        fieldTypeMakeModel.fieldSymbol = typeChainTree.fieldSymbol;
        typeChainTree.fieldTypeMaker = fieldTypeMakeModel;

        makeLeftIdent();
        makeLeft();
        makeRightIdent();
        makeRightFieldGet();
        makerigthStartMethod();
        makeMethodInvocation();
        makeAssign();
        makeAssignStatement( );
        return fieldTypeMakeModel;
    }

    /** left select */
    void makeLeftIdent()
    {
        //JCExpression nameIdent = typeChainTree.parent.getMasterExpr();// new JCIdent(.name);
        //initTree(nameIdent);
        fieldTypeMakeModel.leftVarIdent =  typeChainTree.parent.getMasterExpr();
    }

    /** left select.join */
    void makeLeft()
    {
        if(fieldTypeMakeModel.fieldSymbol==null )
            Assert.error();
        JCFieldAccess fieldAccess = new JCFieldAccess(fieldTypeMakeModel.leftVarIdent,fieldTypeMakeModel.fieldSymbol.name );
        initTree(fieldAccess);
        fieldAccess.setSymbol(fieldTypeMakeModel.fieldSymbol);
        fieldTypeMakeModel.left = fieldAccess;
    }

    /** right select */
    void makeRightIdent()
    {
        JCExpression nameIdent = typeChainTree.parent.getMasterExpr();
        initTree(nameIdent);
        fieldTypeMakeModel.rigtVarIdent = nameIdent;
    }

    /** right select.join */
    void makeRightFieldGet()
    {
        JCFieldAccess fieldAccess = new JCFieldAccess(fieldTypeMakeModel.rigtVarIdent,fieldTypeMakeModel.fieldSymbol.name );
        initTree(fieldAccess);
        fieldAccess.setSymbol(fieldTypeMakeModel.fieldSymbol);
        fieldTypeMakeModel.rightFieldGet = fieldAccess;
    }

    /** right select.join.__start */
    void makerigthStartMethod()
    {
        JCFieldAccess fieldAccess = new JCFieldAccess(fieldTypeMakeModel.rightFieldGet, LongaSpecialNames.startMthodName);
        initTree(fieldAccess);
        fieldTypeMakeModel.rigthStartMethod = fieldAccess;
    }

    /** right select.join.__start(args) */
    void makeMethodInvocation()
    {
        MethodChainTree startCall = (MethodChainTree)typeChainTree.calls.get(0);
        JCMethodInvocation jcMethodInvocation = new JCMethodInvocation( fieldTypeMakeModel.rigthStartMethod,startCall.args);
        initTree(jcMethodInvocation);
        jcMethodInvocation.setSymbol(startCall.methodSymbol);
        fieldTypeMakeModel.rigthStartMethod.setSymbol(startCall.methodSymbol);
        fieldTypeMakeModel.rightStartInvocation = jcMethodInvocation;
    }

    /** select.join = select.join.__start(args) */
    void makeAssign()
    {
        JCAssign assign = new JCAssign( fieldTypeMakeModel.left,fieldTypeMakeModel.rightStartInvocation);
        initTree(assign);
        fieldTypeMakeModel.assign = assign;
    }

    /**
     * Statement select_chain select_0 = new select_chain();
     */
    void makeAssignStatement( )
    {
        JCExpressionStatement stmt = new JCExpressionStatement(fieldTypeMakeModel.assign);
        initTree(stmt);
        fieldTypeMakeModel.assignStatement=stmt;
    }

    protected  void initTree(JCTree jcTree) {
        JCTree  chainTree =typeChainTree.startIdent;
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
