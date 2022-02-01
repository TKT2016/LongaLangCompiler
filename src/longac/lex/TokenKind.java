package longac.lex;
/** 词标记类型*/
public enum TokenKind
{
    EOF(), //结束符号
    ERROR(), //错误
    IDENTIFIER(), //标识符
    INTLITERAL(), //整数常量类型
    STRINGLITERAL(), //字符串常量类型
    STATIC("static"),
    BOOLEAN("boolean"),
    BREAK("break"),
    CLASS("class"),
    CONTINUE("continue"),
    ELSE("else"),
    FOR("for"),
    IF("if"),
    IMPORT("import"),
    INT("int"),
    NEW("new"),
    PACKAGE("package"),
    RETURN("return"),
    THIS("this"),
    VOID("void"),
    WHILE("while"),
    TRUE("true"),
    FALSE("false"),
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),
    LBRACKET("["),
    RBRACKET("]"),
    SEMI(";"),
    COMMA(","),
    DOT("."),
    EQ("="),
    GT(">"),
    LT("<"),
    NOT("!"),
    EQEQ("=="),
    LTEQ("<="),
    GTEQ(">="),
    NOTEQ("!="),
    AND("&&"),
    OR("||"),
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    COLON(":"),
    AT("@")
    ;

    public final String name; //类型对应的名称
    TokenKind() {
        this(null);
    }
    TokenKind(String name) {
        this.name = name;
    }

    /** 获取对应的Class结构(语义分析阶段使用) */
    public Class<?> getClazz( )
    {
        switch (this) {
            case VOID:
                return void.class;
            case INT:
                return int.class;
            case BOOLEAN:
                return boolean.class;
            case STRINGLITERAL:
                return String.class;
            case INTLITERAL:
                return int.class;
            default:
                return null;
        }
    }

    public String toString() {
        switch (this) {
            case IDENTIFIER:
                return "token.identifier";
            case STRINGLITERAL:
                return "token.string";
            case INTLITERAL:
                return "token.integer";
            case ERROR:
                return "token.error";
            case EOF:
                return "token.end-of-input";
            case DOT: case COMMA: case SEMI: case LPAREN: case RPAREN:
            case LBRACKET: case RBRACKET: case LBRACE: case RBRACE:
                return "'" + name + "'";
            default:
                return name;
        }
    }
}
