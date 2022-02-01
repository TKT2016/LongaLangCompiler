package longac.trees;

import longac.visitors.TreePretty;
import tools.jvmx.NamesTexts;
import longac.symbols.DeclMethodSymbol;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;
import java.io.StringWriter;
import java.util.ArrayList;

/** 定义方法树 */
public class JCMethodDecl extends JCTree
{
    public String name;

    public JCExpression retTypeExpr;
    public ArrayList<JCVariableDecl> params;
    public JCBlock body;

    public JCMethodDecl(String name, JCExpression restype, ArrayList<JCVariableDecl> params, JCBlock body)
    {
        this.name = name;
        this.retTypeExpr = restype;
        this.params = params;
        this.body = body;
        if(this.body!=null)
            this.endpos =this.body.endpos;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitMethodDef(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateMethod(this, d);
    }

    public DeclMethodSymbol getMethodSymbol()
    {
        return (DeclMethodSymbol)this.symbol;
    }

    public String getHeadString() {
        StringWriter s = new StringWriter();
        try {
            new TreePretty(s, false).visitMethodDefHead(this);
        }
        catch (TreePretty.UncheckedIOException e) {
            throw new AssertionError(e);
        }
        return s.toString();
        /*
        StringBuilder buff = new StringBuilder();
        buff.append(retTypeExpr);
        buff.append(" ");
        buff.append(name);
        buff.append("(");
        for(int i=0;i< params.size();i++ )
        {
            buff.append(params.get(i).toString());
            if(i!=params.size()-1)
                buff.append(" , ");
        }
        buff.append(")");
        return buff.toString();*/
    }

    public boolean isContructor()
    {
        return this.name.equals( NamesTexts.init);
    }

    @Override
    public boolean isEffective()
    {
        return true;
    }
}
