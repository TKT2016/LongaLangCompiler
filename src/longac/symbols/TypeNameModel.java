package longac.symbols;

public class TypeNameModel
{
    public String packageName;
    public String typeSimpleName;

    public TypeNameModel(String packageName,String typeSimpleName)
    {
        this.packageName=packageName;
        this.typeSimpleName=typeSimpleName;
    }

    public TypeNameModel(String typeSimpleName)
    {
        this.typeSimpleName=typeSimpleName;
    }

    public String getNameFull()
    {
        if(packageName==null)
            return typeSimpleName;
        else
            return packageName+"."+typeSimpleName;
    }

    public boolean isFull()
    {
        return packageName!=null;
    }
}
