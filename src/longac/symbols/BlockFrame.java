package longac.symbols;

import tools.collectx.ListMap;
import longac.attrs.FindKinds;

import java.util.ArrayList;

public class BlockFrame implements SymbolFrame
{
    public ArrayList<BlockFrame> children = new ArrayList<>();
    public ListMap<DeclVarSymbol> localVars = new ListMap<>();
    //public ListMap<DeclVarSymbol> chainVars = new ListMap<>();
    public BlockFrame( SymbolFrame parent )
    {
        this.parent = parent;
    }

    protected SymbolFrame parent;
    public SymbolFrame getParent()
    {
        return parent;
    }

    public boolean containsVar(String varName, FindKinds findKinds)
    {
        if(findKinds.isFindVar) {
            if (localVars.contains(varName)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Symbol> findVars(String varName, FindKinds findKinds)
    {
        ArrayList<Symbol> list = new ArrayList<>();
        if(findKinds.isFindVar) {
            if (localVars.contains(varName)) {
                list.add(localVars.get(varName));
            }
        }
        return list;
    }

    public void addVar(DeclVarSymbol varSymbol)
    {
        if(localVars.contains(varSymbol.name))
            return;
        localVars.put(varSymbol.name,varSymbol);
        DeclMethodSymbol declMethodSymbol = SymbolFrameUtil.getDeclMethodSymbol(this);
        declMethodSymbol.addVar(varSymbol);
    }

    public int getLocalVarCount()
    {
        return localVars.size();
    }

    public BlockFrame createChild( )
    {
        BlockFrame blockSymbolFrame = new BlockFrame(this);
        //blockSymbolFrame.parent = this;
        this.children.add(blockSymbolFrame);
        return blockSymbolFrame;
    }
}
