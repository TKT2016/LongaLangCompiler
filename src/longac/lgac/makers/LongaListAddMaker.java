package longac.lgac.makers;

import longac.attrs.ExprVisitContext;
import longac.lgac.makeModels.LongaListAddModel;
import longac.symbols.JavaClassSymbol;
import longac.symbols.MethodSymbol;
import longac.trees.*;

import java.util.ArrayList;

public class LongaListAddMaker {

    final JCNewList tree;
    final ExprVisitContext context;
    final JCExpression arg;
    final MethodSymbol addMethodSymbol;
    LongaListAddModel longaListAddModel;

    public LongaListAddMaker( ExprVisitContext context,JCNewList tree,JCExpression arg,MethodSymbol addMethodSymbol)
    {
        this.tree =tree;
        this.context =context;
        this.arg =arg;
        this.addMethodSymbol=addMethodSymbol;
    }

    /** objectList.add(arg); */
    public LongaListAddModel make()
    {
        longaListAddModel = new LongaListAddModel();

        tree.listAddModels.add(longaListAddModel);

        makeMaster();
        makeMethod();
        makeMethodInvocation();
        makeStatement();

        return longaListAddModel;
    }

    /**  objectList*/
    void makeMaster()
    {
        longaListAddModel.master = tree.listNewModel.nameIdent;
    }

    /**  objectList.add */
    void makeMethod()
    {
        JCFieldAccess jcFieldAccess = new JCFieldAccess(longaListAddModel.master,LongaListAddModel.methodName );
        initTree(jcFieldAccess);
        jcFieldAccess.setSymbol(addMethodSymbol);
        longaListAddModel.meth = jcFieldAccess;
    }

    /** objectList.add(arg) */
    void makeMethodInvocation()
    {
        ArrayList<JCExpression> args= new ArrayList<>();
        args.add(arg);
        JCMethodInvocation jcMethodInvocation = new JCMethodInvocation( longaListAddModel.meth,args);
        initTree(jcMethodInvocation);
        jcMethodInvocation.setSymbol(addMethodSymbol);
        longaListAddModel.methodInvocation = jcMethodInvocation;
        arg.requireConvertTo = JavaClassSymbol.ObjectSymbol;
    }

    /**
     *  objectList.add(arg);
     */
    void makeStatement( )
    {
        JCExpressionStatement declVarStmt = new JCExpressionStatement(longaListAddModel.methodInvocation);
        initTree(declVarStmt);
        longaListAddModel.invokeStmt =declVarStmt;
    }

    protected void initTree(JCTree jcTree) {
        jcTree.line = arg.line;
        jcTree.pos = arg.pos;
        jcTree.log = arg.log;
        if (jcTree instanceof JCExpression) {
            JCExpression jcExpression=(JCExpression) jcTree;
            jcExpression.sourceExpr = arg;
        }
        else if (jcTree instanceof JCStatement) {
            JCStatement jcStatement=(JCStatement) jcTree;
            jcStatement.sourceExpr = arg;
        }
    }
}
