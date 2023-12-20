package com.example.android_app;

import static java.lang.Integer.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android_app.adapter.ModuleAdapter;
import com.example.android_app.adittional.Helper;
import com.example.android_app.adittional.StatusData;
import com.example.android_app.modules.ActiveLoad;
import com.example.android_app.modules.AnalogSensor;
import com.example.android_app.modules.DTHSensor;
import com.example.android_app.modules.Module;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {
    static OkHttpClient client = new OkHttpClient();
    //static TextView outputField;
    //TextView inputField;
    int status = 0;
    TypedArray statusImages;
    List<Module> summaryData = new ArrayList<>();
    RecyclerView moduleRecycler;
    ModuleAdapter moduleAdapter;
    TextView statusField;
    ImageButton statusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Send("GetData");
        setContentView(R.layout.activity_main);
        /*outputField = findViewById(R.id.output);
        inputField = findViewById(R.id.input);
        outputField.setText("Started");
        Button sendBut = findViewById(R.id.button);
        sendBut.setOnClickListener(view -> {
            Send(inputField.getText().toString());
            if (responseData != null)
                outputField.setText(responseData);
        });*/
        ImageButton upload = findViewById(R.id.uploadButton);
        upload.setOnClickListener(view -> {
            Send(prepareDataSet());
        });
        ImageButton update = findViewById(R.id.updateButton);
        update.setOnClickListener(view -> {
            Send("GetData");
        });
        ImageButton reset = findViewById(R.id.reset);
        reset.setOnClickListener(view -> {
            Send("ResetArduino");
        });
        reset.setOnLongClickListener(view -> {
            Send("ResetAll");
            return true;
        });
        statusField = findViewById(R.id.statusField);
        statusButton = findViewById(R.id.statusButton);
        statusImages = getResources().obtainTypedArray(R.array.status_images);
        statusButton.setOnClickListener(view -> {
            status = 1;
            updateStatus();
            Send("GetStatus");
        });
    }
    public void setModuleRecycler(List<Module> moduleList){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        moduleRecycler = findViewById(R.id.moduleResucler);
        moduleRecycler.setLayoutManager(layoutManager);
        moduleAdapter = new ModuleAdapter(this,moduleList,this);
        moduleRecycler.setAdapter(moduleAdapter);
    }
    public String prepareDataSet(){
        String rez = "DataSet;";
        rez+=prepareTimestamp();
        for (int i = 0; i < summaryData.size();i++){
            rez += ";" + summaryData.get(i).Serialize();
        }
        return rez;
    }
    public String prepareTimestamp(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getSeconds()+":"
                +timestamp.getMinutes()+":"
                +timestamp.getHours()+":"
                +timestamp.getDay()+":"
                +timestamp.getMonth()+":"
                +timestamp.getYear();
    }
    public void updateStatus(){
        statusButton.setImageResource(statusImages.getResourceId(status,0));
        statusField.setText(StatusData.statusNames[status]);
    }
    public void Send(String data) {
        RequestBody formBody = new FormBody.Builder()
                .add("data", data)
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.169.169:13131/")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                final String myResponse = e.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //outputField.setText(myResponse);
                        status = 0;
                        updateStatus();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //outputField.setText(myResponse);
                            String[] local1 = myResponse.split(";");
                            System.out.println(local1[0]);
                            if(local1[0].equals("DataSet")){
                                status = parseInt(local1[1]);
                                summaryData.clear();
                                System.out.println(local1[0]);
                                for(int i = 2; i<local1.length;i++){
                                    System.out.println(local1[i]);
                                    String[] local2 = local1[i].split(" ");
                                    if(local2[0].equals("Module"))
                                        summaryData.add(new Module(local2[1], parseInt(local2[2]), parseInt(local2[3])));
                                    else if(local2[0].equals("AnalogSensor"))
                                        summaryData.add(new AnalogSensor(local2[1], parseInt(local2[2]), parseInt(local2[3]), parseInt(local2[4])));
                                    else if(local2[0].equals("DHT_Sensor"))
                                        summaryData.add(new DTHSensor(local2[1], parseInt(local2[2]), parseInt(local2[3]),Float.parseFloat(local2[4]),Float.parseFloat(local2[5])));
                                    else if(local2[0].equals("ActiveLoad"))
                                        summaryData.add(new ActiveLoad(local2[1], parseInt(local2[2]), parseInt(local2[3]), Helper.Parse(local2[4]),Helper.Parse(local2[5]),local2[6]));
                                }
                                setModuleRecycler(summaryData);
                            }
                            else if (local1[0].equals("Status")){
                                status = parseInt(local1[1]);
                            }
                            else {
                                status = 0;
                            }
                            updateStatus();
                        }
                    });
                }
            }
        });
    }
}