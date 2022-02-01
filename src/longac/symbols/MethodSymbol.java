package longac.symbols;

import java.util.ArrayList;

public abstract class MethodSymbol  extends Symbol
{
    public MethodSymbol(String name,Symbol owner)
    {
        super(name,owner);
    }

    public TypeSymbol returnTypeSymbol;

    public abstract int getParameterCount();
    public abstract VarSymbol getParameterSymbol(int i);

    @Override
    public TypeSymbol getTypeSymbol() {
        return returnTypeSymbol;
    }

    @Override
    public String getSignature(boolean addL)
    {
        final StringBuffer buf = new StringBuffer();
        buf.append("(");
        for (int i = 0; i < getParameterCount(); ++i) {
            Symbol symboli = getParameterSymbol(i);
            TypeSymbol typeSymboli = symboli.getTypeSymbol();
            if(typeSymboli!=null) {
                String singature = typeSymboli.getSignature(true);
                buf.append(singature);
            }
        }
        buf.append(")");
        if(isContructor())
            buf.append("V");
        else
            buf.append(returnTypeSymbol.getSignature(addL));
        return buf.toString();
    }

    public int matchArgTypes(ArrayList<TypeSymbol> argTypes)
    {
        if(argTypes.size()!=this.getParameterCount())
           return -1;
        if(this.getParameterCount()==0)
            return 0;
        int sum=0;
        for(int i = 0; i< getParameterCount(); i++)
        {
            TypeSymbol argtypeSymbol = argTypes.get(i);
            TypeSymbol paramSymbol = getParameterSymbol(i).getTypeSymbol();
            int k = paramSymbol.match(argtypeSymbol);
            if(k<0)
                return -1;
            else
                sum+=k;
        }
        return sum;

    }

    public abstract boolean isContructor();

    public TypeSymbol getParameterType(int i)
    {
        return this.getParameterSymbol(i).getTypeSymbol();
    }

    public abstract boolean isStatic();
}
