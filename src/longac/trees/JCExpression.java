package longac.trees;

/** 表达式抽象语法树父类 */
public abstract class JCExpression extends JCTree
{
    public JCExpression()
    {

    }

    /* 是否是左边被赋值的表达式 */
    public boolean isAssignLeft = false;

    public boolean isEffective;

    @Override
    public boolean isEffective()
    {
        return isEffective;
    }

    public JCExpression sourceExpr;
}
