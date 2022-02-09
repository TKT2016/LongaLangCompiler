# LongaLangCompiler
Longa 是我发明的一种长链表达式调用技术。  
Longa项目是我在自己开发的简单java编译器上的基础上插入Long编译技术。  

## 运行环境
JDK 1.8
IntelliJ IDEA

##运行方法
打开IntelliJ IDEA，运行src/editor.TinyEditor类，  
点击工具栏按钮"Open Longa Source",选择samples下的 HelloWorld.lga，  
点击"Complie Run Longa"，IntelliJ IDEA的输出窗口会运行输出"Hello World...".  

##例子说明
目录samples下有三个例子  
Longa暂时是为了研究演示,没有关键字static和public，编辑器会在编译成功后自动调用void main()方法  
HelloWorld.lga 最简单的HelloWorld,和java很接近  
GUIDSLTest.lga 演示Longa是怎样编写java swing图形程序的  
SQLDomainTest.lga 演示怎样用Longa连接mysql,删除创建查询表

删除创建查询表
```java
void sql_table_WebSite()
{
         /*
          删除表websites如果它存在(drop table 'websites')
          也可以用 drop table tbWebSite,但是如果没有表websites会运行出错
          */
        drop table tbWebSite ifExists;

        /* 创建表websites (create table 'websites') */
        create table tbWebSite [
        column "name" dataType varchar 255 NotNULL comment "站点名称", //定义列,包括名称、数据类型、长度、备注
        ...
        ];

        /* 向表中插入测试数据 ( insert demo data into table 'websites' ) */
        insert into tbWebSite values ["1", "Google", "https://www.google.cm/", "1", "USA"] ;
        /* 运行select查询语句 ( ececute sql  SELECT ) */
        CachedRowSetImpl rs2 = select "*" from tbWebSite;
        DbDataHelper.dump(System.out,rs2); //显示查询结果
}
```
创建登录表单
```java
    void loginWindow()
    {
        Font font1 = new Font("Georgia", Font.PLAIN, 16);
        JFrame jframe = frame
                title "Login Window"
                size 350 200
                component (
                        panel noneLayout
                            component (label "User:" bounds 10 20 80 25 font font1)
                            component (textField bounds 100 20 165 25)
                            component (label "Password:" bounds 10 50 80 25)
                            component (textField bounds 100 50 165 25)
                            component (button "Login" bounds 90 90 80 25)
                )
        ;
    }
```

PhysicsPapers目录下是我写的一些物理学观点，与本项目完全无关，放在此是为了传播本人观点。  

