package longac.symbols;

public abstract class VarSymbol extends Symbol
{
    public TypeSymbol typeSymbol;

    @Override
    public TypeSymbol getTypeSymbol() {
        return typeSymbol;
    }

    public VarKind varSymbolKind;
    public VarSymbol( String name,Symbol owner,VarKind kind,TypeSymbol typeSymbol ) {
        super(name,owner);
        this.typeSymbol=typeSymbol;
        this.varSymbolKind = kind;
    }

    public String toString() {
        return name.toString();
    }

    public abstract boolean isStatic();

    public abstract boolean isField();

   // public JCTree lastAssignTree = null;

    //public Object assignedConstValue = null;
    public abstract boolean canWrite();

}
