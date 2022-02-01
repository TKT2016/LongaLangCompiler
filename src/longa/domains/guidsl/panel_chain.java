package longa.domains.guidsl;

import longa.langtags.LgaChain;
import longa.langtags.LgaNode;

import javax.swing.*;
import java.awt.*;

@LgaChain( domain = "languages.guidsl" )
public class panel_chain {
    JPanel jpanel;

    public panel_chain()
    {

    }

    public panel_chain __start( ) {
        jpanel = new JPanel();
        return this;
    }

    @LgaNode
    public void noneLayout( )
    {
        jpanel.setLayout(null);
    }

    @LgaNode
    public void bounds(int x, int y, int width, int height)
    {
        jpanel.setBounds(x,y,width,height);
    }

    @LgaNode
    public void component(Component component)
    {
        jpanel.add(component);
    }

    public JPanel __end() {

        return jpanel;
    }

}
