package longac.optimizers;

import longac.symbols.VarKind;
import tools.collectx.ArrayListUtil;
import longac.lex.TokenKind;
import longac.symbols.VarSymbol;
import longac.utils.Assert;
import longac.utils.CompileContext;
import longac.visitors.TreeTranslator;
import longac.trees.*;

import java.util.ArrayList;

public class ConstValueOptimizer extends TreeTranslator<ConstValueOptimizerContext> {
    final CompileContext context;
    //final Log log;

    public ConstValueOptimizer(CompileContext context)
    {
        this.context= context;
       // this.log= context.log;
    }

    @Override
    public JCTree translateMethod(JCMethodDecl tree, ConstValueOptimizerContext arg)
    {
        tree.body = (JCBlock) tree.body.translate(this,arg);
        return tree;
    }

    @Override
    public JCTree translateBlock(JCBlock tree, ConstValueOptimizerContext arg)
    {
        ArrayList<JCStatement> newStatements = new  ArrayList<JCStatement>();
        ConstValueOptimizerContext optimizerContext = new ConstValueOptimizerContext();

        for (JCStatement statement : tree.stats)
        {
            JCStatement newStatement = (JCStatement) statement.translate(this, optimizerContext);
            if(newStatement!=null)
                newStatements.add(newStatement);
            if(newStatement instanceof JCIf
                    || newStatement instanceof JCWhile
                    || newStatement instanceof JCForLoop
            )
            {
                optimizerContext.clear(); //清空常量表
            }

        }

        ArrayList<JCStatement> newStatements2 = new  ArrayList<JCStatement>();
        for (JCStatement statement : newStatements)
        {
            if( ConstValueOptimizerContext.getAssignEffective(statement))
                newStatements2.add(statement);
        }

        tree.stats = newStatements2;
        return tree;
    }


    @Override
    public JCTree translateAssign(JCAssign tree, ConstValueOptimizerContext arg)
    {
        tree.left =  translate(tree.left,arg);
        tree.right =  translate(tree.right,arg);
        if(OptimizerUtil.isVar(tree.left))
        {
            VarSymbol varSymbol = OptimizerUtil.getVarSymbol(tree.left);
            arg.setAssignMap(varSymbol,tree);
            Object constValue = arg.getConstValue(tree.right);
            arg.valueMap.replace(varSymbol,constValue);
        }

       // if(varSymbol!=null) {
         //   if (varSymbol.lastAssignTree == null)
                //varSymbol.lastAssignTree = tree;
        //    else
        //    {
                //varSymbol.lastAssignTree.effectiveAssign = false;
                //varSymbol.lastAssignTree = tree;
        //    }

        //    Object constValue = OptimizerUtil.getConstValue(tree.right);
        //    if(constValue !=null)
        //    {
         //       varSymbol.assignedConstValue =constValue;
        //    }
            /*
            if(tree.right  instanceof JCLiteral)
            {
                varSymbol.assignedConstValue = ((JCLiteral)tree.right).value;
            }*/
     //   }
        return tree;
    }

    @Override
    public JCTree translateNewArray(JCNewList tree, ConstValueOptimizerContext arg)
    {

        if(ArrayListUtil.nonEmpty(tree.elems))
        {
            ArrayList<JCExpression> elems2 = new ArrayList<>();
            for(JCExpression jcExpression:tree.elems)
            {
                JCExpression expression = translate(jcExpression,arg);
                elems2.add(expression);
            }
            tree.elems =elems2;
        }
        return tree;
    }

    @Override
    public JCTree translateNewClass(JCNewClass tree, ConstValueOptimizerContext arg)
    {
        if(tree.args!=null)
            tree.args = translates(tree.args,arg);
        return tree;
    }

    @Override
    public JCTree translateIdent(JCIdent tree, ConstValueOptimizerContext arg)
    {
        if(OptimizerUtil.isVar(tree))
        {
            Object constValue = arg.getConstValue(tree);
            if(constValue!=null)
                return OptimizerUtil.newLiteral(tree,constValue);
        }
        return tree;
    }

    @Override
    public JCTree translateUnary(JCUnary tree, ConstValueOptimizerContext arg)
    {
        tree.expr =   translate( tree.expr,arg);
        TokenKind opcode = tree.opcode;
        if(opcode.equals(TokenKind.ADD))
            return tree.expr ;

        Object value = arg.getConstValue(tree.expr); //OptimizerUtil. getConstValue(tree.expr);
        if(value!=null) {
            if (opcode.equals(TokenKind.SUB)) {
                Integer ivalue = (Integer) value;
                int resultValue = -ivalue.intValue();
                return OptimizerUtil.newIntLiteral(tree.expr,resultValue,TokenKind.INTLITERAL);
            }
            else if (opcode.equals(TokenKind.NOT)) {
                Boolean aBoolean = (Boolean) value;
                boolean resultValue = ! aBoolean.booleanValue();
                return OptimizerUtil.newBooleanLiteral(tree.expr,resultValue);
            }
            else
            {
                Assert.error();
            }
        }
        return tree;
    }

    @Override
    public JCTree translateReturn(JCReturn tree, ConstValueOptimizerContext arg)
    {
        tree.expr =  (JCExpression)tree.expr.translate (this,arg);
        return tree;
    }

    @Override
    public JCTree translateIf(JCIf tree, ConstValueOptimizerContext arg)
    {
        tree.cond = (JCExpression)tree.cond.translate (this,arg);
        tree.thenpart = (JCStatement) tree.thenpart.translate(this, arg);
        if (tree.elsepart != null) {
            tree.elsepart = (JCStatement) tree.elsepart.translate(this, arg);
        }
        return tree;
    }

