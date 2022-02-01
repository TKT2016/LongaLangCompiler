package longac.lgac.makers;

import longac.attrs.ExprVisitContext;
import longac.lgac.makeModels.LongaListNewMakeModel;
import longac.symbols.*;
import longac.trees.*;

import java.util.ArrayList;

public class LongaNewListMaker {
    static int AnonymousListVarIndex=0;
    final JCNewList tree;
    final ExprVisitContext context;

    LongaListNewMakeModel listMakeModel;
    JavaClassSymbol listSymbol =JavaClassSymbol.listSymbol;

    public LongaNewListMaker(JCNewList tree, ExprVisitContext context)
    {
        this.tree =tree;
        this.context =context;
    }

    /**  List<Object> objectList = new List<>() */
    public LongaListNewMakeModel make()
    {
        listMakeModel = new LongaListNewMakeModel();
        tree.listNewModel = listMakeModel;

        makeTypeExpr();
        makeVarName();
        makeNewChainClass();
        makeVarDecl();
        makeDeclVarStatement();
        return listMakeModel;
    }

    /** type List */
    void makeTypeExpr()
    {
        listMakeModel.typeExpr = new JCIdent(listSymbol.name);
        listMakeModel.typeExpr.setSymbol(listSymbol);
        initTree(listMakeModel.typeExpr);
    }

    /** var list_0 */
    void makeVarName()
    {
        listMakeModel.localVarName = listSymbol.name+"_"+AnonymousListVarIndex;
        AnonymousListVarIndex++;
        makeVarSymbol();
        listMakeModel.nameIdent = new JCIdent(  listMakeModel.localVarName);
        initTree( listMakeModel.nameIdent );
        listMakeModel.nameIdent .setSymbol(listMakeModel.declVarSymbol);
    }

    void makeVarSymbol()
    {
        listMakeModel.declVarSymbol = new DeclVarSymbol(listMakeModel.localVarName , null, VarKind.chainVar, listSymbol);
        BlockFrame blockFrame = (BlockFrame) context.frame;
        blockFrame.addVar(listMakeModel.declVarSymbol);
    }

    /** new List() */
    void makeNewChainClass()
    {
        //JavaClassSymbol chainTypeSymbol = tree.javaClassSymbol;
        JCNewClass jcNewClass = new JCNewClass(listMakeModel.typeExpr , new ArrayList<>());
        MethodSymbol constrcutor = listSymbol.findConstructor(new ArrayList<>()).get(0);
        jcNewClass.setSymbol(listSymbol);
        jcNewClass.constructorSymbol = constrcutor;
        listMakeModel.jcNewClass =jcNewClass;
    }

    /**
     *  expr   List<Object> objectList = new List<>()
     */
    void makeVarDecl()
    {
        JCIdent typeExpr = listMakeModel.typeExpr;
        JCIdent nameIdent = listMakeModel.nameIdent;
        JCNewClass jcNewClass = listMakeModel.jcNewClass;
        DeclVarSymbol varSymbol = listMakeModel.declVarSymbol;

        JCVariableDecl variableDecl = new JCVariableDecl(typeExpr,nameIdent,jcNewClass);
        initTree(variableDecl);
        variableDecl.setSymbol(varSymbol);

        listMakeModel.variableDecl=variableDecl;
    }

    /**
     * Statement List<Object> objectList = new List<>();
     */
    void makeDeclVarStatement( )
    {
        JCVariableDecl variableDecl = listMakeModel.variableDecl;
        JCExpressionStatement declVarStmt = new JCExpressionStatement(variableDecl);
        initTree(declVarStmt);
        listMakeModel.declVarStmt=declVarStmt;
    }

    protected  void initTree(JCTree jcTree) {
        //JCTree  chainTree = tree;
        jcTree.line = tree.line;
        jcTree.pos = tree.pos;
        jcTree.log = tree.log;
        if (jcTree instanceof JCExpression) {
            JCExpression jcExpression=(JCExpression) jcTree;
            jcExpression.sourceExpr = tree;
        }
        else if (jcTree instanceof JCStatement) {
            JCStatement jcStatement=(JCStatement) jcTree;
            jcStatement.sourceExpr = this.tree;
        }
    }
}

