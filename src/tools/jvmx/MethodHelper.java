package tools.jvmx;

import longac.symbols.JavaClassSymbol;
import longac.symbols.TypeSymbol;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MethodHelper {
    public static int Match(Method method, ArrayList<TypeSymbol> argTypes)
    {
        Class<?>[] parameterTypes =  method.getParameterTypes();
        int result =MethodHelper.isAssignFrom(parameterTypes,argTypes);
        return result;
            //MethodSymbol methodSymbol = new MethodSymbol(method,this,0);
            //methodSyms.add(methodSymbol);
    }

    public static int isAssignFrom(Class<?>[] parameterTypes, ArrayList<TypeSymbol> argTypes) {
        if (parameterTypes.length != argTypes.size())
            return 0;

        for (int i = 0; i < parameterTypes.length; i++) {
            TypeSymbol typeSymbol = JavaClassSymbol.create(parameterTypes[i]);// new JavaClassSymbol(parameterTypes[i]);
            if (!typeSymbol.isAssignableFrom(argTypes.get(i))) {
                return 0;
            }
        }
        return 1;
    }

    public static int isAssignFrom(ArrayList<TypeSymbol> parameterTypes, ArrayList<TypeSymbol> argTypes) {
        if (parameterTypes.size() != argTypes.size())
            return 0;

        for (int i = 0; i < parameterTypes.size(); i++) {
            TypeSymbol typeSymbol =parameterTypes.get(i);// new JavaClassSymbol(parameterTypes[i]);
            if (!typeSymbol.isAssignableFrom(argTypes.get(i))) {
                return 0;
            }
        }
        return 1;
    }
}
