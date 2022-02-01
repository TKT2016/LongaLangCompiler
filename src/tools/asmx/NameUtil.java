package tools.asmx;

public class NameUtil {
    public static String nameToSign(String classFullName)
    {
        String  str = classFullName.replace(".", "/");
        return str;
    }
/*
    public static String nameToSign(Name classFullName)
    {
        String  str = classFullName.toString();
        return nameToSign(str);
    }*/
}
