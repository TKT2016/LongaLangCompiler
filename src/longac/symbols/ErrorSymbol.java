package longac.symbols;

import java.util.ArrayList;

public class ErrorSymbol extends TypeSymbol
{
    public ErrorSymbol() {
        super( "<ErrorSymbol>",null);
    }

    @Override
    public TypeSymbol getTypeSymbol() {
       return JavaClassSymbol.create(Object.class);
    }

    @Override
    public int match(TypeSymbol another)
    {
        return -1;
        //if(SymbolUtil.isPrimitive(another))
        //    return -1;
        //return 0;
    }

    @Override
    public boolean equalType(TypeSymbol a)
    {
        return (a instanceof ErrorSymbol);
    }

    @Override
    public ArrayList<Symbol> findMembers(String name,boolean isStatic)
    {
        return new ArrayList<>();
    }

    @Override
    public boolean isAssignableFrom(TypeSymbol another)
    {
        if(SymbolUtil.isPrimitive(another))
            return false;
        return true;
    }

    @Override
    public ArrayList<MethodSymbol> findConstructor(ArrayList<TypeSymbol> argTypes)
    {
        return new ArrayList<>();
    }

    @Override
    public VarSymbol findField(String name)
    {
        return null;
    }

}
