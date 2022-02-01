package tools.collectx;

import java.util.ArrayList;
import java.util.HashMap;

public class ListMap<V>
{
    //HashMap<K,V> map ;
    HashMap<String,V> nameMap ;
    HashMap<V,Integer> indexMap ;
    ArrayList<V> list;
    //boolean allowDuplicate;

    public ListMap( )
    {
        nameMap = new HashMap<>();
        indexMap = new HashMap<>();
        list = new ArrayList<>();
    }


    public int size()
    {
        return list.size();
    }

    public void put(String name,V v)
    {
        indexMap.put(v,list.size());
        nameMap.put(name,v);
        list.add(v);
    }

    public V get(int i)
    {
        return list.get(i);
    }

    public V get(String name)
    {
        if(nameMap.containsKey(name))
            return nameMap.get(name);
        return null;
    }

    public boolean contains(String name)
    {
        return nameMap.containsKey(name);
    }

}
