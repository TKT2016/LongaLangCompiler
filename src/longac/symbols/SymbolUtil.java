package longac.symbols;

import tools.jvmx.ClazzUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolUtil {

    public static int getExtendsDeep(TypeSymbol typeSymbol)
    {
        if(typeSymbol instanceof JavaClassSymbol)
        {
            return getExtendsDeep(((JavaClassSymbol)typeSymbol).clazz);
        }
        else
        {
            return 1;
        }
    }

    public static int getExtendsDeep(Class<?> clazz)
    {
        int i=0;
        Class<?> temp = clazz;
        while (temp!=null)
        {
            i++;
            temp=temp.getSuperclass();
        }
        return i;
    }

    public static boolean isStatic(Symbol symbol)
    {
        if(symbol instanceof MethodSymbol)
        {
            return ((MethodSymbol) symbol).isStatic();
        }
        else if(symbol instanceof VarSymbol)
        {
            return ((VarSymbol) symbol).isStatic();
        }
        return false;
    }

    public static DeclVarSymbol getAsLocalDeclVarSymbol(Symbol symbol)
    {
        if(symbol==null) return null;
        if(!(symbol instanceof DeclVarSymbol)) return null;
        DeclVarSymbol declVarSymbol = (DeclVarSymbol) symbol;
        if(!declVarSymbol.isLocalVar())return null;
        return declVarSymbol;
    }

    public static boolean isDeclFiledSymbol(Symbol symbol)
    {
        if(symbol instanceof DeclVarSymbol)
        {
            DeclVarSymbol declVarSymbol =(DeclVarSymbol)symbol;
            if(declVarSymbol.varSymbolKind== VarKind.field)
                return true;
        }
        return false;
    }

    public static DeclClassSymbol getDeclClassSymbol(Symbol symbol) {
        Symbol sym = symbol;
        while (!(sym instanceof DeclClassSymbol)) {
            sym = sym.owner;
            if(sym==null)
                break;
        }
        return (DeclClassSymbol) sym;
    }

    public static boolean isObject(Symbol symbol)
    {
        if(!(symbol instanceof JavaClassSymbol)) return false;
        JavaClassSymbol javaClassSymbol = (JavaClassSymbol)symbol;
        return javaClassSymbol.clazz.equals(Object.class);
    }

    public static boolean isPrimitive(Symbol symbol)
    {
        if(!(symbol instanceof JavaClassSymbol)) return false;
        JavaClassSymbol javaClassSymbol = (JavaClassSymbol)symbol;
        return javaClassSymbol.clazz.equals(boolean.class) || javaClassSymbol.clazz.equals(int.class)|| javaClassSymbol.clazz.equals(double.class);
       // return isVoid(symbol)|| isBoolean(symbol)|| isNumber(symbol);
        //if(!(symbol instanceof JavaClassSymbol)) return false;
        //JavaClassSymbol javaClassSymbol = (JavaClassSymbol)symbol;
        //return javaClassSymbol.equals(JavaClassSymbol.voidPrimitiveSymbol) || javaClassSymbol.clazz.equals(Boolean.class);
    }

    public static boolean isPrimitiveBox(Symbol symbol)
    {
        if(!(symbol instanceof JavaClassSymbol)) return false;
        JavaClassSymbol javaClassSymbol = (JavaClassSymbol)symbol;
        return javaClassSymbol.clazz.equals(Boolean.class) || javaClassSymbol.clazz.equals(Integer.class)|| javaClassSymbol.clazz.equals(Double.class);
    }

    public static boolean isBoolean(Symbol symbol)
    {
        if(!(symbol instanceof JavaClassSymbol)) return false;
        JavaClassSymbol javaClassSymbol = (JavaClassSymbol)symbol;
        return javaClassSymbol.clazz.equals(boolean.class) || javaClassSymbol.clazz.equals(Boolean.class);
    }

    public static boolean isString(Symbol symbol)
    {
        if(!(symbol instanceof JavaClassSymbol)) return false;
        JavaClassSymbol javaClassSymbol = (JavaClassSymbol)symbol;
        return javaClassSymbol.clazz.equals(String.class);
    }
/*
    public static boolean isNumber(Symbol symbol)
    {
        return isInt(symbol)|| isDouble(symbol);
    }*/

    public static boolean isInt(Symbol symbol)
    {
        if(!(symbol instanceof JavaClassSymbol)) return false;
        JavaClassSymbol javaClassSymbol = (JavaClassSymbol)symbol;
        return javaClassSymbol.clazz.equals(int.class) || javaClassSymbol.clazz.equals(Integer.class);
    }
/*
    public static boolean isDouble(Symbol symbol)
    {
        if(!(symbol instanceof JavaClassSymbol)) return false;
        JavaClassSymbol javaClassSymbol = (JavaClassSymbol)symbol;
        return javaClassSymbol.clazz.equals(double.class) || javaClassSymbol.clazz.equals(Double.class);
    }
*/

    public static Symbol checkToPrimitive(Symbol symbol)
    {
        if(!(symbol instanceof JavaClassSymbol)) return symbol;
        JavaClassSymbol javaClassSymbol = (JavaClassSymbol)symbol;
       // if( javaClassSymbol.clazz.equals(Double.class))
     //       return JavaClassSymbol.doublePrimitiveSymbol;
        if( javaClassSymbol.clazz.equals(Integer.class))
            return JavaClassSymbol.intPrimitiveSymbol;
        else if( javaClassSymbol.clazz.equals(Void.class))
            return JavaClassSymbol.voidPrimitiveSymbol;
        else
            return symbol;
    }

    public static boolean isVoid(Symbol symbol)
    {
        if(!(symbol instanceof JavaClassSymbol)) return false;
        JavaClassSymbol javaClassSymbol = (JavaClassSymbol)symbol;
        return javaClassSymbol.clazz.equals(void.class)||javaClassSymbol.clazz.equals(Void.class);
    }

    public static ArrayList<MethodSymbol> matchArgTypes(ArrayList<TypeSymbol> argTypes,ArrayList<MethodSymbol> methodSymbols)
    {
        ArrayList<MethodSymbol> symbolsMatchs = new ArrayList<>();
        int min = -1 ;
        Map<MethodSymbol,Integer> map =new HashMap<>();

        for(Symbol symbol:methodSymbols)
        {
            if(symbol instanceof JavaMethodSymbol)
            {
                JavaMethodSymbol methodSymbol = (JavaMethodSymbol)symbol;
                int k = methodSymbol.matchArgTypes(argTypes);
                if(k>=0)
                {
                    if(min==-1)
                        min=k;
                    else
                        min = Math.min(min,k);
                    symbolsMatchs.add(methodSymbol);
                    map.put(methodSymbol,k);
                }
            }
        }
        if(symbolsMatchs.size()<=1)
            return symbolsMatchs;

        ArrayList<MethodSymbol> symbolsMatchs2 = new ArrayList<>();
        for(MethodSymbol symbol:map.keySet())
        {
            Integer value = map.get(symbol);
            if(value.intValue()==min)
                symbolsMatchs2.add(symbol);
        }

        if(symbolsMatchs2.size()<=1)
            return symbolsMatchs2;

        /* 根据返回值判断 */
        ArrayList<MethodSymbol> symbolsMatchs3 = new ArrayList<>();
        int deep = -1;

        //JavaClassSymbol classSymbol = (JavaClassSymbol)symbolsMatchs2.get(0).owner;
        for(MethodSymbol symbol:symbolsMatchs2)
        {
            int symboldeep = SymbolUtil.getExtendsDeep(symbol.returnTypeSymbol);
            //Class<?> dclass = ((JavaClassSymbol)symbol.getTypeSymbol()).clazz;
           // int symboldeep = ClazzUtil.getExtendsDeep(dclass);
            //int newdeep = Math.max(deep,symboldeep);
            if(symboldeep==deep)
            {
                symbolsMatchs3.add(symbol);
            }
            else if(symboldeep>deep)
            {
                symbolsMatchs3.clear();
                symbolsMatchs3.add(symbol);
                deep = symboldeep;
            }
            //deep = newdeep;
        }

        return symbolsMatchs3;
    }

}
