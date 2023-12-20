package com.example.android_app.modules;

public class AnalogSensor extends Module{
    public int analogData;
    public AnalogSensor(String name,int port, int group, int value) {
        super(name,port, group);
        analogData = value;
        typeName = "AnalogSensor";
    }
}
