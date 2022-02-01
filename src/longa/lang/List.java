package longa.lang;

import java.util.ArrayList;

public class List<T> {
    ArrayList<T> arrayList;

    public List()
    {
        arrayList = new ArrayList<>();
    }

    public void add(T t)
    {
        arrayList.add(t);
    }

    public T get(int index)
    {
        return arrayList.get(index);
    }

    public int size( )
    {
        return arrayList.size();
    }
}
