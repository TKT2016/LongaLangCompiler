package longac.attrs;

public class FindKinds {
    public boolean isFindType = false;
    //public boolean isFindField = false;
    public boolean isFindVar = false;
    public boolean isFindMethod = false;

    public FindKinds()
    {

    }

    public FindKinds(boolean isFindVar,boolean isFindMethod)
    {
        this.isFindVar =isFindVar;
        this.isFindMethod =isFindMethod;
    }

    public FindKinds(boolean isFindVar,boolean isFindMethod,boolean isFindType)
    {
        this.isFindVar =isFindVar;
        this.isFindMethod =isFindMethod;
        this.isFindType =isFindType;
    }
}
