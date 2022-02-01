package longac.trees;

import org.objectweb.asm.Label;

/** 循环语句父类*/
public abstract class LoopStatement extends JCStatement{
    public Label loopBodyStartLabel;
    public Label loopBodyEndLabel;
}
