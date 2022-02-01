package longac.parse;

import longac.diagnostics.SourceLog;
import longac.lex.Token;

public abstract class ParserBase {
    protected final JCParser jcparser;
    protected final SourceLog log;
    protected final TreeMaker maker;
    public ParserBase(JCParser jcparser)
    {
        this.jcparser = jcparser;
        this.log = jcparser.log;
        this.maker = jcparser.maker;
    }

    protected void error(Token posToken, String msg)
    {
        jcparser.error(posToken,msg);
    }
}
