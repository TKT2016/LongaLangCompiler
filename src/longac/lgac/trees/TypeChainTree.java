package longac.lgac.trees;

import longac.lgac.makeModels.FieldTypeMakeModel;
import longac.lgac.makers.FieldTypeMaker;
import longac.lgac.makeModels.MethodEndModel;
import longac.lgac.makeModels.HeadTypeMakeModel;
import longac.symbols.JavaClassSymbol;
import longac.symbols.JavaMethodSymbol;
import longac.symbols.JavaVarSymbol;
import longac.trees.JCChain;
import longac.trees.JCExpression;
import longac.trees.JCIdent;
import longac.utils.Assert;
import longac.utils.CompileError;

import java.util.ArrayList;
import java.util.concurrent.CompletionException;

public class TypeChainTree extends ChainTree {
    public final JCChain jcChain;

    public final JCIdent startIdent;
    public final JavaVarSymbol fieldSymbol;
    public final JavaClassSymbol javaClassSymbol;
    public final TypeChainKind typeChainKind;

    public final TypeChainTree parent;

    public ArrayList<ChainTree> calls = new ArrayList<>();

    public TypeChainTree( JCChain jcChain,JCIdent startIdent,JavaClassSymbol javaClassSymbol,TypeChainTree parent)
    {
        this.jcChain=jcChain;
        this.parent=parent;
        this.startIdent = startIdent;
        this.javaClassSymbol = javaClassSymbol;
        this.fieldSymbol=null;
        this.typeChainKind =TypeChainKind.headType;
    }

    public TypeChainTree( JCChain jcChain,JCIdent startIdent,JavaVarSymbol fieldSymbol,TypeChainTree parent)
    {
        this.jcChain=jcChain;
        this.parent=parent;
        this.startIdent = startIdent;
        this.fieldSymbol = fieldSymbol;
        this.javaClassSymbol =(JavaClassSymbol) this.fieldSymbol.getTypeSymbol();
        this.typeChainKind =TypeChainKind.typeField;
    }

    public JavaMethodSymbol endMethodSymbol;

    public HeadTypeMakeModel headTypeMakeModel;

    public FieldTypeMakeModel fieldTypeMaker;

    public MethodEndModel methodEndModel;

    public JCExpression getMasterExpr()
    {
        if(typeChainKind== TypeChainKind.headType)
        {
            return headTypeMakeModel.nameIdent;
        }
        else if(typeChainKind== TypeChainKind.typeField)
        {
            return fieldTypeMaker.rightFieldGet;
        }
        else
        {
            throw new CompileError();
        }
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(startIdent);
        builder.append(" { ");
        for (ChainTree tree:this.calls)
        {
            builder.append(tree);
            builder.append(" ");
        }
        builder.append("} ");
        return builder.toString();
    }

    public enum TypeChainKind
    {
        headType,
        typeField
    }
}

