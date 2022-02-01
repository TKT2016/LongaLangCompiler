package longac.symbols;

import longac.attrs.FindKinds;

import java.util.ArrayList;

public class SymbolFrameUtil {
    public static DeclMethodSymbol getDeclMethodSymbol(SymbolFrame frame)
    {
        SymbolFrame temp = frame;
        while (temp!=null)
        {
            if(temp instanceof DeclMethodSymbol)
                return (DeclMethodSymbol) temp;
            temp =temp.getParent();
        }
        return null;
    }

    public static DeclClassSymbol getDeclClassSymbol(SymbolFrame frame)
    {
        SymbolFrame temp = frame;
        while (temp!=null)
        {
            if(temp instanceof DeclClassSymbol)
                return (DeclClassSymbol) temp;
            temp =temp.getParent();
        }
        return null;
    }

    public static ArrayList<TypeSymbol> findTypes(SymbolFrame frame, TypeNameModel typeNameModel)
    {
        DeclClassSymbol declClassSymbol = getDeclClassSymbol(frame);
        if(declClassSymbol ==null) return new ArrayList<>();
        SourceFileSymbol sourceFileSymbol =declClassSymbol.sourceFileSymbol;
        return sourceFileSymbol.findTypes(typeNameModel);
    }

    public static boolean containsVar(SymbolFrame frame, String varName, FindKinds findKinds)
    {
        if(findKinds==null) findKinds = new FindKinds();
        SymbolFrame temp = frame;
        while (temp!=null)
        {
            if(temp.containsVar(varName,findKinds))
                return true;
            temp = temp.getParent();
        }
        return false;
    }

    public static ArrayList<Symbol> findVars(SymbolFrame frame, String varName, FindKinds findKinds)
    {
        if(findKinds==null) findKinds = new FindKinds();
        ArrayList<Symbol> list = new ArrayList<>();
        SymbolFrame temp = frame;
        while (temp!=null)
        {
            ArrayList<Symbol> finds = temp.findVars(varName,findKinds);
            list.addAll(finds);
            temp = temp.getParent();
        }
        return list;
    }
}
