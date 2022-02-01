package longac.symbols;

import longa.langtags.LgaNode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

public class JavaVarSymbol extends VarSymbol {
    Field field;
    Parameter parameter;

    public LgaNode lgaNode;

    public JavaVarSymbol(Field field, Symbol owner) {
        super(field.getName(),owner, VarKind.field,JavaClassSymbol.create(field.getType()));
        this.field = field;
        if(field.isAnnotationPresent(LgaNode.class))
        {
            lgaNode = (LgaNode) field.getAnnotation(LgaNode.class);
        }
    }

    public JavaVarSymbol(Parameter parameter,Symbol owner) {
        super(parameter.getName(),owner,VarKind.field,JavaClassSymbol.create(parameter.getType()));
        this.parameter = parameter;

    }

    public boolean isField()
    {
        return field!=null;
    }

    @Override
    public boolean isStatic()
    {
        return  Modifier.isStatic(field.getModifiers());
    }

    @Override
    public boolean canWrite()
    {
        return  Modifier.isFinal(field.getModifiers());
    }

}
