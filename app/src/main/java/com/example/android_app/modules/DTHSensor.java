package com.example.android_app.modules;

public class DTHSensor extends Module{
    public float temperature;
    public float humidity;
    public DTHSensor(String name,int port, int group,float temperature,float humidity) {
        super(name,port, group);
        this.temperature = temperature;
        this.humidity = humidity;
        typeName = "DHT_Sensor";
    }
}
