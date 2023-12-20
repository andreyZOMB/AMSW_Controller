package com.example.android_app.modules;

public class Module {
    public String name;
    public int group;
    public int port;
    public String typeName = "Module";

    public Module(String name, int port, int group) {
        this.port = port;
        this.name = name;
        this.group = group;
    }

    public String getHeader(){return port+"  "+ group+"  "+name;}
    public String Serialize(){
        return typeName+" "+name+" "+port+" "+group;
    }
}
