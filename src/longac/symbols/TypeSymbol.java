package longac.symbols;

import java.util.ArrayList;

public abstract class TypeSymbol extends Symbol {

    public TypeSymbol(String name,Symbol owner)
    {
        super(name,owner);
    }

    public abstract VarSymbol findField(String name);

    //public abstract ArrayList<MethodSymbol> findMethod(String name, ArrayList<TypeSymbol> argTypes);

    public abstract ArrayList<MethodSymbol> findConstructor(ArrayList<TypeSymbol> argTypes);

    @Override
    public TypeSymbol getTypeSymbol() {
        return this;
    }

    public abstract  boolean isAssignableFrom(TypeSymbol typeSymbolSub);

    public boolean isInterface()
    {
        return false;
    }

    public TypeSymbol getElementType()
    {
        return this;
    }

    public abstract ArrayList<Symbol> findMembers(String name,boolean isStatic);

    public abstract boolean equalType(TypeSymbol a);

    public abstract int match(TypeSymbol another);

}
