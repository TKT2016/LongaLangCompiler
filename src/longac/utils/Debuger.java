package longac.utils;

public class Debuger {
    public static void mark(Object obj ) {
        if(obj!=null)
            outln("Debuger.mark:"+obj.toString());
    }

    public static int index=0;

    public static void outln(Object obj)
    {
        if(obj==null)
            obj="<null>";
        changeDebug(2);
        printColorStart( );
        print(index+"|-> ");
        println(obj);
        printColorEnd( );
    }
/*
    public static void outlnCompletionAnnotateException(Object obj)
    {
        printColorStart( );
        print(index+"|->");
        println(obj);
        printColorEnd( );
    }*/
/*
    public static void outln( )
    {
        println();
    }
*/
    public static void outln(int color,Object obj)
    {
        changeDebug(2);
        printColorStart(color );
        print(index+"|->");
        println(obj);
        printColorEnd( );
    }

    public static void outln(Object[] objs)
    {
        changeDebug(3);
        printColorStart( );
        print(index+"|-->[");
        for (int i=0; i<objs.length;i++)
        {
            System.out.print(objs[i]);
            print(" , ");
        }
        println("]");
        printColorEnd( );
    }

    public static void outln(int color,Object[] objs)
    {
        changeDebug(3);
        printColorStart( color);
        print(index+"|-->[");
        for (int i=0; i<objs.length;i++)
        {
            System.out.print(objs[i]);
            print(" , ");
        }
        println("]");
        printColorEnd( );
    }

    public static boolean inState = false;
    private static int inMaxCount = 1;
    private static int inCount = 0;
    public static void stateInc()
    {
        inCount++;
        if(inCount>=inMaxCount)
        {
            inState=false;
            inCount=0;
        }
    }

    public static void setInMaxCount(int c)
    {
        inMaxCount=c;
        inCount=0;
    }

    //public static int debugCode = 0;
    private static int colorCode = 30 ;
    private static void changeDebug(int icode)
    {
        index++;
        colorCode =30+ index%7;
        //System.out.println("\033[1;" + colorCode+ "m" + "" + "\033[0m ");
        //System.out.println("\033[1;" + colorCode+ "m" + "aaa" + "\033[0m ");
        //System.out.print("\033[1;" + colorCode+ "m" + "" );
        //System.out.println( "\033[0m ");
    }

    private static void printColorStart( )
    {
        //System.out.print("\033[41;32;4m" +obj.toString() + "\033[0m");
        System.out.print("\033["+colorCode+"m");
    }

    private static void printColorStart(int color )
    {
        //System.out.print("\033[41;32;4m" +obj.toString() + "\033[0m");
        color =30+ color %7;
        System.out.print("\033["+color+"m");
    }

    private static void printColorEnd( )
    {
        //System.out.print("\033[41;32;4m" +obj.toString() + "\033[0m");
        System.out.print("\033[0m");
    }

    private static void print(Object obj)
    {
        //System.out.print("\033[41;32;4m" +obj.toString() + "\033[0m");
        //System.out.print("\033["+colorCode+"m" + obj.toString()  + "\033[0m");
       /* if(index==14)
        {
            System.out.print("14 126");
        }*/
        System.out.print(obj.toString());
    }

    private static void println(Object obj)
    {
        print(obj);
        System.out.println();
    }

    private static void println( )
    {
        System.out.println();
    }
}
