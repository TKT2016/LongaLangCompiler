package longa.domains.guidsl;

import longa.langtags.LgaChain;
import longa.langtags.LgaNode;

import javax.swing.*;

@LgaChain( domain = "languages.guidsl" )
public class button_chain {
    JButton jButton;

    public button_chain()
    {

    }

    public button_chain __start(String text) {
        jButton = new JButton(text);
        return this;
    }

    @LgaNode
    public void bounds(int x, int y, int width, int height)
    {
        jButton.setBounds(x,y,width,height);
    }

    @LgaNode
    public void size(int x, int y)
    {
        jButton.setSize(x,y);
    }

    public JButton __end() {
        return jButton;
    }

}
