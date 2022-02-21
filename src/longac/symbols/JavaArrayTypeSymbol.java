package longac.symbols;

import java.util.ArrayList;

public class JavaArrayTypeSymbol extends TypeSymbol {
    public final TypeSymbol typeSymbol;

    public JavaArrayTypeSymbol(TypeSymbol typeSymbol)
    {
        super(typeSymbol.name,null);
        this.typeSymbol =typeSymbol;
    }

    DeclVarSymbol lengthFieldSymbol = new DeclVarSymbol("length",this, VarKind.field, JavaClassSymbol.create(int.class));

    @Override
    public VarSymbol findField(String name)
    {
        if(name.equals(lengthFieldSymbol.name))
            return lengthFieldSymbol;
        return null;
    }
/*
    @Override
    public ArrayList<MethodSymbol> findMethod(String name, ArrayList<TypeSymbol> argTypes)
    {
        ArrayList<MethodSymbol> methods= new ArrayList<>();
        return methods;
    }*/

    @Override
    public ArrayList<MethodSymbol> findConstructor(ArrayList<TypeSymbol> argTypes)
    {
        ArrayList<MethodSymbol> methods= new ArrayList<>();
        return methods;
    }

    @Override
    public  boolean isAssignableFrom(TypeSymbol typeSymbolSub)
    {
        if(this.equals(typeSymbolSub))return true;
        if(typeSymbolSub instanceof JavaArrayTypeSymbol)
        {
            TypeSymbol sub = ((JavaArrayTypeSymbol) typeSymbolSub).typeSymbol;
            boolean b= this.typeSymbol.isAssignableFrom(sub);
            return b;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String getSignature(boolean addL)
    {
        return "["+typeSymbol.getSignature(addL);
    }

    @Override
    public ArrayList<Symbol> findMembers(String name,boolean isStatic)
    {
        ArrayList<Symbol> arrayList = new ArrayList<>();
        if(!isStatic) {
            VarSymbol symbol = this.findField(name);
            if (symbol != null)
                arrayList.add(symbol);
        }
        return arrayList;
    }

    public boolean isInterface()
    {
        return false;
    }

    @Override
    public boolean equalType(TypeSymbol a)
    {
        if(this.equals(a)) return true;
        if(!(a instanceof JavaArrayTypeSymbol))
            return false;
        return this.typeSymbol.equals(((JavaArrayTypeSymbol)a).typeSymbol);
    }

    @Override
    public String toString()
    {
        return "ArrayTypeSymbol:"+this.typeSymbol.name;
    }

    @Override
    public int match(TypeSymbol another)
    {
        if(this.equals(another)) return 0;
        if(!(another instanceof JavaArrayTypeSymbol)) return -1;
        JavaArrayTypeSymbol anotype = (JavaArrayTypeSymbol) another;
        if(this.typeSymbol.equals(anotype.typeSymbol))
            return 0;
        return -1;
    }

    @Override
    public TypeSymbol getElementType()
    {
        return typeSymbol;
    }
/*
    @Override
    public boolean isPrimitive()
    {
        return false;
    }*/
}
