package longac.symbols;

import longa.langtags.LgaNode;
import longac.utils.Debuger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class JavaMethodSymbol extends MethodSymbol {

    private Method method;
    private Constructor constructor;
    public ArrayList<JavaVarSymbol> varSymbols = new ArrayList<>();
    //protected int parameterCount;

    public JavaMethodSymbol(String name, Symbol owner)
    {
        super(name,owner);
    }

    public LgaNode lgaNode;
    public JavaMethodSymbol(Method method, TypeSymbol owner)
    {
        super(method.getName(),owner);
        this.method = method;
        this.returnTypeSymbol = JavaClassSymbol.create(method.getReturnType());
        Parameter[] parameters =  method.getParameters();
        putParameters(parameters);
        //if(method.getName().equals("__start"))//&&owner.name.equals("join_chain"))
        //    Debuger.outln("33 JavaMethodSymbol init:"+method.getName()+" "+owner.name );

        if(method.isAnnotationPresent(LgaNode.class))
        {
            lgaNode = (LgaNode) method.getAnnotation(LgaNode.class);
        }
    }

    public JavaMethodSymbol(Constructor constructor, TypeSymbol owner)
    {
        super("init",owner);
       // this.isStatic = Modifier.isStatic(constructor.getModifiers());
        this.constructor = constructor;
        this.returnTypeSymbol = JavaClassSymbol.create(constructor.getDeclaringClass());
        Parameter[] parameters =  constructor.getParameters();
        putParameters(parameters);
    }

    private void putParameters(Parameter[] parameters )
    {
        for(int i=0;i< parameters.length;i++)
        {
            Parameter parameter = parameters[i];
            //TypeSymbol typeSymbol = JavaClassSymbol.create(parameter.getType());
            JavaVarSymbol varSymbol = new JavaVarSymbol(parameter,this);
            this.varSymbols.add(varSymbol);
            //putVar(varSymbol);
        }
    }

    @Override
    public int getParameterCount()
    {
        if(isConstructor())
            return constructor.getParameterCount();
        else
            return method.getParameterCount();
    }

    //@Override
    public boolean isConstructor() {
        return constructor!=null;
    }

    @Override
    public VarSymbol getParameterSymbol(int i)
    {
        return varSymbols.get(i);
    }

    @Override
    public boolean isStatic()
    {
        return  Modifier.isStatic(method.getModifiers());
    }

    @Override
    public boolean isContructor()
    {
        return this.constructor!=null;
    }


    public String toString()
    {
        if(method!=null)
            return method.toString();
        if(constructor!=null)
            return constructor.toString();
        return  super.toString();
    }
}
