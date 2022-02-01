package longa.domains.guidsl;

import longa.langtags.LgaChain;
import longa.langtags.LgaNode;

import javax.swing.*;

import java.awt.*;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

@LgaChain( domain = "languages.guidsl" )
public class label_chain {
    JLabel jlabel;

    public label_chain()
    {

    }

    public label_chain __start(String text) {
        jlabel = new JLabel(text);
        return this;
    }

    @LgaNode
    public void bounds(int x, int y, int width, int height)
    {
        jlabel.setBounds(x,y,width,height);
    }

    @LgaNode
    public void font(Font font)
    {
        jlabel.setFont(font);
    }


    public JLabel __end() {
        return jlabel;
    }

}
