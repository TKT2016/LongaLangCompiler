package longac.trees;

import java.io.IOException;
import java.io.StringWriter;

import longac.diagnostics.SourceLog;
import longac.symbols.TypeSymbol;
import longac.symbols.Symbol;
import longac.visitors.*;

/** 抽象语法树父类 */
public abstract class JCTree implements Cloneable
{
    protected  static int treeIndex=0;
    public int pos=-1;
    public int endpos=-1;
    public int line;
    public final int treeIndexCurrent;
    public SourceLog log;

    public JCTree( )
    {
        treeIndexCurrent= treeIndex;
        treeIndex++;
    }

    public void error(String msg)
    {
        log.error(pos,line,msg);
    }

    /* 需要转换目标的符号 */
    public TypeSymbol requireConvertTo;

    protected Symbol symbol;

    public Symbol getSymbol()
    {
        return symbol;
    }
    public TypeSymbol getTypeSymbol()
    {
        return symbol.getTypeSymbol();
    }

    public void setSymbol(Symbol symbol)
    {
        this.symbol = symbol;
    }

    /** 用 TreePretty生成格式化代码字符串 */
    @Override
    public String toString() {
        StringWriter s = new StringWriter();
        try {
            new TreePretty(s, false).printTree(this);
        }
        catch (IOException e) {
            throw new AssertionError(e);
        }
        return s.toString();
    }

    /** TreeScanner分析  */
    public abstract <D> void scan(TreeScanner<D> v, D arg);

    public abstract <D> JCTree translate(TreeTranslator<D> v, D d);

    /* 是否是有效的(用于优化阶段) */
    public abstract boolean isEffective();

}
