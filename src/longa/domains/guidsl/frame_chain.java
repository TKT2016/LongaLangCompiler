package longa.domains.guidsl;

import longa.langtags.LgaChain;
import longa.langtags.LgaNode;

import javax.swing.*;

import java.awt.*;

import javax.swing.WindowConstants;

@LgaChain( domain = "languages.guidsl" )
public class frame_chain {
    JFrame jframe;

    public frame_chain()
    {

    }

    public frame_chain __start( ) {
        jframe = new JFrame();
        return this;
    }

    @LgaNode
    public void title(String text)
    {
        jframe.setTitle(text);
    }

    @LgaNode
    public void bounds(int x, int y, int width, int height)
    {
        jframe.setBounds(x,y,width,height);
    }

    @LgaNode
    public void size( int width, int height)
    {
        jframe.setSize(width,height);
    }

    @LgaNode
    public void component(Component component)
    {
        if(component instanceof JPanel)
            jframe.add((JPanel)component);
        else
            jframe.getContentPane().add(component);
    }

    @LgaNode
    public void pack()
    {
        jframe.pack();
    }

    public JFrame __end() {
        jframe.setVisible(true);//设置窗口可见
        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return jframe;
    }

}
