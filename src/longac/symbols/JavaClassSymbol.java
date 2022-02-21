package longac.symbols;

import longa.langtags.LgaChain;
import longa.langtags.LgaNode;
import tools.jvmx.MethodHelper;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

public class JavaClassSymbol extends TypeSymbol {
    public final Class<?> clazz;
    public final boolean isPrimitive;

    public static final JavaClassSymbol voidPrimitiveSymbol;
    public static final JavaClassSymbol voidSymbol;
    public static final JavaClassSymbol booleanPrimitiveSymbol;
    public static final JavaClassSymbol BooleanSymbol;
    public static final JavaClassSymbol intPrimitiveSymbol;
    public static final JavaClassSymbol IntegerSymbol;
    public static final  JavaClassSymbol listSymbol;
    //public static final JavaClassSymbol doublePrimitiveSymbol;
   // public static final JavaClassSymbol DoubleSymbol;

    public static final JavaClassSymbol StringSymbol;
    public static final JavaClassSymbol ObjectSymbol;

    public LgaChain lgaChain;
    private JavaClassSymbol( Class<?> clazz,boolean isPrimitive)
    {
        super(clazz.getSimpleName(),null);
        this.clazz=clazz;
        this.isPrimitive=isPrimitive;
        if(clazz.isAnnotationPresent(LgaChain.class))
        {
            lgaChain = clazz.getAnnotation(LgaChain.class);
        }
    }

    public static JavaClassSymbol forName(String classFullName)
    {
        try {
            Class<?> clazz = Class.forName(classFullName);
            JavaClassSymbol javaClassSymbol =JavaClassSymbol.create(clazz);
            return javaClassSymbol;
        }catch (ClassNotFoundException e)
        {

        }
        return null;
    }

    private static HashMap<Class<?>,JavaClassSymbol> clazzSymbolHashMap = new HashMap<>();
    public static JavaClassSymbol create(Class<?> clazz)
    {
        if(clazzSymbolHashMap.containsKey(clazz))
            return clazzSymbolHashMap.get(clazz);
        JavaClassSymbol javaClassSymbol = new JavaClassSymbol(clazz,false);
        clazzSymbolHashMap.put(clazz,javaClassSymbol);
        return javaClassSymbol;
    }

    static {
        voidPrimitiveSymbol = new JavaClassSymbol(void.class,true);
        putSymbol(voidPrimitiveSymbol);

        voidSymbol = new JavaClassSymbol(Void.class,false);
        putSymbol(voidSymbol);

        booleanPrimitiveSymbol = new JavaClassSymbol(boolean.class,true);
        putSymbol(booleanPrimitiveSymbol);

        BooleanSymbol = new JavaClassSymbol(boolean.class,true);
        putSymbol(BooleanSymbol);

        intPrimitiveSymbol = new JavaClassSymbol(int.class,true);
        putSymbol(intPrimitiveSymbol);

        IntegerSymbol = new JavaClassSymbol(Integer.class,false);
        putSymbol(IntegerSymbol);

        //doublePrimitiveSymbol = new JavaClassSymbol(double.class,true);
       // putSymbol(doublePrimitiveSymbol);

      //  DoubleSymbol = new JavaClassSymbol(Double.class,false);
      //  putSymbol(DoubleSymbol);

        StringSymbol  = new JavaClassSymbol(String.class,false);
        putSymbol(StringSymbol);

        ObjectSymbol  = new JavaClassSymbol(Object.class,false);
        putSymbol(ObjectSymbol);

         listSymbol = JavaClassSymbol.create(longa.lang.List.class);
    }

    private static void putSymbol(JavaClassSymbol sym)
    {
        clazzSymbolHashMap.put(sym.clazz,sym);
    }

    @Override
    public VarSymbol findField(String name)
    {
        try {
            Field field = clazz.getField(name);
            JavaVarSymbol varSymbol = new JavaVarSymbol(field,this);
            return varSymbol;
        }catch (NoSuchFieldException|SecurityException e)
        {
            return null;
        }
    }

