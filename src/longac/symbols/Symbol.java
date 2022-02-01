package longac.symbols;

public abstract class Symbol {

    public  abstract TypeSymbol getTypeSymbol();

    public String name;

    public Symbol owner;

    public Symbol(String name,Symbol owner)
    {
        this.name=name;
        this.owner=owner;
    }

    public void setOwner(Symbol owner)
    {
        this.owner=owner;
    }

    public Symbol getOwner( )
    {
        return this.owner;
    }

    public void setName(String name)
    {
        this.name=name;
    }

    public boolean isLocal() {
        return (owner instanceof VarSymbol || owner instanceof JavaMethodSymbol) ||
                (owner instanceof TypeSymbol && owner.isLocal());
    }

    public PackageSymbol packge() {
        Symbol sym = this;
        while (!(sym instanceof PackageSymbol)) {
            sym = sym.owner;
            if(sym==null)
                break;
        }
        return (PackageSymbol) sym;
    }

/*
    public SourceFileSymbol getCompilationFileSymbol() {
        if( this instanceof SourceFileSymbol) return (SourceFileSymbol)this;
        DeclClassSymbol declClassSymbol = getDeclClassSymbol();
        return declClassSymbol.sourceFileSymbol;
    }*/

    public String getSignature(boolean addL)
    {
        return this.getTypeSymbol().getSignature(addL);
    }
}
