package longac.lgac.symbols;

import longac.symbols.JavaClassSymbol;
import longac.symbols.JavaMethodSymbol;

import java.util.ArrayList;

public abstract class TypeChainSymbol {
    public JavaClassSymbol typeSymbol;

    //public ArrayList<JavaMethodSymbol> startMethodSymbols;

    public JavaMethodSymbol endMethodSymbol;

}
