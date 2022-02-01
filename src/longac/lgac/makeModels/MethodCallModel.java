package longac.lgac.makeModels;

import longac.trees.*;

public class MethodCallModel {
    public JCExpression master;

    public String methodName;

    public JCFieldAccess meth;

    public JCMethodInvocation methodInvocation;


    public JCExpressionStatement invokeStmt;
}
