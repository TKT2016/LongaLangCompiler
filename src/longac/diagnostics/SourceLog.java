package longac.diagnostics;

import longac.utils.CompileContext;
import tools.commons.StringHelper;

import java.io.File;

/* 源文件Log */
public class SourceLog extends SimpleLog {

    public static final int NOPOS  = -1;

    public final String sourceFile;
    final String sourceCode;

    public SourceLog(CompileContext context, String sourceFile, String code)
    {
        super(context);
        this.sourceFile = sourceFile;
        this.sourceCode= code;
    }

    public String getSimpleName()
    {
        File file = new File(sourceFile);
        return file.getName();
    }

    public void error(int pos,int line, String msg)
    {
        context.errors++;

        int pos1 = StringHelper.getNewLineCharPosForward (sourceCode,pos)+1;
        int pos2 = StringHelper.getNewLineCharPosBackward(sourceCode,pos);
        String lineCode = sourceCode.substring(pos1,pos2);
        int col = pos-pos1+1;

        StringBuilder buff = new StringBuilder();
        buff.append(sourceFile);
        buff.append(" ");
        buff.append(line+"行 " );
        buff.append(col+"列 : ");
        buff.append(msg);
        buff.append("\n"+ lineCode +"\n");
        for(int i=0;i<col;i++)
        {
            char ch = lineCode.charAt(i);
            if(ch=='\t')
                buff.append(ch);
            else
                buff.append(" ");
        }
        buff.append("^\n");
        response(buff.toString());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (!(other instanceof SourceLog))
            return false;

        SourceLog o = (SourceLog) other;
        return sourceFile.equals(o.sourceFile);
    }
}
