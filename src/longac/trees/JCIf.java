package longac.trees;

//import longac.emits.StackFrame;


import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;

/** 如果否则语句 "if ( ) { } else { }"  */
public class JCIf extends JCStatement
{
    public JCExpression cond;
    public JCStatement thenpart;
    public JCStatement elsepart;
    public JCIf(JCExpression cond, JCStatement thenpart, JCStatement elsepart)
    {
        this.cond = cond;
        this.thenpart = thenpart;
        this.elsepart = elsepart;
    }

    public boolean hasElse()
    {
        if(elsepart==null)
            return false;
        if(!(elsepart instanceof JCIf))
            return true;
        return ((JCIf) elsepart).hasElse();
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitIf(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateIf(this, d);
    }

   // public StackFrame thenStackFrame;
   // public StackFrame elseStackFrame;
}
