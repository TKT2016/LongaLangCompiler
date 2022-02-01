package longac.symbols;

import com.sun.org.apache.bcel.internal.generic.RET;
import longac.attrs.FindKinds;
import longac.utils.Assert;
import java.util.ArrayList;

public class SourceFileSymbol extends Symbol //implements SymbolFrame
{
    public SourceFileSymbol(String name, ProjectSymbol projectSymbol) {
        super(name,projectSymbol);
    }

    public PackageSymbol packageSymbol;

    public ProjectSymbol getProjectSymbol()
    {
        if(this.owner==null)
            return null;
        return (ProjectSymbol) owner;
    }

    public ArrayList<ImportSymbol> importSymbols = new ArrayList<>();

    public ArrayList<TypeSymbol> findTypes(TypeNameModel typeNameModel) {
        String className = typeNameModel.typeSimpleName;
        ArrayList<TypeSymbol> typeSymbols = new ArrayList<>();
        if(typeNameModel.isFull()) {
            TypeSymbol typeSymbol = JavaClassSymbol.forName(typeNameModel.getNameFull());
            if (typeSymbol != null) {
                typeSymbols.add(typeSymbol);
                return typeSymbols;
            }
            ProjectSymbol projectSymbol = getProjectSymbol();
            if(projectSymbol.packageSymbolMap.containsKey(typeNameModel.packageName))
            {
                PackageSymbol packageSymbol = projectSymbol.packageSymbolMap.get(typeNameModel.typeSimpleName);
                if (packageSymbol.contains(className))
                    typeSymbols.add(packageSymbol.getType(className));
            }
        }
        else {
            for (ImportSymbol importSymbol : importSymbols) {
                TypeSymbol typeSymbol2 = importSymbol.findSymbol(className);
                if (typeSymbol2 != null)
                    typeSymbols.add(typeSymbol2);
            }
            if (packageSymbol.contains(className))
                typeSymbols.add(packageSymbol.getType(className));
        }
        return typeSymbols;
    }

    public ArrayList<VarSymbol> findVars(String name)
    {
        ArrayList<VarSymbol> typeSymbols = new ArrayList<>();
        for (ImportSymbol importSymbol : importSymbols) {
            if(importSymbol.isStatic==false)
                continue;
            VarSymbol varSymbol = importSymbol.findVar(name);
            if (varSymbol != null)
                typeSymbols.add(varSymbol);
        }
        return typeSymbols;
    }

    @Override
    public TypeSymbol getTypeSymbol() {
        Assert.error();
        return null;
    }
/*
    @Override
    public SymbolFrame getParent()
    {
        return null;
    }

    @Override
    public  void addVar(DeclVarSymbol varSymbol)
    {
        Assert.error();
    }

    @Override
    public ArrayList<Symbol> findVars(String varName, FindKinds findKinds)
    {
        ArrayList<Symbol> arrayList = new ArrayList<>();
        ArrayList<VarSymbol>  varSymbols = findVars(varName);
        arrayList.addAll(varSymbols);
        return arrayList;
    }
*/
}
