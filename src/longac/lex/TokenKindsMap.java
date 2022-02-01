package longac.lex;
import java.util.HashMap;

/** TokenKind键值对应表 */
public class TokenKindsMap {
    /* 名称-类型表 */
    private static final HashMap<String, TokenKind> nameKindMap;
    /* 类型-名称表 */
    private static final HashMap<TokenKind, String> kindNameMap;

    static {
        nameKindMap = new HashMap<>();
        kindNameMap = new HashMap<>();
        /* 把TokenKind所有枚举值放入表中 */
        for (TokenKind t : TokenKind.values())
        {
            if (t.name != null)
                enterKeyword(t.name, t);
        }
    }

    private static void enterKeyword(String s, TokenKind tk) {
        if (!nameKindMap.containsKey(s))
            nameKindMap.put(s, tk);
        if (!kindNameMap.containsKey(tk))
            kindNameMap.put(tk, s);
    }

    /** 根据名称查类型 */
    public static TokenKind lookupKind(String name) {
        if (nameKindMap.containsKey(name))
            return nameKindMap.get(name);
        return TokenKind.IDENTIFIER; //如果找不到，就默认为标识符
    }

    /** 根据类型查名称 */
    public static String lookupName(TokenKind tokenKind) {
        if (kindNameMap.containsKey(tokenKind))
            return kindNameMap.get(tokenKind);
        return null;
    }
}
