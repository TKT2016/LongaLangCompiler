package longac.lex;

import longac.diagnostics.SourceLog;

/* 词法分析器 */
public class Tokenizer {
    /* 要编译的源码字符数组 */
    char[] buf;
    /* 当前字符位置 */
    int pos;
    /* 当前字符 */
    char ch;
    /* 当前行号 */
    int line;
    final SourceLog log;
    /* 源码长度 */
    final int maxLength;

    /* 表示扫描结束字符 */
    public final static byte EOI = 0x1A;
    /* 出错的位置 */
    public int errPos = SourceLog.NOPOS;

    public Tokenizer(SourceLog log, String code) {
        line=1;
        this.log = log;
        buf = code.toCharArray();
        maxLength = buf.length;

        pos = -1;
        scanChar();//读入第一个字符
    }

    /** 读取token */
    public Token readToken() {
        //Debuger.outln(ch+" , "+line+","+pos);
        switch (ch) {
            case '(': case ')': case '[': case ']': case '{': case '}':
            case '+': case '-': case '*':
            case ',':   case ';': case '.':
                case ':': case '@':
                return scanOneChar();
            case '&':
                return scanAND();
            case '|':
                return scanOR();
            case '>': case '<': case '!': case '=':
                return scanCompare();
            case '\"':
                return scanString();
            case '/': {
                Token divToken = scanCommentOrDiv();
                if (divToken != null)
                    return divToken;
                else
                    return readToken();
            }
            case ' ': case '\t':
                skipSpace();
                return readToken();
            case '\n': case '\r':
                skipLine();
                return readToken();
            case EOI://这里表示字符已经到了末尾
                return Token.EOF;
            default:
                if (ch>='0' && ch<='9') {
                    return scanNumber();
                }
                else if (isIdentChar(ch)) {
                    return scanIdentOrKeyword();
                }
                else {
                    String arg = (32 < ch && ch < 127) ?
                            String.format("%s", ch) :
                            String.format("\\u%04x", (int) ch);
                    lexError(pos,line, "非法字符:'" + arg + "'");
                    /* 报错后进行错误处理，略过这个非法字符，扫描下一个Token */
                    scanChar();
                    return readToken();
                }
        }
    }

    /* 跳过空白 */
    void skipSpace()
    {
        while (ch == ' ' || ch == '\t')
            scanChar();
    }

    /* 跳过换行并记录行号 */
    void skipLine()
    {
        while (notEnd())
        {
            if(ch=='\r')
            {
                line++; //行号加1
                scanChar();
                if(ch=='\n')
                    scanChar();
            }
            else if(ch=='\n')
            {
                line++; //行号加1
                scanChar();
            }
            else
            {
                break;
            }
        }
    }

    /* 扫描整数 */
    Token scanNumber() {
        int startPos = pos;
        StringBuilder buf = new StringBuilder();
        while (ch>='0' && ch<='9')
        {
            buf.append(ch);
            scanChar();
        }
        int endPos = pos;
        return Token.createLiteral(TokenKind.INTLITERAL,line,startPos,endPos,buf.toString());
    }

    /* 扫描字符串常量 */
    Token scanString() {
        int startPos = pos;// 保存开始位置
        StringBuilder buf = new StringBuilder();//存储字符串内容
        scanChar(); //跳过双引号，读取下一个字符
        while (notEnd() )
        {
            if(ch=='\"')
                break;
            else if(isLineChar(ch))//字符串常量不包含换行符,遇到换行符表示结束
                break;
             buf.append(ch);//保存字符
             scanChar();//读取下一个字符
        }
        /* 验证结束字符是否是引号 */
        if(ch=='\"')
            scanChar();
        else
            lexError(pos, line,"字符串缺少末尾引号");
        int endPos = pos;
        String stringVal = buf.toString();
        return Token.createLiteral(TokenKind.STRINGLITERAL,line,startPos,endPos,stringVal);
    }

    /*扫描标识符或关键字*/
    Token scanIdentOrKeyword() {
        int startPos = pos;
        StringBuilder buf = new StringBuilder();
        while (notEnd())
        {
            if(isIdentChar(ch)||(ch>='0' && ch<='9'))
            {
                buf.append(ch);
                scanChar();
            }
            else
                break;
        }
        int endPos = pos;
        String name = buf.toString();//标识符名称
        /* 根据名称查找TokeKind */
        TokenKind tk = TokenKindsMap.lookupKind(name);
        if(tk== TokenKind.IDENTIFIER)
            return Token.createNamed(tk,line,startPos,endPos,name);
        else
            return Token.createNormal(tk,line,startPos,endPos);
    }

    /* 是否是标识符字符 */
    boolean isIdentChar(char ch)
    {
        return  (ch>='A' && ch<='Z') || (ch>='a' && ch<='z') || ch=='_'|| ch=='$';
    }

