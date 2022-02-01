package longac.analyzers;

import longac.diagnostics.SimpleLog;
import longac.symbols.SymbolUtil;
import longac.symbols.TypeSymbol;
import tools.collectx.ArrayListUtil;
import longac.utils.CompileContext;
import longac.visitors.TreeScanner;

import java.util.ArrayList;
import longac.trees.*;
public class ReturnAnalyzer extends TreeScanner<ReturnAnalyzer.ReturnFlowTree> {
    SimpleLog log;
    CompileContext context;

    public ReturnAnalyzer(CompileContext context )
    {
        this.context = context;
        log =context.log;
    }

    @Override
    public void visitMethodDef(JCMethodDecl tree, ReturnFlowTree arg)
    {
        if(tree.isContructor())
            return;
        TypeSymbol retType = tree.getSymbol().getTypeSymbol();
        if(SymbolUtil.isVoid(retType))
            return;
        ReturnFlowTree returnFlowTree = new ReturnFlowTree();
        tree.body.scan(this,returnFlowTree);
        if(!returnFlowTree.exist)
        {
            tree.body.error("缺少返回语句");
        }
    }

    @Override
    public void visitIf(JCIf tree, ReturnFlowTree arg)
    {
       // ReturnFlowTree parentFlowTree =  (ReturnFlowTree) arg;
       // ReturnFlowTree thisFlowTree = new ReturnFlowTree(tree);
        //thisFlowTree.parent = parentFlowTree;
        //parentFlowTree.children.add(thisFlowTree);
        ReturnFlowTree thenTree = arg.createChild();
        tree.thenpart.scan(this,thenTree);
        ReturnFlowTree elseTree = arg.createChild();
        if(tree.elsepart!=null)
            tree.elsepart.scan(this,elseTree);

       /* tree.thenpart.scan(this,thisFlowTree);
        if(tree.elsepart!=null)
            tree.elsepart.scan(this,thisFlowTree);*/
    }

    @Override
    public void visitReturn(JCReturn tree, ReturnFlowTree arg)
    {
        ReturnFlowTree returnFlowTree =  (ReturnFlowTree) arg;
        returnFlowTree.exist = true ;
        returnFlowTree.setParentExist();
    }

    class ReturnFlowTree
    {
        public ReturnFlowTree(/*JCTree tree*/)
        {
            /*this.tree = tree;*/
        }

        public ReturnFlowTree parent;
        //public JCTree tree;
        public boolean exist =false;
        public ArrayList<ReturnFlowTree> children = new ArrayList<>();

        public boolean childrenAllExist()
        {
            if(ArrayListUtil.nonEmpty(children))
            {
                for(ReturnFlowTree sub :children)
                {
                    if(!sub.exist)
                        return  false;
                }
                return true;
            }
            return true;
        }

        public void setParentExist()
        {
            if(parent==null) return;
            if(parent.childrenAllExist())
            {
                this.parent.exist =true;
                parent.setParentExist();
            }
        }

        public ReturnFlowTree createChild()
        {
            ReturnFlowTree newTree = new ReturnFlowTree();
            newTree.parent = this;
            this.children.add(newTree);
            return newTree;
        }
    }
}
