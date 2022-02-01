package tests;

import longa.domains.sql.select_chain;
import longa.langtags.LgaNode;

import java.lang.reflect.Method;

public class TestIsAnnotationPresent {
    public static void main(String[] args) {
        select_chain selectChain = new select_chain();
        Class<?> clazz = selectChain.getClass();
        Method[] methods = clazz.getMethods();
        for(Method method:methods)
        {
            System.out.println(method.getName() +":"+ method.isAnnotationPresent(LgaNode.class));
        }

    }
}
