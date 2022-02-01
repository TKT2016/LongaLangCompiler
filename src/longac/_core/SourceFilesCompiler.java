package longac._core;

import longac.analyzers.ReturnAnalyzer;
import longac.attrs.*;
import longac.analyzers.AssignAnalyzer;
import longac.diagnostics.SourceLog;
import longac.emits.CodeEmit;
import longac.lex.Tokenizer;
import tools.io.JavaSourceReader;
import longac.parse.JCParser;
import longac.symbols.ProjectSymbol;
import longac.trees.JCFileTree;
import longac.utils.CompileContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SourceFilesCompiler {

    CompileContext context;

    public SourceFilesCompiler( )
    {
        this.context = new CompileContext();
      
    }

    public ProjectSymbol compile(String [] input_files) {
        ProjectSymbol projectSymbol = new ProjectSymbol();
        context.projectSymbol = projectSymbol;
        compileFiles(input_files);
        return projectSymbol;
    }

    private void compileFiles(String [] input_files) {
        ArrayList<JCFileTree> compilationFiles = null;
        compilationFiles = parseFiles(input_files);
        enterTrees(compilationFiles);
        if (errorCount() > 0) return;
        memberEnterTrees(compilationFiles);
        if (errorCount() > 0) return;
        attrTrees(compilationFiles);
        if (errorCount() > 0) return;
        compilationFiles = flowTrees(compilationFiles);
        compilationFiles = optimize(compilationFiles);
        genTrees(compilationFiles);
    }

    public ArrayList<JCFileTree> parseFiles(String [] fileObjects)
    {
        ArrayList<JCFileTree> trees = new ArrayList<>();
        for (String fileObject : fileObjects) {
            JCFileTree compilationFile = parse(fileObject);
            if(compilationFile!=null)
                trees.add(compilationFile);
        }
        return trees;
    }

    public void enterTrees(ArrayList<JCFileTree> roots) {
        (new FileVisitor(context)).visit(roots);
        (new TypeEnter(context)).visit(roots);
    }

    public void memberEnterTrees(ArrayList<JCFileTree> roots) {
        (new CompletenessScanner(context)).visit(roots);
        (new ImportEnter(context)).visit(roots);
        (new MemberEnter(context)).enter(roots);
    }

    public void genTrees(ArrayList<JCFileTree> roots) {
        for (JCFileTree compilationUnit : roots) {
            genCode(compilationUnit);
        }
    }

    public void genCode(JCFileTree compilationUnit)
    {
        CodeEmit gen = new CodeEmit(context);
        try {
            if (errorCount() == 0)
            {
                gen.genCompilationUnit(compilationUnit);
            }
        }
        catch (IOException ex)
        {
            context.log.error("生成class文件发生异常:"+  ex.getMessage());
        }
    }

    public ArrayList<JCFileTree> flowTrees(ArrayList<JCFileTree> roots) {
        ReturnAnalyzer returnFlowVisitor = new ReturnAnalyzer(context);
        for (JCFileTree compilationUnit : roots) {
            new AssignAnalyzer(context).analyzeTree(compilationUnit);
            returnFlowVisitor.visitCompilationUnit( compilationUnit,null);
        }
        return roots;
    }

    /* 语义分析 */
    public void attrTrees(ArrayList<JCFileTree> roots) {
        LoopVisitor loopVisitor = new LoopVisitor(context);
        StmtExpAttrTranslator stmtExpAttrTranslator = new StmtExpAttrTranslator(context);
        for (JCFileTree compilationUnit : roots) {
            stmtExpAttrTranslator.translateCompilationUnit(compilationUnit,null);
            loopVisitor.visitCompilationUnit(compilationUnit,null);
        }
    }

    protected ArrayList<JCFileTree> optimize(ArrayList<JCFileTree> roots)
    {
        Optimizer optimizer = new Optimizer(context);
        ArrayList<JCFileTree> list = new ArrayList<>();
        for (JCFileTree compilationUnit : roots) {
            JCFileTree tree1 = (JCFileTree)optimizer.translate(compilationUnit);
            list.add( tree1);
        }
        return list;
    }

    public JCFileTree parse(String file)
    {
        File fileObj = new File(file);
        if(!fileObj.exists())
        {
            context.log.error("文件不存在:" + file+"");
            return null;
        }
        try {
            JavaSourceReader reader = new JavaSourceReader(file);
            String code = reader.readFileContent();
            SourceLog log = new SourceLog( context, file,code);

            Tokenizer tokenizer = new Tokenizer(log, code);
            JCParser parser = new JCParser(context,tokenizer,log);
            JCFileTree tree = parser.parseCompilationUnit();
           // System.out.println(tree.toString());
            return tree;
        }
        catch (IOException e) {
            context.log.error("文件读取发生异常:" + file+":"+e.getMessage());
        }
        return null;
    }

    public int errorCount() {
        if ( context.errors == 0 && context.warnings > 0) {
            context.log.response("没有错误,有"+context.warnings +"个警告");
        }
        return context.errors;
    }
}
