package longac.symbols;
import tools.asmx.NameUtil;
import tools.jvmx.MethodHelper;
import tools.jvmx.NamesTexts;
import longac.attrs.FindKinds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeclClassSymbol extends TypeSymbol implements SymbolFrame
{
    public SourceFileSymbol sourceFileSymbol;
    public PackageSymbol packageSymbol;
    protected Map<String,VarSymbol> fields = new HashMap<>();
    protected Map<String,DeclMethodSymbol> methods = new HashMap<>();
    protected MethodSymbol DefaultConstructorSymbol = new DeclMethodSymbol(NamesTexts.init,this,this);

    public DeclVarSymbol thisFieldSymbol = new DeclVarSymbol("this",this,VarKind._this, this);


    public DeclClassSymbol(String name,  SourceFileSymbol sourceFileSymbol) {
        super(name,sourceFileSymbol.packageSymbol);
        this.sourceFileSymbol = sourceFileSymbol;
    }

    public String getFullname()
    {
        if(this.packageSymbol==null)
            return name;
        String packageName = packageSymbol.getFullname();
        if(packageName == null)
            return name;
        if(packageName.trim().equals(""))
            return name;
        return packageName+"."+name;
    }

    public void addVar(DeclVarSymbol varSymbol)
    {
        fields.put(varSymbol.name,varSymbol);
    }

    public int constructorCount = 0;

    public boolean addMethod(DeclMethodSymbol methodSymbol)
    {
        String sign =methodSymbol.name+methodSymbol.getSignature(true);
        if(methods.containsKey(sign))
            return false;
        methods.put(sign,methodSymbol);
        if(methodSymbol.isContructor())
            constructorCount++;
        return true;
    }

    @Override
    public boolean isAssignableFrom(TypeSymbol typeSymbolSub)
    {
        if(this.equals(typeSymbolSub))return true;
        return false;
    }

    @Override
    public VarSymbol findField(String name)
    {
        if(fields.containsKey(name))
            return fields.get(name);
        return null;
    }

    @Override
    public String getSignature(boolean addL)
    {
        String lstr = addL?"L":"";
        String fullName = getFullname();
        String  str = fullName.replace(".", "/");
        String elstr = addL?";":"";
        return lstr +str+elstr;
    }

    @Override
    public ArrayList<MethodSymbol> findConstructor(ArrayList<TypeSymbol> argTypes) {
        ArrayList<MethodSymbol> list = new ArrayList<>();
        if (constructorCount == 0) {
            if (argTypes.size() == 0)
                list.add(DefaultConstructorSymbol);
        }
        else {
            for (String key : methods.keySet()) {
                DeclMethodSymbol methodSymbol = methods.get(key);
                if(methodSymbol.isContructor()) {
                    ArrayList<TypeSymbol> parameterTypes = methodSymbol.getParameterTypes();
                    if (MethodHelper.isAssignFrom(parameterTypes, argTypes) > 0) {
                        list.add(methodSymbol);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public ArrayList<Symbol> findMembers(String name,boolean isStatic)
    {
        ArrayList<Symbol> symbols = new ArrayList<>();
        if(!isStatic) {
            VarSymbol fieldSymbol = findField(name);
            if (fieldSymbol != null)
                symbols.add(fieldSymbol);

            for (String sign : methods.keySet()) {
                MethodSymbol methodSymbol = methods.get(sign);
                if (methodSymbol.name.equals(name))
                    symbols.add(methodSymbol);
            }
        }
        return symbols;
    }

    @Override
    public boolean equalType(TypeSymbol a)
    {
        if(this.equals(a)) return true;
        if(!(a instanceof  DeclClassSymbol))
            return false;
        return this.getFullname().equals(((DeclClassSymbol)a).getFullname());
    }

    @Override
    public int match(TypeSymbol another)
    {
        if(this.equals(another)) return 0;
        if(another instanceof DeclClassSymbol)
        {
            if(this.equals(another))
                return 0;
            else
                return -1;
        }
        else if(another instanceof JavaClassSymbol)
        {
            if(another.equals(JavaClassSymbol.ObjectSymbol))
                return 0;
            else
                return -1;
        }
        else
        {
            return -1;
        }
    }

    public SymbolFrame getParent()
    {
        return null;// this.sourceFileSymbol;
    }

    public boolean containsField(String varName) {
        return (fields.containsKey(varName));
    }

    public boolean containsVar(String varName, FindKinds findKinds)
    {
        if(findKinds.isFindVar) {
            if (fields.containsKey(varName)) {
                return true;
            }
            ArrayList<VarSymbol> symbols = this.sourceFileSymbol.findVars(varName);
            return symbols.size()>0;
        }
        if(findKinds.isFindMethod) {
            for(String sign : methods.keySet())
            {
                MethodSymbol methodSymbol = methods.get(sign);
                if(methodSymbol.name.equals(name))
                   return true;
            }
        }
        return false;
    }

    public ArrayList<Symbol> findVars(String varName, FindKinds findKinds)
    {
        ArrayList<Symbol> list = new ArrayList<>();
        if(findKinds.isFindVar) {
            if (fields.containsKey(varName)) {
                list.add(fields.get(varName));
            }
            ArrayList<VarSymbol> symbols = this.sourceFileSymbol.findVars(varName);
            list.addAll(symbols);
        }
        if(findKinds.isFindMethod) {
            for(String sign : methods.keySet())
            {
                MethodSymbol methodSymbol = methods.get(sign);
                if(methodSymbol.name.equals(varName))
                {
                    list.add(methodSymbol);
                }
            }
        }
        if(findKinds.isFindType)
        {
            if(this.name.equals(varName))
                list.add(this);

            ArrayList<TypeSymbol> typeSymbols = sourceFileSymbol.findTypes(new TypeNameModel(varName));
            list.addAll(typeSymbols);
        }
        return list;
    }

    public String compiledClassFile;

}
