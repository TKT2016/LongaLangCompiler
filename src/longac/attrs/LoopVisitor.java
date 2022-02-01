package longac.attrs;
import longac.diagnostics.SimpleLog;
import longac.trees.*;
import longac.utils.CompileContext;
import longac.visitors.TreeScanner;

public class LoopVisitor extends TreeScanner<LoopStatement>
{
    SimpleLog log;
    CompileContext context;

    public LoopVisitor(CompileContext context) {
        this.context = context;
        log =context.log;
    }

    @Override
    public void visitWhileLoop(JCWhile tree, LoopStatement arg)
    {
        tree.body.scan(this,tree);
    }

    @Override
    public void visitForLoop(JCForLoop tree, LoopStatement arg)
    {
        tree.body.scan(this,tree);
    }

    @Override
    public void visitBreak(JCBreak tree, LoopStatement arg)
    {
        if(arg==null)
            tree.error("break没有对应的循环");
        else
            tree.loopTree = arg;
    }

    @Override
    public void visitContinue(JCContinue tree, LoopStatement arg)
    {
        if(arg ==null)
            tree.error("continue 没有对应的循环");
        else
            tree.loopTree = arg;
    }
}