    @Override
    public ArrayList<MethodSymbol> findConstructor(ArrayList<TypeSymbol> argTypes)
    {
        Constructor[] constructors = clazz.getConstructors();
        ArrayList<MethodSymbol> methodSyms = new ArrayList<>();
        for(Constructor constructor:constructors) {
            if(Modifier.isPublic(constructor.getModifiers())) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (MethodHelper.isAssignFrom(parameterTypes, argTypes) > 0) {
                    JavaMethodSymbol methodSymbol = new JavaMethodSymbol(constructor, this);
                    methodSyms.add(methodSymbol);
                }
            }
        }
        return methodSyms;
    }

    @Override
    public boolean isAssignableFrom(TypeSymbol typeSymbolSub)
    {
        if(typeSymbolSub instanceof JavaClassSymbol)
        {
            Class<?> sub = ((JavaClassSymbol) typeSymbolSub).clazz;
            return  ( this.clazz.isAssignableFrom(sub));
        }
        return false;
    }


    @Override
    public String getSignature(boolean addL)
    {
        if (int.class.equals(clazz)) {
            return "I";
        }
        else if (void.class.equals(clazz)) {
            return "V";
        }
        else if (boolean.class.equals(clazz)) {
            return "Z";
        }
        else if (char.class.equals(clazz)) {
            return "C";
        }
        else  if (byte.class.equals(clazz)) {
            return "B";
        }
        else if (short.class.equals(clazz)) {
            return "S";
        }
        else if (float.class.equals(clazz)) {
            return "F";
        }
        else  if (long.class.equals(clazz)) {
            return "J";
        }
        else  if (double.class.equals(clazz)) {
            return "D";
        }
        else
        {
            String nameFull = this.clazz.getName();
            String name2 = nameFull.replaceAll("\\.", "/");
            String lstr = addL?"L":"";
            String elstr = addL?";":"";
            return lstr + name2+elstr;
        }
    }

    @Override
    public ArrayList<Symbol> findMembers(String name,boolean isStatic)
    {
        ArrayList<Symbol> arrayList = new ArrayList<>();
        VarSymbol fieldSymbol =this.findField(name);
        if(fieldSymbol!=null && isStatic==fieldSymbol.isStatic())
            arrayList.add(fieldSymbol);
        Method[] methods = clazz.getMethods();
        for(Method method :methods)
        {
            String methodName = method.getName();
            if(methodName.equals(name)&&Modifier.isPublic(method.getModifiers()) && isStatic == Modifier.isStatic(method.getModifiers()))
            {
                JavaMethodSymbol methodSymbol = new JavaMethodSymbol(method,this);
                arrayList.add(methodSymbol);
            }
        }
        return arrayList;
    }

    public ArrayList<Symbol> findMembersLgaNode(String name,boolean isStatic)
    {
        ArrayList<Symbol> arrayList = new ArrayList<>();
        VarSymbol fieldSymbol =this.findField(name);
        if(fieldSymbol!=null && isStatic==fieldSymbol.isStatic())
        {
            JavaVarSymbol javaFieldSymbol = (JavaVarSymbol) fieldSymbol;
            if(javaFieldSymbol.field.isAnnotationPresent(LgaNode.class))
            {
                arrayList.add(fieldSymbol);
            }
        }

        Method[] methods = clazz.getMethods();
        for(Method method :methods)
        {
            String methodName = method.getName();
            boolean isStaticMethod = Modifier.isStatic(method.getModifiers());
            boolean isPublic = Modifier.isPublic(method.getModifiers());
            if( methodName.equals(name) && isStatic ==isStaticMethod && isPublic )
            {
                if(method.isAnnotationPresent(LgaNode.class))
                {
                    JavaMethodSymbol methodSymbol = new JavaMethodSymbol(method,this);
                    arrayList.add(methodSymbol);
                }
            }
        }
        return arrayList;
    }

    public ArrayList<JavaMethodSymbol> findMethods(String name,boolean isStatic)
    {
        ArrayList<JavaMethodSymbol> arrayList = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        for(Method method :methods)
        {
            if(method.getName().equals(name) && isStatic == Modifier.isStatic(method.getModifiers()))
            {
                JavaMethodSymbol methodSymbol = new JavaMethodSymbol(method,this);
                arrayList.add(methodSymbol);
            }
        }
        return arrayList;
    }

    @Override
    public boolean isInterface()
    {
        return this.clazz.isInterface();
    }

    @Override
    public String toString()
    {
        return "JavaClassSymbol:"+this.clazz.getName();
    }

    @Override
    public boolean equalType(TypeSymbol a)
    {
        if(this.equals(a)) return true;
        if(!(a instanceof  JavaClassSymbol))
            return false;
        return this.clazz.equals(((JavaClassSymbol)a).clazz);
    }

    @Override
    public int match(TypeSymbol another)
    {
        if(this.equals(another)) return 0;
        if(another instanceof JavaClassSymbol)
        {
            /*if(this.equals(JavaClassSymbol.doublePrimitiveSymbol))
            {
                if(another.equals(JavaClassSymbol.doublePrimitiveSymbol))
                {
                    return 0;
                }
                else if(another.equals(JavaClassSymbol.intPrimitiveSymbol))
                {
                    return 1;
                }
            }*/
            if(this.isInterface() || another.isInterface()) {
                JavaClassSymbol anotype = (JavaClassSymbol) another;
                if (this.clazz.isAssignableFrom(anotype.clazz))
                    return 2;
            }
            else
            {
                JavaClassSymbol anotherClass =(JavaClassSymbol) another;
                Class<?> aclazz =anotherClass.clazz;
               // Debuger.outln("275:"+this.clazz+" , "+aclazz);
               // if(clazz.equals(Object.class)&& aclazz.equals(String.class) )
              //      Debuger.outln("277:"+this.clazz+" , "+aclazz);
                int i= 0 ;
                while (aclazz!=null)
                {
                    if(this.clazz .equals(aclazz))
                    {
                        return i;
                    }
                    aclazz = aclazz.getSuperclass();
                    i++;
                }
                return -1;
            }
            return -1;
        }
        else if(another instanceof JavaArrayTypeSymbol)
        {
            if(this.isObject())
                return 1;
        }
        else
        {
            if(this.isObject())
                return 1;
        }
        return -1;
    }

    private boolean isObject()
    {
        return this.clazz.equals(Object.class);
    }
}
