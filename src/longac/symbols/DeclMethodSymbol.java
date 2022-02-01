package longac.symbols;

import tools.collectx.ListMap;
import tools.jvmx.NamesTexts;
import longac.attrs.FindKinds;
import org.objectweb.asm.Label;

import java.util.ArrayList;

public class DeclMethodSymbol extends MethodSymbol implements SymbolFrame {

    public DeclMethodSymbol(String name, Symbol owner,TypeSymbol returnType )
    {
        super(name,owner);
        this.returnTypeSymbol =returnType;
        //this.parameterCount = parameterCount;
    }

    ListMap<DeclVarSymbol> parametersMap = new ListMap<>() ;
    ListMap<DeclVarSymbol> allVars = new ListMap<>();

   // HashMap<String,DeclVarSymbol> parametersMap = new HashMap<>() ;
   // ArrayList<DeclVarSymbol> allVars = new ArrayList<>();

    public SymbolFrame getParent()
    {
        return (DeclClassSymbol)owner;
    }

    public boolean containsParameter(String varName) {
        return (parametersMap.contains(varName));
    }

    public boolean containsVar(String varName, FindKinds findKinds)
    {
        if(findKinds.isFindVar)
            return parametersMap.contains(varName);
        return false;
    }

    public ArrayList<Symbol> findVars(String varName, FindKinds findKinds)
    {
        ArrayList<Symbol> list = new ArrayList<>();
        if(findKinds.isFindVar) {
            if (parametersMap.contains(varName)) {
                list.add(parametersMap.get(varName));
            }
        }
        return list;
    }

    public void addVar(DeclVarSymbol varSymbol)
    {
        if(varSymbol.varSymbolKind== VarKind.parameter)
            parametersMap.put(varSymbol.name, varSymbol);
        allVars.put(varSymbol.name,varSymbol);
    }

    public int getVarCount()
    {
        return allVars.size();
    }

    public DeclVarSymbol getDeclSymbols(int i)
    {
        return allVars.get(i);
    }

    public int getParameterCount()
    {
        return parametersMap.size();
    }

    public ListMap<DeclVarSymbol> getParametersMap()
    {
        return parametersMap;
    }

    @Override
    public VarSymbol getParameterSymbol(int i)
    {
        return allVars.get(i);
    }

    public ArrayList<TypeSymbol> getParameterTypes()
    {
        ArrayList<TypeSymbol> arrayList = new ArrayList<>();
        for(int i=0;i<parametersMap.size();i++) {
            DeclVarSymbol symbol = parametersMap.get(i);
            arrayList.add(symbol.getTypeSymbol());
        }
       /* for(String key : parametersMap.keySet()) {
            DeclVarSymbol declVarSymbol = parametersMap.get(key);
            arrayList.add(declVarSymbol.getTypeSymbol());
        }*/
        return arrayList;
    }

    @Override
    public boolean isContructor()
    {
        return this.name.equals( NamesTexts.init);
    }

    @Override
    public boolean isStatic()
    {
        return false;
    }

    public DeclClassSymbol getDeclClassSymbol()
    {
        return (DeclClassSymbol) owner;
    }

    public Label endLabel;

}