    /* 扫描注释或除号 */
    Token scanCommentOrDiv()
    {
        int startPos = pos; //保存除号的位置
        scanChar();//读取下一个字符
        if (ch == '/')
            skipLineComment(); //单行注释
        else if (ch == '*')
            skipMultiLineComment(); //多行注释
        else {
            /* 当前字符不是'/'或'*',则是除号 */
            return Token.createNormal(TokenKind.DIV,line, startPos, startPos);
        }
        return null;
    }

    /* 扫描单行注释 */
    void skipLineComment()
    {
        while (notEnd())
        {
            if(isLineChar(ch))
                break;
            scanChar();
        }
        if(isLineChar(ch))
            skipLine();
    }

    /* 扫描多行注释 */
    void skipMultiLineComment()
    {
        scanChar();
        boolean isCommentEnd=false;
        while (notEnd())
        {
            if (ch == '*')
            {
                isCommentEnd = scanMultiLineCommentClosed();
                if(isCommentEnd)
                    break;
            }
            else if(isLineChar(ch))//注意处理换行
                skipLine();
            else
                scanChar();
        }
        if(!isCommentEnd) //检测结束符是否完整
        {
            lexError(pos,line, "注释没有结束符");
        }
    }

    /* 判断是否是多行注释的结束符号*/
    boolean scanMultiLineCommentClosed()
    {
        scanChar();//略过当前'*'号
        if(ch=='/')
        {
            scanChar();
            return true;
        }
        else
        {
            return false;
        }
    }

    /* 判断是否是换行符 */
    static boolean isLineChar(char c)
    {
        return (c=='\n'|| c=='\r');
    }

    /* 扫描 &&  */
    Token scanAND()
    {
        int startPos = pos; //记录开始位置
        int endPos = pos; // 结束位置变量endPos, 暂时保存当前位置到endPos

        scanChar();//移到下一个字符
        /* 验证'&'后面是否是'&' */
        if(ch=='&')
        {
            scanChar(); // 正确,下移一个
            endPos= pos; //正确情况,保存当前位置到endPos
        }
        else
        {
            /* 错误情况，报错 */
            lexError(startPos,line,"与运算符缺少一个'&'");
        }
        Token token =  Token.createNormal(TokenKind.AND,line,startPos,endPos);
        return token;
    }

    /* 扫描 ||  */
    Token scanOR()
    {
        int startPos = pos;
        int endPos = pos;
        scanChar();
        if(ch=='|')
        {
            scanChar();
            endPos= pos;
        }
        else
        {
            lexError(startPos,line,"或运算符缺少一个'|'");
        }
        Token token =  Token.createNormal(TokenKind.OR,line,startPos,endPos);
        return token;
    }

    /* 扫描比较运算符 */
    Token scanCompare() {
        /* 保存位置信息 */
        int startPos = pos;
        int endPos = pos;

        /* 第二个字符不是'='时的Token类型 */
        TokenKind tk1 = TokenKind.ERROR;
        /* 第二个字符是'='时的Token类型 */
        TokenKind tk2 = TokenKind.ERROR;
        /* 根据当前字符初始化tk1,tk2 */
        switch (ch) {
            case '>':
                tk1 = TokenKind.GT;
                tk2 = TokenKind.GTEQ;
                break;
            case '<':
                tk1 = TokenKind.LT;
                tk2 = TokenKind.LTEQ;
                break;
            case '=':
                tk1 = TokenKind.EQ;
                tk2 = TokenKind.EQEQ;
                break;
            case '!':
                tk1 = TokenKind.NOT;
                tk2 = TokenKind.NOTEQ;
                break;
        }
        scanChar();//已经处理完第一个字符，下移一个
        /* 根据第二个字符判断Token类型 */
        if(ch=='=')
        {
            scanChar();
            endPos = pos;
            return Token.createNormal(tk2,line,startPos,endPos);
        }
        else
        {
            return Token.createNormal(tk1,line,startPos,endPos);
        }
    }

    public boolean notEnd()
    {
        return (pos < maxLength);
    }

    /** 读取下一个字符 */
    public void scanChar() {
        /* 超出最大长度会导致异常,需要检测 */
        if (pos < maxLength -1) {
            pos++;
            ch = buf[pos];
        }
        else
        {
            ch= EOI; //超过最大长度，返回一个特殊的字符
        }
    }

    /* 扫描一个字符的Token */
    Token scanOneChar( )
    {
        String name = String.valueOf(ch);
        TokenKind kind = TokenKindsMap.lookupKind(name);
        return scanOneChar(kind);
    }

    Token scanOneChar(TokenKind tk)
    {
        Token token = Token.createNormal(tk,line, pos, pos);
        scanChar();//当前字符处理完成，移到下一位置
        return token;
    }

    /* 报告错误 */
    void lexError(int pos,int line, String msg) {
        log.error(pos,line, msg);
        errPos = pos;
    }
}
