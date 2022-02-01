package tools.collectx;

import java.util.ArrayList;
import java.util.List;

public class ArrayListUtil {

    public static <T> T  head(ArrayList<T> list)
    {
        return list.get(0);
    }

    public static <T> T  last(ArrayList<T> list)
    {
        int size = list.size();
        return list.get(size-1);
    }

    public static  <T> boolean nonEmpty(ArrayList<T> list)
    {
        return list!=null&&list.size()>0;
    }

    public static <T> ArrayList<T>  toList( T t)
    {
        ArrayList<T> arrayList = new ArrayList<>();
        arrayList.add(t);
        return arrayList;
    }

    public static <T> ArrayList<T>  prepend( ArrayList<T> arrayList, T t)
    {
        ArrayList<T> arrayList2 = new ArrayList<>();
        arrayList2.add(t);
        arrayList2.addAll(arrayList);
        arrayList.clear();
        arrayList = null;
        return arrayList2;
    }

    public static <T> void  removeLast( ArrayList<T> arrayList)
    {
       int size = arrayList.size();
       if(size==0) return;
       arrayList.remove(size-1);
    }

    public static <A,B> ArrayList<B>  toList( List<A> aList)
    {
        ArrayList<B> arrayList = new ArrayList<>();
        for (A a:aList)
        {
            arrayList.add((B)a);
        }
        return arrayList;
    }
}
