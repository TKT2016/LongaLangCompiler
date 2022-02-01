package longac.trees;

import longac.symbols.BlockFrame;
import longac.visitors.TreeScanner;
import longac.visitors.TreeTranslator;
import org.objectweb.asm.Label;

import java.util.ArrayList;

/** 代码块语句 */
public class JCBlock extends JCStatement
{
    public ArrayList<JCStatement> stats;

    public int endLine;


    public JCBlock(ArrayList<JCStatement> stats) {
        this.stats = stats;
    }

    public BlockFrame frame;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitBlock(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D d) {
        return v.translateBlock(this, d);
    }

    public Label startLabel;
    public Label endLabel ;

}