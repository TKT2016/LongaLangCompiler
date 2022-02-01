package longac.trees;


/** 语句父类 */
public abstract class JCStatement extends JCTree
{
    public JCStatement()
    {

    }

    public boolean isEffective;

    @Override
    public boolean isEffective()
    {
        return isEffective;
    }

    public JCExpression sourceExpr;
}