    @Override
    public JCTree translateWhile(JCWhile tree, ConstValueOptimizerContext arg)
    {
        tree.cond   = translate (tree.cond,arg);
        tree.body = (JCStatement)tree.body.translate (this,arg);
        return tree;
    }

    @Override
    public JCTree translateVariable(JCVariableDecl tree, ConstValueOptimizerContext arg) {
        //Assert.checkNonNull(arg);
        if(tree.dimKind!=  VarKind.localVar)
            return tree;
        if(arg==null)
            Assert.error();
        if (tree.init != null) {
            VarSymbol varSymbol = tree.getDeclVarSymbol();
            arg.setAssignMap(varSymbol,tree);
            tree.init = (JCExpression) tree.init.translate(this, arg);
            Object constValue = arg.getConstValue(tree.init);
            arg.valueMap.replace(varSymbol,constValue);
        }
        return tree;
    }

    @Override
    public JCTree translateMethodInvocation(JCMethodInvocation tree, ConstValueOptimizerContext arg)
    {
        tree.args = translates(tree.args,arg);
        return tree;
    }

    @Override
    public JCTree translateForLoop(JCForLoop tree, ConstValueOptimizerContext arg){
        //if(tree.init!=null)
        //     tree.init = translates(tree.init,arg) ;
        if(tree.init!=null)
            tree.init = (JCStatement) tree.init.translate(this,arg);

        if(tree.cond!=null)
            tree.cond  = (JCExpression)tree.cond.translate (this,arg);
        //if(tree.step!=null)
        //    tree.step =translates(tree.step,arg) ;
        if(tree.step!=null)
            tree.step = (JCExpression) tree.step.translate(this,arg);
        if(tree.body!=null)
            tree.body = (JCStatement)tree.body.translate (this,arg);
        return tree;
    }

    @Override
    public JCTree translateBinary(JCBinary tree, ConstValueOptimizerContext arg)
    {
        JCExpression newTreeLeft = translate(tree.left, arg);
        JCExpression newTreeRight = translate(tree.right, arg);
       // TokenKind opcode= tree.opcode;
        Object leftValue = arg.getConstValue(newTreeLeft);// OptimizerUtil.getConstValue(newTreeLeft);
        Object rightValue =  arg.getConstValue(newTreeRight);// OptimizerUtil.getConstValue(newTreeRight);
        TokenKind op = tree.opcode;

        switch (op) {
            case AND:
                if (OptimizerUtil.isTrue(leftValue) && OptimizerUtil.isTrue(rightValue))
                    return OptimizerUtil.newBooleanLiteral(tree, true);
                else if (OptimizerUtil.isFalse(leftValue) || OptimizerUtil.isFalse(rightValue))
                    return OptimizerUtil.newBooleanLiteral(tree, false);
                break;
            case OR:
                if (OptimizerUtil.isTrue(leftValue) || OptimizerUtil.isTrue(rightValue))
                    return OptimizerUtil.newBooleanLiteral(tree, true);
                else if (OptimizerUtil.isFalse(leftValue) && OptimizerUtil.isFalse(rightValue))
                    return OptimizerUtil.newBooleanLiteral(tree, false);
                break;
            case GT:
                if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
                    return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue > (Integer) rightValue);
                break;
            case GTEQ:
                if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
                    return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue >= (Integer) rightValue);
                break;
            case EQEQ:
                if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
                    return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue == (Integer) rightValue);
                break;
            case LTEQ:
                if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
                    return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue <= (Integer) rightValue);
                break;

            case NOTEQ:
                if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
                    return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue != (Integer) rightValue);
                break;
            case LT:
                if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
                    return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue < (Integer) rightValue);
                break;

            case ADD:
                if (tree.isStringAppend) {
                    if (OptimizerUtil.isString(leftValue) && OptimizerUtil.isString(rightValue))
                        return OptimizerUtil.newStringLiteral(tree, (String) leftValue + (String) rightValue);
                }
                else {
                    if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
                        return OptimizerUtil.newIntLiteral(tree, (Integer) leftValue + (Integer) rightValue);
                }
                break;
            case SUB:
                if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
                    return OptimizerUtil.newIntLiteral(tree, (Integer) leftValue - (Integer) rightValue);
                break;
            case MUL:
                if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
                    return OptimizerUtil.newIntLiteral(tree, (Integer) leftValue * (Integer) rightValue);
                else if(OptimizerUtil.isIntZero(leftValue) || OptimizerUtil.isIntZero(rightValue))
                    return OptimizerUtil.newIntLiteral(tree, 0);
                else if(OptimizerUtil.isIntOne(leftValue))
                    return newTreeRight;
                else if(OptimizerUtil.isIntOne(rightValue))
                    return newTreeLeft;
                break;
            case DIV:
                if(OptimizerUtil.isInt(leftValue)&&OptimizerUtil.isInt(rightValue))
                    return OptimizerUtil.newIntLiteral(tree, (Integer) leftValue / (Integer) rightValue);
                else if(OptimizerUtil.isIntOne(rightValue))
                    return newTreeLeft;
                break;
            default:
                Assert.error();

        }
         tree.left = newTreeLeft;
        tree.right=  newTreeRight;
        return tree;
    }

    @Override
    protected boolean needTranslate(JCTree tree)
    {
        return true;
        //return (tree instanceof JCSourceFile || tree instanceof JCClassDecl ||tree instanceof JCMethodDecl
        //        ||tree instanceof JCBlock || tree instanceof JCWhile ||  tree instanceof JCIf );
    }
}
