package longac.symbols;

import longac.utils.Assert;

import java.util.HashMap;
import java.util.Map;

public class PackageSymbol extends Symbol
{
    public final String fullname;

    protected Map<String,TypeSymbol> typesMap = new HashMap<>();

    public PackageSymbol(String name,Symbol owner) {
        super(name,owner);
        this.fullname =name;//SymbolUtil.formFullName(name, owner);
    }

    public String getFullname() {
        return fullname;
    }

    @Override
    public TypeSymbol getTypeSymbol() {
        Assert.error();
        return null;
    }

    public TypeSymbol findTypeFromSimpleClassName(String className) {
        String fullClassName = this.fullname +"."+className;
        TypeSymbol typeSymbol = JavaClassSymbol.forName(fullClassName);
        return  typeSymbol;
    }

    @Override
    public String getSignature(boolean addL)
    {
        return this.fullname.replace('.','/');
    }

    public boolean contains(String name)
    {
        return  typesMap.containsKey(name);
    }

    public TypeSymbol getType(String name)
    {
        return  typesMap.get(name);
    }

    public void add(DeclClassSymbol declClassSymbol)
    {
        typesMap.put(declClassSymbol.name,declClassSymbol);
    }

    public String toString() {
        return fullname;
    }
}
