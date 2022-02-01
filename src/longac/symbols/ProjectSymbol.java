package longac.symbols;

import longac.utils.CompileError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProjectSymbol extends Symbol{
    public ProjectSymbol( ) {
        super("<ProjectSymbol>",null);
    }

    public Map<String,PackageSymbol> packageSymbolMap = new HashMap<>();

    @Override
    public TypeSymbol getTypeSymbol() {
       throw new CompileError();
    }

    public ArrayList<DeclClassSymbol> compiledClassFiles = new ArrayList<>();
}
