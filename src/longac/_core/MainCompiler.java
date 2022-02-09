package longac._core;

import longac.diagnostics.SourceLog;
import longac.lex.Token;
import longac.lex.TokenKind;
import longac.lex.Tokenizer;
import longac.utils.CompileContext;
import tools.io.FileUtil;
import tools.io.JavaSourceReader;
import tools.runs.ClassRunArg;
import tools.runs.ClassRuner;
import longac.symbols.DeclClassSymbol;
import longac.symbols.ProjectSymbol;
//import org.apache.commons.cli.*;
import java.io.IOException;

public class MainCompiler {

    /*public static void main(String[] args) {
        CommandLineParser parser = new BasicParser();
        Options options = new Options();


        options.addOption("h", "help", false, "Print this help message.");
        options.addOption("V", "version", false, "Version information.");

        try {
            CommandLine cmd = parser.parse(options, args);


            int errors = 0;
            if (!cmd.hasOption("i")) {
                System.out.println("-i|--input requires a MiniJava input file");
                System.out.println("--help for more info.");
                errors++;
            }

            if (errors > 0) {
                System.exit(1);
            }
            String inputFile = cmd.getOptionValue("i");
            compile(inputFile, cmd);
        } catch (ParseException exp) {
            System.out.println("Argument Error: " + exp.getMessage());
        }
    }

    public static void compile(String inputFile, CommandLine cmd) {
        String [] input_files = {inputFile};
        SourceFilesCompiler compileFiles = new SourceFilesCompiler();
        compileFiles.compile(input_files);
    }*/

    public static void compileRun(String inputFile,boolean checkBytes)
    {
        String [] input_files = {inputFile};
        SourceFilesCompiler compileFiles = new SourceFilesCompiler();
        ProjectSymbol projectSymbol = compileFiles.compile(input_files);

        ClassRuner classRuner = new ClassRuner();
        if(projectSymbol.compiledClassFiles.size()==0)
        {
            return;
        }
        String[] classPaths = new String[]{FileUtil.getCurrentPath()+"\\out"};
        try {
            DeclClassSymbol declClassSymbol = projectSymbol.compiledClassFiles.get(0);
            byte[] bytes =  FileUtil.readAllBytes(declClassSymbol.compiledClassFile);
            ClassRunArg runArg = new ClassRunArg();
            runArg.classPaths = classPaths;
            runArg.checkBytes = checkBytes;
            runArg.bytes = bytes;
            runArg.className =  declClassSymbol.getFullname();
            runArg.method =  "main";
            runArg.argTypes = new Class<?>[]{};
            runArg.args = new Object[]{};
            Object result=  classRuner.runBytes(runArg);
        }
        catch (IOException e)
        {
            System.err.println("文件编译失败:"+e.getMessage());
        }
        catch (Exception e)
        {
            System.err.println("调用异常:"+e.getMessage());
            e.printStackTrace();
        }
    }

    /* 词法分析 */
    public static void scan(String file)
    {
        CompileContext context= new CompileContext();

        try {
            JavaSourceReader reader = new JavaSourceReader(file);
            String code = reader.readFileContent();
            SourceLog log = new SourceLog(context, file,code);

            Tokenizer tokenizer = new Tokenizer(log, code);
            while (true)
            {
                Token token = tokenizer.readToken();
                if(token!=null&& token.kind !=TokenKind.EOF)
                {
                    System.out.println(token);
                }
                else
                    break;
            }
        }
        catch (IOException e) {
            context.log.error("文件读取发生异常:" + file+":"+e.getMessage());
        }
    }
}
