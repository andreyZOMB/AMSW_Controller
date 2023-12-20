#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

/* Установите здесь свои SSID и пароль */
const char* ssid = "AMSW_Prototype";       // SSID
const char* password = "Progress";  // пароль

/* Настройки IP адреса */
IPAddress local_ip(192, 168, 169, 169);
IPAddress gateway(192, 168, 169, 169);
IPAddress subnet(255, 255, 255, 0);

ESP8266WebServer server(13131);
bool waiting_response = 0;
unsigned long time_for_noResponse = 0;
unsigned long waiting_for_respose_time = 2000;
String local_string;

void setup() {
  Serial.begin(115200);

  WiFi.softAP(ssid, password);
  WiFi.softAPConfig(local_ip, gateway, subnet);
  delay(100);

  server.on("/", handle_OnConnect);
  server.onNotFound(handle_NotFound);

  server.begin();
  Serial.println("");
  Serial.println("HTTP server started");
}

void loop() {
  server.handleClient();
  if (waiting_response) {
    if (time_for_noResponse > millis()) {
      if (Serial.available() > 0) {
        server.send(200, "text/plain",Serial.readString());
        waiting_response = 0;
      }
    }else{
      server.send(200, "text/plain","NO_RESPONSE");
    }
  }
}

void handle_OnConnect() {
  if (server.hasArg("data")) {
    time_for_noResponse = millis()+waiting_for_respose_time;
    waiting_response = 1;
    local_string = server.arg("data");
      if(local_string == "ResetArduino"){
        ResetArduino();
      }else if(local_string == "ResetAll"){
        ResetArduino();
        ESP.restart();
      }else{
    Serial.print(local_string);
      }
  } else {
    Serial.println("OnConnect___NO_DATA");
    server.send(200, "text/plain", "OnConnect___NO_DATA");
  }
}

void handle_NotFound() {
  Serial.println("NotFound");
  server.send(404, "text/plain", "Not found");
}
void ResetArduino(){
  pinMode(2,OUTPUT);
  digitalWrite(2,0);
  delay(1000);
  pinMode(2,INPUT);
  digitalWrite(2,1);
}
