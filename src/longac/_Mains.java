package longac;

import longac.emits.CodeEmit;
import longac._core.SourceFilesCompiler;
import longac.analyzers.AssignAnalyzer;
import longac.diagnostics.SimpleLog;
import tools.asmx.JAsmUtil;
import longac.lex.Tokenizer;
import tools.asmx.ASMEmitlus;
import tools.asmx.JVMOpCodeSelecter;
import longac.utils.Debuger;
import longac.visitors.TreePretty;

public class _Mains {
    Debuger debuger;
    TreePretty treePretty;
    SimpleLog log;

    SourceFilesCompiler mainCompileFiles;
    Tokenizer javaTokenizer;
    //Scanner scanner;
    //StmtExpVisitor attrVisitor;

    CodeEmit byteCodeGen;

    JAsmUtil jAsmUtil;
    ASMEmitlus asmPlusEmit;
    JVMOpCodeSelecter opCodeSelecter;

    AssignAnalyzer assignAnalyzer;

}
