package tools.jvmx;

public class NamesTexts {
    public static final String init ="<init>";
    // operators and punctuation
    public static final String star ="*";
    public static final String comma=",";
    public static final String empty="";
    //public static final String hyphen="-";
    public static final String one="1";
    //public static final String slash="/";

    // keywords
  /*  public static final String _class="class";
    public static final String _super="super";*/
    public static final String _this="this";
    //public static final String var;
    /*public static final String exports="exports";
    public static final String opens="opens";
    public static final String module="module";
    public static final String provides="provides";
    public static final String requires="requires";*/
    public static final String to="to";
  /*  public static final String transitive="transitive";
    public static final String uses="uses";
    public static final String open="open";
    public static final String with="with";
    public static final String yield="yield";*/

    // field and method names
  /*  public static final String _name ="name";
    public static final String addSuppressed ="addSuppressed";
    public static final String any ="<any>";*/
    public static final String append ="append";
    /*public static final String clinit ="<clinit>";
    public static final String clone ="clone";
    public static final String close ="close";
    public static final String deserializeLambda ="$deserializeLambda$";
    public static final String desiredAssertionStatus ="desiredAssertionStatus";*/
    public static final String equals ="equals";
    public static final String error ="<error>";
   /* public static final String finalize ="finalize";
    public static final String forRemoval ="forRemoval";
    public static final String essentialAPI ="essentialAPI";
    public static final String getClass ="getClass";
    public static final String hasNext ="hasNext";
    public static final String hashCode ="hashCode";*/

  //  public static final String iterator ="iterator";
    public static final String length ="length";
  /*  public static final String next ="next";
    public static final String ordinal ="ordinal";
    public static final String provider ="provider";
    public static final String serialVersionUID ="serialVersionUID";*/
    public static final String toString ="toString";
    public static final String value ="value";
    public static final String valueOf ="valueOf";
    /*public static final String values ="values";
    public static final String readResolve ="readResolve";
    public static final String readObject ="readObject";*/

    // class names
   /* public static final String java_io_Serializable="java.io.Serializable";
    public static final String java_lang_Class="java.lang.Class";
    public static final String java_lang_Cloneable="java.lang.Cloneable";
    public static final String java_lang_Enum="java.lang.Enum";
    public static final String java_lang_Object="java.lang.Object";

    // names of builtin classes
    public static final String Array="Array";
    public static final String Bound="Bound";
    public static final String Method="Method";*/

  /*  // package names
    public static final String java_lang="java.lang";

    // module names
    public static final String java_base="java.base";*/

    // attribute names
   /* public static final String Annotation="Annotation";
    public static final String AnnotationDefault="AnnotationDefault";
    public static final String BootstrapMethods="BootstrapMethods";
    public static final String Bridge="Bridge";
    public static final String CharacterRangeTable="CharacterRangeTable";
    public static final String Code="Code";
    public static final String CompilationID="CompilationID";
    public static final String ConstantValue="ConstantValue";
    public static final String Deprecated="Deprecated";
    public static final String EnclosingMethod="EnclosingMethod";
    public static final String Enum="Enum";
    public static final String Exceptions="Exceptions";
    public static final String InnerClasses="InnerClasses";
    public static final String LineNumberTable="LineNumberTable";
    public static final String LocalVariableTable="LocalVariableTable";
    public static final String LocalVariableTypeTable="LocalVariableTypeTable";
    public static final String MethodParameters="MethodParameters";
    public static final String Module="Module";
    public static final String ModuleResolution="ModuleResolution";
    public static final String NestHost="NestHost";
    public static final String NestMembers="NestMembers";*/
    //public static final Name Record;
  /*  public static final String RuntimeInvisibleAnnotations="RuntimeInvisibleAnnotations";
    public static final String RuntimeInvisibleParameterAnnotations="RuntimeInvisibleParameterAnnotations";
    public static final String RuntimeInvisibleTypeAnnotations="RuntimeInvisibleTypeAnnotations";
    public static final String RuntimeVisibleAnnotations="RuntimeVisibleAnnotations";
    public static final String RuntimeVisibleParameterAnnotations="RuntimeVisibleParameterAnnotations";
    public static final String RuntimeVisibleTypeAnnotations="RuntimeVisibleTypeAnnotations";
    public static final String Signature="Signature";
    public static final String SourceFile="SourceFile";
    public static final String SourceID="SourceID";
    public static final String StackMap="StackMap";
    public static final String StackMapTable="StackMapTable";
    public static final String Synthetic="Synthetic";
    public static final String Value="Value";
    public static final String Varargs="Varargs";*/

    // members of java.lang.annotation.ElementType
   /* public static final String ANNOTATION_TYPE="ANNOTATION_TYPE";
    public static final String CONSTRUCTOR="CONSTRUCTOR";
    public static final String FIELD="FIELD";
    public static final String LOCAL_VARIABLE="LOCAL_VARIABLE";
    public static final String METHOD="METHOD";
    public static final String MODULE="MODULE";
    public static final String PACKAGE="PACKAGE";
    public static final String PARAMETER="PARAMETER";
    public static final String TYPE="TYPE";
    public static final String TYPE_PARAMETER="TYPE_PARAMETER";
    public static final String TYPE_USE="TYPE_USE";
    //public static final Name RECORD_COMPONENT;

    // members of java.lang.annotation.RetentionPolicy
    public static final String CLASS="CLASS";
    public static final String RUNTIME="RUNTIME";
    public static final String SOURCE="SOURCE";*/

    // other identifiers
    public static final String T="T";
    //  public static final String ex="ex";
  /*  public static final String module_info="module-info";
    public static final String package_info="package-info";
    public static final String requireNonNull="requireNonNull";

    // lambda-related
    public static final String lambda="lambda$";
    public static final String metafactory="metafactory";
    public static final String altMetafactory="altMetafactory";
    public static final String dollarThis="$this";

    // string concat
    public static final String makeConcat="makeConcat";
    public static final String makeConcatWithConstants="makeConcatWithConstants";

    // record related
    // members of java.lang.runtime.ObjectMethods
    public static final String bootstrap="bootstrap";*/

    //public static final Name record;

    // serialization members, used by records too
  /*  public static final String serialPersistentFields="serialPersistentFields";
    public static final String writeObject="writeObject";
    public static final String writeReplace="writeReplace";
    public static final String readObjectNoData="readObjectNoData";*/

    //public final NameTable table;
    //public final Names names;

/*
    protected NameTable createTable(Options options) {
        boolean useUnsharedTable = options.isSet("useUnsharedTable");
        if (useUnsharedTable)
            return UnsharedNameTable.create(this.names);
        else
            return SharedNameTable.create(this.names);
    }*/
/*
    public void dispose() {
        table.dispose();
    }*/
/*
    public Name fromChars(char[] cs, int start, int len) {
        return table.fromChars(cs, start, len);
    }

    public Name fromString(String s) {
        return table.fromString(s);
    }

    public Name fromUtf(byte[] cs) {
        return table.fromUtf(cs);
    }

    public Name fromUtf(byte[] cs, int start, int len) {
        return table.fromUtf(cs, start, len);
    }*/
}
