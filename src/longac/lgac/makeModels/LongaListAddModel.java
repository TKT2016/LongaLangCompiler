package longac.lgac.makeModels;

import longac.trees.JCExpression;
import longac.trees.JCExpressionStatement;
import longac.trees.JCFieldAccess;
import longac.trees.JCMethodInvocation;

public class LongaListAddModel {
    public JCExpression master;

    public static final String methodName="add";

    public JCFieldAccess meth;

    public JCMethodInvocation methodInvocation;


    public JCExpressionStatement invokeStmt;
}
