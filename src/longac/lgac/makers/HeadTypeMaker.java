package longac.lgac.makers;

import longac.attrs.ExprVisitContext;
import longac.lgac.makeModels.HeadTypeMakeModel;
import longac.lgac.trees.TypeChainTree;
import longac.symbols.*;
import longac.trees.*;

import java.util.ArrayList;

public class HeadTypeMaker {
    static int chain_anonymous_var_index=0;
    final TypeChainTree typeChainTree;
    final ExprVisitContext context;

    HeadTypeMakeModel topMakeModel;

    public HeadTypeMaker(TypeChainTree typeChainTree, ExprVisitContext context)
    {
        this.typeChainTree =typeChainTree;
        this.context =context;
    }

    public HeadTypeMakeModel make()
    {
        topMakeModel = new HeadTypeMakeModel();
        typeChainTree.headTypeMakeModel =topMakeModel;

        makeTypeExpr();
        makeVarName();
        makeNewChainClass();
        makeVarDecl();
        makeDeclVarStatement();
        return topMakeModel;
    }

    /** type select_chain */
    void makeTypeExpr()
    {
        topMakeModel.typeExpr = new JCIdent(typeChainTree.javaClassSymbol.name);
        topMakeModel.typeExpr.setSymbol(typeChainTree.javaClassSymbol);
        initTree(topMakeModel.typeExpr);
    }

    /** var select_0 */
    void makeVarName()
    {
        topMakeModel.localVarName = typeChainTree.startIdent.name+"_"+chain_anonymous_var_index;
        chain_anonymous_var_index++;
        makeVarSymbol();
        topMakeModel.nameIdent = new JCIdent(  topMakeModel.localVarName);
        initTree( topMakeModel.nameIdent );
        topMakeModel.nameIdent .setSymbol(topMakeModel.declVarSymbol);
    }

    void makeVarSymbol()
    {
        topMakeModel.declVarSymbol = new DeclVarSymbol(topMakeModel.localVarName , null, VarKind.chainVar, typeChainTree.javaClassSymbol);
        BlockFrame blockFrame = (BlockFrame) context.frame;
        blockFrame.addVar(topMakeModel.declVarSymbol);
    }

    /** new select_chain() */
    void makeNewChainClass()
    {
        JavaClassSymbol chainTypeSymbol = typeChainTree.javaClassSymbol;
        JCNewClass jcNewClass = new JCNewClass(topMakeModel.typeExpr , new ArrayList<>());
        MethodSymbol constrcutor = chainTypeSymbol.findConstructor(new ArrayList<>()).get(0);
        jcNewClass.setSymbol(chainTypeSymbol);
        jcNewClass.constructorSymbol = constrcutor;
        topMakeModel.jcNewClass =jcNewClass;
    }

    /**
     *  expr  select_chain select_0 = new select_chain()
     */
    void makeVarDecl()
    {
        JCIdent typeExpr = topMakeModel.typeExpr;
        JCIdent nameIdent = topMakeModel.nameIdent;
        JCNewClass jcNewClass =topMakeModel.jcNewClass;
        DeclVarSymbol varSymbol = topMakeModel.declVarSymbol;

        JCVariableDecl variableDecl = new JCVariableDecl(typeExpr,nameIdent,jcNewClass);
        initTree(variableDecl);
        variableDecl.setSymbol(varSymbol);

        topMakeModel.variableDecl=variableDecl;
    }

    /**
     * Statement select_chain select_0 = new select_chain();
     */
    void makeDeclVarStatement( )
    {
        JCVariableDecl variableDecl =topMakeModel.variableDecl;
        JCExpressionStatement declVarStmt = new JCExpressionStatement(variableDecl);
        initTree(declVarStmt);
        topMakeModel.declVarStmt=declVarStmt;
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
