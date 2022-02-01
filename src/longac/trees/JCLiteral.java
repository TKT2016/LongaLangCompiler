package longac.trees;


import longac.lex.TokenKind;
import longac.utils.Assert;
import longac.utils.CompileError;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** 字面常量表达式 */
public class JCLiteral extends JCExpression
{
    public TokenKind literalKind = TokenKind.INTLITERAL;

    public Object value;

    public JCLiteral( TokenKind literalKind,Object value) {
        this.value = value;
        this.literalKind=literalKind;
    }

    public JCLiteral(Object value) {
        this.value = value;
        if(value instanceof String)
            literalKind = TokenKind.STRINGLITERAL;
        else if(value instanceof Integer)
            literalKind = TokenKind.INTLITERAL;
        else if(value instanceof Boolean) {
            Boolean b = (Boolean) value;
            if (b)
                literalKind = TokenKind.TRUE;
            else
                literalKind = TokenKind.FALSE;
        }
        else
            Assert.error();
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitLiteral(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateLiteral(this, d);
    }

    public Class<?> getValueClazz()
    {
        if(literalKind== TokenKind.TRUE)
            return boolean.class;
        else if(literalKind== TokenKind.FALSE)
            return boolean.class;
        else if(literalKind== TokenKind.INTLITERAL)
            return int.class;
        else if(literalKind== TokenKind.STRINGLITERAL)
            return String.class;
        throw new CompileError();
    }

    public Object getValue() {
        if(literalKind== TokenKind.BOOLEAN)
        {
            int bi = (Integer) value;
            return (bi != 0);
        }
        else if(literalKind== TokenKind.TRUE)
        {
            return true;
        }
        else if(literalKind== TokenKind.FALSE)
        {
            return false;
        }
        else if(literalKind== TokenKind.INTLITERAL)
        {
            int ci = (Integer) value;
            return ci;
        }
        else if(literalKind== TokenKind.STRINGLITERAL)
        {
            String ci = (String) value;
            return ci;
        }
        else
        {
            return value;
        }
    }
}
