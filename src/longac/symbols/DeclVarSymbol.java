package longac.symbols;

import longac.utils.Debuger;
import org.objectweb.asm.Label;

public class DeclVarSymbol extends VarSymbol{
    private int adr = -1;

    public DeclVarSymbol( String name,Symbol owner,VarKind kind,TypeSymbol typeSymbol ) {
        super(name,owner,kind,typeSymbol);
        this.typeSymbol=typeSymbol;
        this.varSymbolKind = kind;
       // this.isStatic =false;
    }

    public boolean isLocalVar()
    {
        return this.varSymbolKind== VarKind.localVar;
    }

    @Override
    public boolean isStatic()
    {
        return  false;
    }

    @Override
    public boolean isField()
    {
        return  this.varSymbolKind.equals( VarKind.field);
    }

    public Label startLabel;
    public Label endLabel;

    public void setAdr(int adr)
    {
        this.adr =adr;
        //Debuger.outln("41 DeclVarSymbol adr:"+this.name+" "+this.adr+" "+" ");
    }

    public int getAdr()
    {
        return adr;
    }

    public boolean hasGot =false;

    @Override
    public boolean canWrite()
    {
        return true;
    }
}
