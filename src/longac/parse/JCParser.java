package longac.parse;

import longac.diagnostics.SourceLog;
import longac.lex.Token;
import longac.lex.TokenKind;
import longac.lex.Tokenizer;
import longac.utils.CompileContext;
import longac.trees.*;

import java.util.HashMap;

import static longac.lex.TokenKind.IDENTIFIER;
import static longac.lex.TokenKind.SEMI;

/** 语法分析器 */
public class JCParser {
    SourceLog log;
    Tokenizer tokenizer;
    Token token;
    TokenKind tkind; //当前词法标记
    TreeMaker maker;
    CompileContext context;

    FileTreeParser fileTreeParser;
    MethodTreeParser methodTreeParser;
    StmtTreeParser stmtTreeParser;
    ExprTreeParser exprParser;
    TermParser termParser;

    HashMap<String,JCImport> importedChainMap = new HashMap<>();

    public JCParser(CompileContext context, Tokenizer tokenizer, SourceLog log) {
        this.context = context;
        this.tokenizer = tokenizer;
        this.log = log;
        this.maker = new TreeMaker(log);
        termParser = new TermParser(this);
        exprParser = new ExprTreeParser(this);
        fileTreeParser = new FileTreeParser(this);
        methodTreeParser = new MethodTreeParser(this);
        stmtTreeParser = new StmtTreeParser(this);

        nextToken(); //读取第一个Token
    }

    void nextToken() {
        token = tokenizer.readToken();
        tkind = token.kind;
    }

    boolean isChainIdent()
    {
        if(this.tkind== IDENTIFIER)
        {
            String identName = this.token.identName;
            if(this.importedChainMap.containsKey(identName))
            {
                return true;
            }
        }
        return false;
    }

    public JCFileTree parseCompilationUnit() {
        return fileTreeParser.parseCompilationUnit();
    }

    void accept(TokenKind tk, boolean moveNext) {
        boolean b = expectKind(tk);
        if(b)
            nextToken();
        else
            if(moveNext)
                nextToken();
    }

    void accept(TokenKind tk) {
        boolean b = expectKind(tk);
        if(b)
            nextToken();
    }
/*
    void acceptTo(TokenKind tk)
    {
        expectKind(tk);
        while (token.kind!=tk)
        {
            if(token.kind==TokenKind.EOF)
                throw new ParseEOFError();

            nextToken();
        }
        Token temp = token;
    }*/

    void skipTo(TokenKind tk)
    {
        while (token.kind!=tk)
        {
            if(token.kind==TokenKind.EOF)
                throw new ParseEOFError();
            nextToken();
        }
    }

    boolean expectKind(TokenKind tk)
    {
        boolean same = (token.kind == tk);
        if (!same) {
            String msg;
            if (tk.name != null)
                msg = "需要 '" + tk.name + "' ";
            else if (tk == TokenKind.IDENTIFIER)
                msg ="需要<标识符>";
            else
                msg ="需要<" + tk + ">";
            log.error(token.pos, token.line,msg);
        }
        return same;
    }

    void skipSEMI() {
        while (token.kind == SEMI) {
            nextToken();
        }
    }

    void error(Token posToken,String msg)
    {
        this.log.error(posToken.pos,posToken.line,msg);
    }

}
