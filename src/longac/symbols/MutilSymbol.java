package longac.symbols;

import tools.jvmx.ClazzUtil;
import longac.utils.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MutilSymbol extends Symbol{

    public ArrayList<Symbol> symbols;

    public MutilSymbol(String name, Symbol owner, ArrayList<Symbol> symbols)
    {
        super(name,owner);
        this.symbols =symbols;
    }

    @Override
    public TypeSymbol getTypeSymbol()
    {
        Assert.error();
        throw new AssertionError();
    }

    public ArrayList<MethodSymbol> matchArgTypes(ArrayList<TypeSymbol> argTypes)
    {
        ArrayList<MethodSymbol> tempMethodSymbols = new ArrayList<>();
        for(Symbol symbol:this.symbols)
        {
            if(symbol instanceof MethodSymbol)
            {
                tempMethodSymbols.add((MethodSymbol)symbol);
            }
        }
        return SymbolUtil.matchArgTypes(argTypes,tempMethodSymbols);
        
    }

}
