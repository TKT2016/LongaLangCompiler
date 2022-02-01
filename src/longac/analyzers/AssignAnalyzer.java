package longac.analyzers;
import longac.diagnostics.SimpleLog;
import longac.trees.*;
import longac.symbols.*;
import longac.utils.CompileContext;

import longac.visitors.ExprScanner;

import java.util.ArrayList;
import java.util.HashMap;

public class AssignAnalyzer extends ExprScanner<AssignAnalyzer.AssignFlowTree> //ExprVisitor
{
    public final CompileContext context;
    public final SimpleLog log;

    public AssignAnalyzer(CompileContext context ) {
        this.context=context;
        log =context.log;
    }

    public void analyzeTree(JCFileTree compilationUnit) {
        super.visitCompilationUnit(compilationUnit,null);
    }

    @Override
    public void visitMethodDef(JCMethodDecl tree, AssignFlowTree arg)
    {
        AssignFlowTree returnFlowTree = new AssignFlowTree();
        tree.body.scan(this,returnFlowTree);
    }

    @Override
    public void visitVarDef(JCVariableDecl tree, AssignFlowTree arg) {
        DeclVarSymbol declVarSymbol = tree.getDeclVarSymbol();
        if(declVarSymbol.isLocalVar() ) {
            if (tree.init != null) {
                arg.setAssign(declVarSymbol);
                tree.init.scan(this,arg);
            }
        }
    }

    @Override
    public void visitAssign(JCAssign tree, AssignFlowTree arg) {
        DeclVarSymbol declVarSymbol = SymbolUtil.getAsLocalDeclVarSymbol(tree.left.getSymbol());
        if(declVarSymbol!=null)
        {
            arg.setAssign(declVarSymbol);
        }
        tree.right.scan(this,arg);
    }

    @Override
    public void visitIdent(JCIdent tree, AssignFlowTree arg) {
        DeclVarSymbol declVarSymbol = SymbolUtil.getAsLocalDeclVarSymbol(tree.getSymbol());
        if(declVarSymbol!=null)
        {
            if(!arg.isAssigned(declVarSymbol))
            {
                tree.error(String.format("可能尚未初始化变量'%s'",declVarSymbol.name));
            }
        }
    }
/*
    @Override
    public void visitForLoop(JCForLoop tree, AssignFlowTree arg)
    {
        if (tree.cond != null) {
            tree.cond.scan(this,arg);
        }
        for(JCExpressionStatement jcExpressionStatement:tree.step)
        {
            jcExpressionStatement.scan(this,arg);
        }
        tree.body.scan(this,arg);
    }
*/
    @Override
    public void visitIf(JCIf tree, AssignFlowTree arg)
    {
       // AssignFlowTree thisFlowTree = arg.createChild();
       // super.visitIf(tree,thisFlowTree);
        AssignFlowTree thenTree = arg.createChild();
        tree.thenpart.scan(this,thenTree);
        AssignFlowTree elseTree = arg.createChild();
        if(tree.elsepart!=null)
            tree.elsepart.scan(this,elseTree);
    }

    class AssignFlowTree
    {
        AssignFlowTree parent;
        ArrayList<AssignFlowTree> children = new ArrayList<>();
        //public JCTree tree;
        ArrayList<AssignFlowTree> path = new ArrayList<>();
        HashMap<DeclVarSymbol,Boolean> assignedSymbolMap = new HashMap<>();

        private AssignFlowTree()
        {
            path.add(this);
           // this.tree = tree;
        }

        public boolean isAssigned(DeclVarSymbol declVarSymbol)
        {
            for(AssignFlowTree tree:path)
            {
                if(tree.assignedSymbolMap.containsKey(declVarSymbol))
                    return true;
            }
            return false;//assignedSymbolMap.containsKey(declVarSymbol);
        }

        public void setAssign(DeclVarSymbol declVarSymbol)
        {
            this.curAdd(declVarSymbol);
            if(parent!=null)
            {
                if(parent.curContains(declVarSymbol))
                    return;
                for (AssignFlowTree child:parent.children)
                {
                    if(!child.assignedSymbolMap.containsKey(declVarSymbol))
                        return;
                }
                this.parent.curAdd(declVarSymbol);
            }
        }

        private boolean curContains(DeclVarSymbol declVarSymbol )
        {
            return this.assignedSymbolMap.containsKey(declVarSymbol);
        }

        private void curAdd(DeclVarSymbol declVarSymbol )
        {
           if(!this.assignedSymbolMap.containsKey(declVarSymbol))
               this.assignedSymbolMap.put(declVarSymbol,true);
        }

/*
        public boolean exist =false;


        public boolean childrenAllExist()
        {
            if(ArrayListUtil.nonEmpty(children))
            {
                for(AssignFlowTree sub :children)
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
        }*/

        public AssignFlowTree createChild( )
        {
            AssignFlowTree child = new AssignFlowTree();
            child.parent = this;
            child.path.addAll(this.path);
            child.path.add(child);
            this.children.add(child);
            return child;
        }
    }
}
