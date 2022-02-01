package longac.trees;

import longac.lgac.trees.TypeChainTree;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

import java.util.ArrayList;

public class JCChain extends JCExpression {

    //public JCExpression master;

    public ArrayList<JCExpression> nodes;

   /* public JCChain(JCExpression master,ArrayList<JCExpression> nodes)
    {
        this.master = master;
        this.nodes = nodes;
    }*/

    public JCChain(ArrayList<JCExpression> nodes)
    {
        this.nodes = nodes;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg){ v.visitChain(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateChain(this, d);
    }
/*
    public JavaChainSymbol getChainSymbol()
    {
        return (JavaChainSymbol) symbol;
    }
*/
     public TypeChainTree typeNode;


}
