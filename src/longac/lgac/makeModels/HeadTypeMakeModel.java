package longac.lgac.makeModels;

import longac.symbols.DeclVarSymbol;
import longac.trees.JCExpressionStatement;
import longac.trees.JCIdent;
import longac.trees.JCNewClass;
import longac.trees.JCVariableDecl;

public class HeadTypeMakeModel {
    //public  String name_chain;

    public String localVarName;

    public DeclVarSymbol declVarSymbol;

    public JCIdent typeExpr;

    public JCIdent nameIdent;
    
    public JCNewClass jcNewClass;

    public JCVariableDecl variableDecl;

    public JCExpressionStatement declVarStmt;
}
