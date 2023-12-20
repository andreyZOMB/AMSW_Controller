package com.example.android_app.modules;

import com.example.android_app.adittional.Helper;

public class Condition {
    public int port;
    public int group;
    public int dataType;
    public boolean more;
    public int value;
    public Condition(String input){
        String[] strs = input.split("_");
        port = Integer.parseInt(strs[0]);
        group = Integer.parseInt(strs[1]);
        dataType = Integer.parseInt(strs[2]);
        more = Helper.Parse(strs[3]);
        value = Integer.parseInt(strs[4]);
    }
    public String Serialize(){
        return port+" "+group+" "+dataType+" "+more+" "+value;
    }
}
