package longac.attrs;

import longac.symbols.BlockFrame;
import longac.symbols.DeclClassSymbol;
import longac.symbols.DeclMethodSymbol;
import longac.symbols.SymbolFrame;
import longac.trees.JCStatement;

import java.util.ArrayList;

public class ExprVisitContext {
    public DeclClassSymbol classSymbol;
    public DeclMethodSymbol methodSymbol;

    public SymbolFrame frame;
    public FindKinds findKinds;

    public ExprVisitContext clone()
    {
        return this.clone(this.findKinds);
    }

    public ExprVisitContext clone(FindKinds findKinds)
    {
        ExprVisitContext context = new ExprVisitContext();
        context.classSymbol = this.classSymbol;
        context.methodSymbol = this.methodSymbol;
        context.frame = this.frame;
        context.findKinds = findKinds;
        context.insertStatements = insertStatements;
        return context;
    }

    public ExprVisitContext createBlock()
    {
        ExprVisitContext context = new ExprVisitContext();
        context.classSymbol = this.classSymbol;
        context.methodSymbol = this.methodSymbol;
        if(this.frame instanceof BlockFrame)
        {
            context.frame = ((BlockFrame) this.frame).createChild();
        }
        else {
            context.frame = new BlockFrame(this.frame);
        }
        context.findKinds = findKinds;
        return context;
    }

    public ArrayList<JCStatement> insertStatements;
}
