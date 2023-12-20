package com.example.android_app.modules;

public class ActiveLoad extends Module{
    public boolean state;
    public boolean hot;
    public Condition condition;
    public ActiveLoad(String name,int port, int group,boolean state,boolean hot,String condition) {
        super(name,port, group);
        this.state = state;
        this.hot = hot;
        this.condition = new Condition(condition);
        typeName = "ActiveLoad";
    }
    public String OnOff(){
        System.out.println(state);
        return "onOff_command;"+port+";"+group+";"+state;
    }
    public String Serialize(){
        return super.Serialize()+" "+hot+" "+condition.Serialize();
    }
}
