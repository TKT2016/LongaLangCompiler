package longa.domains.guidsl;

import longa.langtags.LgaChain;
import longa.langtags.LgaNode;

import javax.swing.*;

@LgaChain( domain = "languages.guidsl" )
public class textField_chain {
    JTextField  jTextField;

    public textField_chain()
    {

    }

    public textField_chain __start( ) {
        jTextField = new JTextField();
        return this;
    }

    @LgaNode
    public void bounds(int x, int y, int width, int height)
    {
        jTextField.setBounds(x,y,width,height);
    }

    public JTextField __end() {
        return jTextField;
    }

}
