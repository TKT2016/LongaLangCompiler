package tools.commons;

public class StringHelper {

    public final static byte CR    = 0xD;
    public final static byte LF    = 0xA;

    public static String getLineString(String str,int pos)
    {
        if(isNewLineChar(str.charAt(pos)))
            return "";
        int pos1 =getNewLineCharPosForward (str,pos);
        int pos2 = getNewLineCharPosBackward(str,pos);
        return str.substring(pos1+1,pos2);
    }

    public static int getNewLineCharPosForward(String str,int pos)
    {
        int i=pos;
        char ch ;
        for(;i>=0;i--)
        {
            ch = str.charAt(i);
            if(isNewLineChar(ch))
                break;
        }
        ch = str.charAt(i);
        if(!isNewLineChar(ch))
            i--;
        return i;
    }

    public static int getNewLineCharPosBackward(String str,int pos)
    {
        int i=pos;
        char ch ;
        for(;i<str.length();i++)
        {
            ch = str.charAt(i);
            if(isNewLineChar(ch))
                break;
        }
        ch = str.charAt(i);
        return i;

    }
    public static boolean isNewLineChar(char ch)
    {
        return (ch=='\n'||ch=='\r');
    }
}
