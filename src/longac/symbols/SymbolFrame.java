package longac.symbols;

import longac.attrs.FindKinds;

import java.util.ArrayList;

public interface SymbolFrame
{
    SymbolFrame getParent();
    boolean containsVar(String varName, FindKinds findKinds);
    ArrayList<Symbol> findVars(String varName, FindKinds findKinds);
    void addVar(DeclVarSymbol varSymbol);
}
