package longac.lgac;

import longac.attrs.ExprVisitContext;
import longac.attrs.StmtExpAttrTranslator;
import longac.trees.JCChain;
import longac.trees.JCExpression;

import java.util.ArrayList;

public class NodeReader
{
    final JCChain tree;
    ArrayList<JCExpression> nodes;
    JCExpression node;
    int index = -1 ;

    public NodeReader(JCChain tree)
    {
        this.tree=tree;
        nodes= tree.nodes;
        next();
    }


    void next()
    {
        index++;
        if(index<nodes.size()) {
            node = nodes.get(index);
        }
        else {
            node = null;
        }
    }
}
