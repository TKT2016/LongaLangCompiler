package longac.diagnostics;

import longac.utils.CompileContext;

public class SimpleLog {
   /* public int nerrors = 0;
    public int nwarnings = 0;*/

    final CompileContext context;
   // final DiagnosticSource diagnosticSource;
/*
    public Log(CompileContext context,DiagnosticSource diagnosticSource)
    {
        this.context =context;
        this.diagnosticSource =diagnosticSource;
    }*/

    public SimpleLog(CompileContext context)
    {
        this.context =context;
    }

    public void error(String msg)
    {
        context.errors++;
        response(msg);
    }

    public void warning(String msg)
    {
        context.warnings++;
        response(msg);
    }
/*
    public void error(JCTree tree, String msg)
    {
        error(tree.pos, tree.diagnosticSource,msg);
    }*/

    public void response(String msg)
    {
        System.err.println(msg);
    }
/*
    public void error(int pos,String msg)
    {
        context.errors++;

        StringBuilder buff = new StringBuilder();
        buff.append(diagnosticSource.sourceFile);
        buff.append(" ");
        buff.append(diagnosticSource.getLine(pos)+"行 " );
        int col = diagnosticSource.getColumnNumber(pos,false);
        buff.append( col+"列 : ");
        buff.append(msg);
        buff.append("\n"+diagnosticSource.getLineSourceCode(pos)+"\n");
        for(int i=0;i<col;i++)
            buff.append(" ");
        buff.append("^\n");
        response(buff.toString());
    }*/

    /*
    public void error(int pos,DiagnosticSource diagnosticSource ,String msg)
    {
        context.errors++;

        StringBuilder buff = new StringBuilder();
        buff.append(diagnosticSource.sourceFile);
        buff.append(" ");
        buff.append(diagnosticSource.getLine(pos)+"行 " );
        int col = diagnosticSource.getColumnNumber(pos,false);
        buff.append( col+"列 : ");
        buff.append(msg);
        buff.append("\n"+diagnosticSource.getLineSourceCode(pos)+"\n");
        for(int i=0;i<col;i++)
            buff.append(" ");
        buff.append("^\n");
        response(buff.toString());
    }*/
}
