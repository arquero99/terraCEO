#include <Arduino.h>
#include <credentials.h>
#include <WiFi.h>           // WiFi control for ESP32
#include "ThingsBoard.h"    // ThingsBoard SDK


WiFiClient espClient;
ThingsBoard tb(espClient);
int status = WL_IDLE_STATUS;
bool subscribed = false;

float latitude=0.0;
float longitude=0.0;
float waterPreassure=0.0;
int flowRate=0;
float ph=0.0;
int tds=0;

void printLogs();
void InitWiFi();
void reconnect();
void connectIoT();
void asignarValoresAleatorios();
void sendData();

void setup() {
  srand(time(NULL));
  latitude = 38.78311;
  longitude = -3.04394;
  Serial.begin(9600);
  WiFi.begin(WIFI_SSID,WIFI_PWD);
  InitWiFi();                                 // Init SHT20 Sensor
  delay(1000);
}

void loop() {
connectIoT();
asignarValoresAleatorios();
sendData();
printLogs();
delay(60000);
}


void printLogs(){
    Serial.println();
    Serial.println("\n ---------------------------------------------------------------");
    Serial.print("Latitude: ");
    Serial.print(latitude);
    Serial.print("º");
    Serial.println();
    Serial.print("Longitude: ");
    Serial.print(longitude);
    Serial.print("º");
    Serial.println();
    Serial.print("Water Preassure: ");
    Serial.print(waterPreassure);
    Serial.print("bar");
    Serial.println();
    Serial.print("Flow Rate: ");
    Serial.print(flowRate);
    Serial.print("lpm");
    Serial.println();
    Serial.print("pH: ");
    Serial.print(ph);
    Serial.println();
    Serial.print("TDS: ");
    Serial.print(tds);
    Serial.print("mg/l");
    Serial.println();
}

void InitWiFi()
{
  Serial.println("Connecting to AP ...");

  WiFi.begin(WIFI_SSID, WIFI_PWD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("Connected to AP");
}

void reconnect() 
{
  status = WiFi.status();
  if ( status != WL_CONNECTED) {
    WiFi.begin(WIFI_SSID, WIFI_PWD);
    while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print(".");
    }
    Serial.println("Connected to AP");
  }
}

void connectIoT()
{
 if (WiFi.status() != WL_CONNECTED) {
    reconnect();
    return;
  }
  if (!tb.connected()) {
    subscribed = false;

    // Connect to the ThingsBoard
    Serial.print("Connecting to: ");
    Serial.print(TB_SERVER);
    Serial.print(" with token ");
    Serial.println(TB_DEVICE_TOKEN);
    if (!tb.connect(TB_SERVER, TB_DEVICE_TOKEN)) {
      Serial.println("Failed to connect");
      return;
    }
    else
      Serial.println("Connected!");
  }

}

void sendData()
{
    tb.sendTelemetryFloat("Latitude", latitude);
    tb.sendTelemetryFloat("Longitude", longitude);
    tb.sendTelemetryFloat("WaterPreassure", waterPreassure);
    tb.sendTelemetryInt("FlowRate", flowRate);
    tb.sendTelemetryFloat("pH", ph);
    tb.sendTelemetryInt("TDS", tds);
}

void asignarValoresAleatorios() 
{
    // Inicializar la semilla del generador de números aleatorios
    srand(time(NULL));

    // Asignar valores aleatorios a las variables
    waterPreassure = (float)(rand()) / RAND_MAX * 100.0;
    flowRate = rand() % 100;
    ph = (float)(rand()) / RAND_MAX * 14.0;
    tds = rand() % 1000;
}