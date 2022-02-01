package longa.domains.sql.models;

public class ConnectionModel {

    public String host;

    public int port;

    public String database;

    public String user;

    public String password;

    public String toUrl()
    {
        String str= "jdbc:mysql://"+ host+":"+port+"/"+ database+"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        return str;
    }
}
