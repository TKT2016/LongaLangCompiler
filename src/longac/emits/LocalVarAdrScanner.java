package longac.emits;

import longac.emits.gens.EmitContext;
import longac.utils.Assert;
import tools.collectx.ListMap;
import longac.diagnostics.SimpleLog;
import longac.symbols.DeclMethodSymbol;
import longac.symbols.DeclVarSymbol;
import longac.trees.*;
import longac.utils.CompileContext;
import longac.visitors.TreeScanner;

public class LocalVarAdrScanner extends TreeScanner<LocalVarAdrScanner.LocalVarAdrContext>
{
    SimpleLog log;
    CompileContext context;

    public LocalVarAdrScanner(CompileContext context) {
        this.context = context;
        log =context.log;
    }

    public void visit(JCMethodDecl tree)
    {
        visitMethodDef(tree,null);
    }

    @Override
    public void visitMethodDef(JCMethodDecl tree, LocalVarAdrContext arg)
    {
        DeclMethodSymbol declMethodSymbol = tree.getMethodSymbol();
        ListMap<DeclVarSymbol> parametersMap  = declMethodSymbol.getParametersMap();
        int startAdr = declMethodSymbol.isStatic()?0:1;
        //setLocalAddr(parametersMap,startAdr);
        for(int i=0;i<parametersMap.size();i++)// for(String key : paramsMap.keySet())
        {
            DeclVarSymbol symbol = parametersMap.get(i);
            symbol.setAdr(startAdr);
            startAdr++;
        }
        LocalVarAdrContext context = new LocalVarAdrContext(startAdr);
        tree.body.scan(this,context );
    }

    @Override
    public void visitBlock(JCBlock tree, LocalVarAdrContext arg)
    {
        LocalVarAdrContext newContext = new LocalVarAdrContext(arg.adr);
        //int currentSize = tree.frame.localVars.size();
        //setLocalAddr( tree.frame.localVars,arg );
        for(JCTree stmt:tree.stats)
        {
            if(stmt instanceof JCExpressionStatement || stmt instanceof JCIf
                    || stmt instanceof JCForLoop ||stmt instanceof JCWhile ||stmt instanceof JCBlock  )
                stmt.scan(this,newContext);
        }
    }

    @Override
    public void visitExec(JCExpressionStatement tree, LocalVarAdrContext arg)
    {
        JCExpression expr = tree.expr;
        if(expr instanceof JCVariableDecl
                || expr instanceof JCChain
                || expr instanceof JCNewList
                || expr instanceof JCMethodInvocation
        )
            expr.scan(this,arg);
    }

    @Override
    public void visitVarDef(JCVariableDecl tree, LocalVarAdrContext arg) {
        DeclVarSymbol varSymbol = tree.getDeclVarSymbol();
        varSymbol.setAdr(arg.adr);
        arg.adr++;

        if(tree.init!=null)
            tree.init.scan(this,arg);
    }

    public void visitNewArray(JCNewList tree, LocalVarAdrContext arg)
    {
        Assert.error();
        /*
        DeclVarSymbol varSymbol = tree.listVarSymbol;
        varSymbol.setAdr(arg.adr);
        arg.adr++;*/
    }

    @Override
    public void visitChain(JCChain tree, LocalVarAdrContext arg) {
       // tree.typeNode.init.scan(this,arg);
        /*
        DeclVarSymbol varSymbol =  tree.typeNode.varSymbol ;
        varSymbol.setAdr(arg.adr);
        arg.adr++;

        if(tree.init!=null)
            tree.init.scan(this,arg);*/
    }

    @Override
    public void visitForLoop(JCForLoop tree, LocalVarAdrContext arg)
    {
        LocalVarAdrContext newContext = new LocalVarAdrContext(arg.adr);
        if(tree.init!=null)
            tree.init.scan(this,newContext);
        tree.body.scan(this,newContext);
    }

    class LocalVarAdrContext
    {
        public int adr;
        public LocalVarAdrContext(int adr)
        {
            this.adr = adr;
        }
    }
}
