package tests;

import longa.lang.List;

public class TestLongaList {
    public  List<Object> test1()
    {
        List<Object> objectList = new List<>();
        objectList.add("a");
        objectList.add("a");
        return objectList;
    }

    public Object[] test2()
    {
      return new Object[]{"A","B"};
    }
}
