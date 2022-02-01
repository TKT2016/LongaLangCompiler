package tools.runs;

/* 运行参数设置 */
public class ClassRunArg {
    public byte[] bytes; //二进制数据
    public String className; //类全名称
    public String method; //方法名称
    public Class<?>[] argTypes; //方法的参数类型数组
    public Object[] args; //传递的参数值数组

    public boolean checkBytes; //是否检查class文件正确性
    public String[] classPaths; //需要包的classPath
}
