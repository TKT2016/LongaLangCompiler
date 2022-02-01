package longac.symbols;

import longac.trees.JCFieldAccess;
import longac.trees.JCIdent;
import longac.trees.JCImport;
import longac.utils.Assert;
import longac.utils.CompileError;
import longac.utils.Debuger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ImportSymbol extends Symbol
{
    public final JCImport importTree;
    final String packageName;
    final String typeName;
    String typeFullName="";
    boolean isImportStar;
    final boolean isStatic ;

    public ImportSymbol(JCImport jcImport , Symbol owner,boolean isStatic) {
        super( jcImport.qualid.toString(),owner);
        this.isStatic = isStatic;
        this.importTree=jcImport;
        isImportStar = jcImport.isPackageStar;
        if(isImportStar)
        {
            packageName = jcImport.qualid.toString();
            typeName="";
        }
        else
        {
            typeFullName=jcImport.qualid.toString();
            if(jcImport.qualid instanceof JCFieldAccess)
            {
                JCFieldAccess jcFieldAccess = (JCFieldAccess)jcImport.qualid;
                packageName = jcFieldAccess.selected.toString();
                typeName = jcFieldAccess.name;
            }
            else if(jcImport.qualid instanceof JCIdent)
            {
                JCIdent jcIdent = (JCIdent)jcImport.qualid;
                packageName = "";
                typeName = jcIdent.name;
            }
            else
                throw new CompileError();
        }

    }

    public ImportSymbol(String imported,String lastIdent , Symbol owner,boolean isStatic) {
        super( imported+"."+lastIdent ,owner);
        this.isStatic = isStatic;
        importTree = null;
        isImportStar = lastIdent=="*";
        if(isImportStar)
        {
            packageName =imported;
            typeName="";
        }
        else
        {
            packageName = imported;
            typeName = lastIdent;
            typeFullName=imported+"."+lastIdent;
        }
    }

    public TypeSymbol findSymbol(String ident)
    {
        //Assert.error();
        if(isStatic)
        {
          /*  Debuger.outln("77:"+ident);
            if (isImportStar) {
                String typeFullName = packageName;
                JavaClassSymbol javaClassSymbol = JavaClassSymbol.forName(typeFullName);

                if(javaClassSymbol==null)
                    return null;
                if(javaClassSymbol.clazz.isEnum())
                {
                    Field[] fields = javaClassSymbol.clazz.getFields();
                    for (Field field:fields)
                    {
                        int modi = field.getModifiers();
                        if(Modifier.isPublic(modi)&&Modifier.isStatic(modi)&&Modifier.isFinal(modi))
                        {
                            String fieldName = field.getName();
                            if(fieldName.equals(ident))
                            {
                                Debuger.outln("89:"+field);
                            }
                        }
                    }
                }
                return null;
            }
            else {
                if (this.typeName.equals(ident)) {
                    JavaClassSymbol javaClassSymbol = JavaClassSymbol.forName(typeFullName);
                    return javaClassSymbol;
                }
            }*/

            return null;
        }
        else {
            if (isImportStar) {
                String typeFullName = packageName + "." + ident;
                JavaClassSymbol javaClassSymbol = JavaClassSymbol.forName(typeFullName);
                return javaClassSymbol;
            }
            else {
                if (this.typeName.equals(ident)) {
                    JavaClassSymbol javaClassSymbol = JavaClassSymbol.forName(typeFullName);
                    return javaClassSymbol;
                }
            }
        }
        return null;
    }


    public VarSymbol findVar(String ident)
    {
        if(isStatic)
        {
            //if(ident.equals("left"))
            //    Debuger.outln("133:"+ident);
            if (isImportStar) {
                String typeFullName = packageName;
                JavaClassSymbol javaClassSymbol = JavaClassSymbol.forName(typeFullName);

                if(javaClassSymbol==null)
                    return null;
                if(javaClassSymbol.clazz.isEnum())
                {
                    Field[] fields = javaClassSymbol.clazz.getFields();
                    for (Field field:fields)
                    {
                        int modi = field.getModifiers();
                        if(Modifier.isPublic(modi)&&Modifier.isStatic(modi)&&Modifier.isFinal(modi))
                        {
                            String fieldName = field.getName();
                            if(fieldName.equals(ident))
                            {
                                //Debuger.outln("89:"+field);
                                return new JavaVarSymbol(field,javaClassSymbol);
                            }
                        }
                    }
                   /* Method method = javaClassSymbol.clazz.getMethod("values",new Class[]{});
                    Object object = method.invoke(null, null);

                    EnumMessage inter[] = (EnumMessage[])
                    for (EnumMessage enumMessage : inter) {
                        KeyValue vo = new KeyValue();
                        vo.setKey( enumMessage.getKey() );
                        vo.setValue( enumMessage.getValue() );
                        keyValueList.add(vo);
                    }*/
                }
                return null;
            }
        }
        return null;
    }

    @Override
    public TypeSymbol getTypeSymbol() {
        throw new CompileError();
    }
}
