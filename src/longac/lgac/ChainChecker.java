package longac.lgac;

import longa.langtags.LgaMethodReqKind;
import longa.langtags.LgaNode;
import longac.attrs.ExprVisitContext;
import longac.attrs.StmtExpAttrTranslator;
import longac.lgac.trees.ChainTree;
import longac.lgac.trees.MethodChainTree;
import longac.lgac.trees.TypeChainTree;
import longac.symbols.*;
import longac.trees.JCExpression;
import longac.trees.JCIdent;
import longac.utils.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import static longa.lang.LongaSpecialNames.endMthodName;

public class ChainChecker {

    StmtExpAttrTranslator expVisitor;
    ExprVisitContext context;

    public ChainChecker(StmtExpAttrTranslator expVisitor, ExprVisitContext context)
    {
        this.expVisitor = expVisitor;
        this.context = context;
    }

    public void check(TypeChainTree typeChainTree){
        parseTypeChainTree(typeChainTree);
    }

    void parseTypeChainTree(TypeChainTree typeChainTree)
    {
        checkCalls(typeChainTree);
        for(ChainTree tree:typeChainTree.calls)
        {
            if(tree instanceof TypeChainTree)
            {
                parseTypeChainTree((TypeChainTree)tree);
            }
        }
    }

    void checkCalls(TypeChainTree typeChainTree)
    {
        checkMethodReq(typeChainTree);
    }

    void checkMethodReq(TypeChainTree typeChainTree)
    {
        Method[] methods = typeChainTree.javaClassSymbol.clazz.getMethods();
        for(Method method :methods)
        {
            if(Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()))
            {
                if(method.isAnnotationPresent(LgaNode.class))
                {
                    LgaNode lgaNode = (LgaNode) method.getAnnotation(LgaNode.class);
                    if(lgaNode.once()!= LgaMethodReqKind.any)
                    {
                        String methodName = method.getName();
                        ArrayList<MethodChainTree> chainTrees = findMethodCall(typeChainTree,method.getName());
                        if(lgaNode.once()== LgaMethodReqKind.mustOnce) {
                            if (chainTrees.size() == 0) {
                                typeChainTree.startIdent.error("invoke chain need call '" + methodName+"'");
                            }
                            else if (chainTrees.size() >1) {
                                typeChainTree.startIdent.error("invoke chain call '" + methodName+"' only once");
                            }
                        }
                    }
                }
            }
        }
    }

    ArrayList<MethodChainTree> findMethodCall(TypeChainTree typeChainTree,String methodName)
    {
        ArrayList<MethodChainTree> list = new ArrayList<>();
        for (ChainTree tree : typeChainTree.calls)
        {
            if(tree instanceof MethodChainTree)
            {
                MethodChainTree methodChainTree =(MethodChainTree)tree;
                if(methodChainTree.getMethodName().equals(methodName))
                    list.add(methodChainTree);
            }
        }
        return list;
    }

}
