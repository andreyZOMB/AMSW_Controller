package com.example.android_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_app.MainActivity;
import com.example.android_app.R;
import com.example.android_app.adittional.AvailableData;
import com.example.android_app.adittional.ModuleType;
import com.example.android_app.modules.ActiveLoad;
import com.example.android_app.modules.AnalogSensor;
import com.example.android_app.modules.DTHSensor;
import com.example.android_app.modules.Module;

import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {
    MainActivity main;
    Context context;
    List<Module> modules;
    public ModuleAdapter(Context context, List<Module> modules, MainActivity main) {
        this.context = context;
        this.modules = modules;
        this.main = main;
    }

    public int getItemViewType(int position) {
        if (modules.get(position) instanceof DTHSensor) {
            return ModuleType.DTH_SENSOR;
        } else if (modules.get(position) instanceof AnalogSensor) {
            return ModuleType.ANALOG_SENSOR;
        } else if (modules.get(position) instanceof ActiveLoad) {
            return ModuleType.ACTIVE_LOAD;
        } else {
            return -1;
        }
    }
    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ModuleType.DTH_SENSOR) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dht_sensor_item, parent, false);
            return new DTHSensorViewHolder(view);
        } else if (viewType == ModuleType.ANALOG_SENSOR) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.analog_sensor_item, parent, false);
            return new AnalogSensorViewHolder(view);
        } else if (viewType == ModuleType.ACTIVE_LOAD) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.active_load_item, parent, false);
            return new ActiveLoadViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.module_item, parent, false);
            return new ModuleViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        holder.module = modules.get(position);
        if(holder instanceof DTHSensorViewHolder){
            ((DTHSensorViewHolder)holder).temperatureField.setText(((DTHSensor)holder.module).temperature+"");
            ((DTHSensorViewHolder)holder).humidityField.setText(((DTHSensor)holder.module).humidity+"");
        }else if(holder instanceof ActiveLoadViewHolder){
            ((ActiveLoadViewHolder) holder).UpdateImages();
            ((ActiveLoadViewHolder) holder).conditionPort.setText(((ActiveLoad)holder.module).condition.port+"");
            ((ActiveLoadViewHolder) holder).conditionGroup.setText(((ActiveLoad)holder.module).condition.group+"");
            ((ActiveLoadViewHolder) holder).conditionValue.setText(((ActiveLoad)holder.module).condition.value+"");
            System.out.println(((ActiveLoad)holder.module).condition.dataType);
            ((ActiveLoadViewHolder) holder).spinner.setSelection(((ActiveLoad)holder.module).condition.dataType);
        }else if(holder instanceof AnalogSensorViewHolder){
            ((AnalogSensorViewHolder)holder).valueField.setText(((AnalogSensor)holder.module).analogData+"");
        }
        holder.portField.setText(Integer.toString(holder.module.port));
        holder.groupField.setText(Integer.toString(holder.module.group));
        holder.nameField.setText(holder.module.name);
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    public class ModuleViewHolder extends RecyclerView.ViewHolder {
        Module module;
        TextView portField;
        TextView groupField;
        EditText nameField;
        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            portField = itemView.findViewById(R.id.modulePort);
            groupField = itemView.findViewById(R.id.moduleGroup);
            nameField = itemView.findViewById(R.id.moduleName);
            nameField.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    module.name = s.toString();
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }
    }
    public class DTHSensorViewHolder extends ModuleViewHolder{

        public TextView temperatureField;
        public TextView humidityField;
        public DTHSensorViewHolder(@NonNull View itemView) {
            super(itemView);
            temperatureField = itemView.findViewById(R.id.temperatureField);
            humidityField = itemView.findViewById(R.id.humidityField);
        }
    }
    public class AnalogSensorViewHolder extends ModuleViewHolder{
        public TextView valueField;
        public AnalogSensorViewHolder(@NonNull View itemView) {
            super(itemView);
            valueField = itemView.findViewById(R.id.valueField);
        }
    }
    public class ActiveLoadViewHolder extends ModuleViewHolder {
        public ImageButton onOff;
        public ImageButton coldHot;
        public ImageButton lessMore;
        public TextView stateField;
        public TextView conditionPort;
        public TextView conditionGroup;
        public TextView conditionValue;
        public Spinner spinner;
        String[] dataTypes = { "Wrong port/group"};
        public void UpdateImages(){
            if (((ActiveLoad) module).state) {
                onOff.setImageResource(R.drawable.on);
                stateField.setText("ON");
            }
            else{
                onOff.setImageResource(R.drawable.off);
                stateField.setText("OFF");
            }
            if (((ActiveLoad) module).hot) {
                coldHot.setImageResource(R.drawable.hot);
            }
            else{
                coldHot.setImageResource(R.drawable.cold);
            }
            if (((ActiveLoad) module).condition.more) {
                lessMore.setRotationY(180);
            }
            else{
                lessMore.setRotationY(0);
            }
        }
        public void UpdateCondition() {
            Module target = null;
            for (int i = 0; i < modules.size(); i++) {
                if((((ActiveLoad) module).condition.port == modules.get(i).port)&&(((ActiveLoad) module).condition.group == modules.get(i).group)){
                    target = modules.get(i);
                    break;
                }
            }
            if(target!=null){
                if(target instanceof AnalogSensor) dataTypes = AvailableData.ANALOG_SENSOR;
                else if(target instanceof DTHSensor) dataTypes = AvailableData.DTH_SENSOR;
                else dataTypes = new String[]{"No matching module type"};
            }else
                dataTypes = new String[]{"Wrong port/group"};
            spinnerInitializer();
        }
        private void spinnerInitializer() {
            ArrayAdapter<String> adapter = new ArrayAdapter(main, android.R.layout.simple_spinner_item, dataTypes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

        public ActiveLoadViewHolder(@NonNull View itemView) {
            super(itemView);
            onOff = itemView.findViewById(R.id.onOff);
            coldHot = itemView.findViewById(R.id.coldHot);
            lessMore = itemView.findViewById(R.id.lessMore);
            stateField = itemView.findViewById(R.id.stateField);
            conditionPort = itemView.findViewById(R.id.conditionPort);
            conditionGroup = itemView.findViewById(R.id.conditionGroup);
            conditionValue = itemView.findViewById(R.id.conditionValue);
            onOff.setOnClickListener(view -> {
                ((ActiveLoad) module).state = !((ActiveLoad) module).state;
                UpdateImages();
                main.Send(((ActiveLoad) module).OnOff());
            });
            coldHot.setOnClickListener(view -> {
                ((ActiveLoad) module).hot = !((ActiveLoad) module).hot;
                UpdateImages();
            });
            lessMore.setOnClickListener(view -> {
                ((ActiveLoad) module).condition.more = !((ActiveLoad) module).condition.more;
                UpdateImages();
            });
            conditionPort.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    try {
                    ((ActiveLoad) module).condition.port = Integer.parseInt(s.toString());
                    UpdateCondition();
                }
                    catch (NumberFormatException e){}
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
            conditionGroup.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    try {
                        ((ActiveLoad) module).condition.group = Integer.parseInt(s.toString());
                        UpdateCondition();
                    }
                    catch (NumberFormatException e){}
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
            conditionValue.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    try {
                    ((ActiveLoad) module).condition.value = Integer.parseInt(s.toString());
                    }
                    catch (NumberFormatException e){}
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
            spinner = itemView.findViewById(R.id.dataType);
            spinnerInitializer();
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                    ((ActiveLoad)module).condition.dataType = spinner.getSelectedItemPosition();
                }
                public void onNothingSelected(AdapterView<?> parent) {
                    ((ActiveLoad)module).condition.dataType = -1;
                }
            });
        }


    }
}
