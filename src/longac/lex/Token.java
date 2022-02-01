package longac.lex;

public class Token {
    /** 类型 */
    public final TokenKind kind;

    /** 开始位置 */
    public final int pos;

    /** 行号 */
    public final int line;

    /** 结束位置 */
    public final int endPos;

    /* 标识符名称(如果kind是IDENTIFIER类型) */
    public String identName;

    /* 常量(如果kind是STRINGLITERAL或INTLITERAL) */
    public String valueString;

    /* 特殊Token,表示文件扫描结束 */
    public static final Token EOF = new Token(TokenKind.EOF, 0, 0,0);

    private Token(TokenKind kind, int line,int pos, int endPos) {
        this.kind = kind;
        this.pos = pos;
        this.endPos = endPos;
        this.line=line;
    }

    public static Token createLiteral(TokenKind kind,int line, int pos, int endPos, String stringVal) {
        Token token = new Token(kind, line,pos, endPos);
        token.valueString = stringVal;
        return token;
    }

    public static Token createNamed(TokenKind kind,int line, int pos, int endPos, String name) {
        Token token = new Token(kind, line,pos, endPos);
        token.identName = name;
        return token;
    }

    public static Token createNormal(TokenKind kind,int line, int pos, int endPos) {
        Token token = new Token(kind,  line,pos, endPos);
        token.identName = TokenKindsMap.lookupName(kind);
        return token;
    }

    @Override
    public String toString()
    {
        String str = getTextShow();
        return str+" 第"+line+"行"+" "+ pos;
    }

    private String getTextShow()
    {
        if(kind== TokenKind.STRINGLITERAL)
            return '"'+ valueString +'"';
        if(kind== TokenKind.INTLITERAL)
            return valueString ;
        if(kind== TokenKind.IDENTIFIER)
            return identName;
        if(kind== TokenKind.ERROR)
            return "(token.error)";
        if(kind== TokenKind.EOF)
            return "(token.eof)";
        if(identName !=null)
            return kind.name;
        return  " ";
    }
}
