package longac.lgac.makeModels;

import longac.symbols.DeclVarSymbol;
import longac.symbols.JavaVarSymbol;
import longac.trees.*;

public class FieldTypeMakeModel {
    /** select.join = select.join.__start(args); */

    public JavaVarSymbol fieldSymbol;

    /** left select */
    public JCExpression leftVarIdent;

    /** left select.join */
    public JCFieldAccess left;

    /** right select */
    public JCExpression rigtVarIdent;

    /** right select.join */
    public JCFieldAccess rightFieldGet;

    /** right select.join.__start */
    public JCFieldAccess rigthStartMethod;

    /** right select.join.__start(args) */
    public JCMethodInvocation rightStartInvocation;

    /** select.join = select.join.__start(args) */
    public JCAssign assign;

    /** select.join = select.join.__start(args); */
    public JCExpressionStatement assignStatement;
}
