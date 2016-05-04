#include <Wire.h>
//#include <SoftwareSerial.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BNO055.h>
#include <utility/imumaths.h>

// Volatile Variables, used in the interrupt service routine!
volatile int BPM;                   // int that holds raw Analog in 0. updated every 2mS
volatile int Signal;                // holds the incoming raw data
volatile int IBI = 600;             // int that holds the time interval between beats! Must be seeded!
volatile boolean Pulse = false;     // "True" when User's live heartbeat is detected. "False" when not a "live beat".
volatile boolean QS = false;        // becomes true when Arduoino finds a beat.
static boolean serialVisual = true;  
boolean knapTryk = false;
int pulsePin = 0;
int ledPin = 13;
int state = 0;
int flag = 0;
int buttonPin = 8;
int buttonState = 0;

#define BNO055_SAMPLERATE_DELAY_MS (100) // BNO Activation code.
Adafruit_BNO055 bno = Adafruit_BNO055(55); 
void displaySensorDetails(void){
  sensor_t sensor;
  bno.getSensor(&sensor);
  delay(500);
}


void setup() {
  Serial.begin(9600); // Baud-rate for Bluetooth connection.
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);
  pinMode(buttonPin, INPUT);
  interruptSetup();
   if(!bno.begin())
  {
    // There was a problem detecting the BNO055 ... check your connections 
    Serial.print("Ooops, no BNO055 detected ... Check your wiring or I2C ADDR!");
    while(1);
  }
  delay(1000);
  displaySensorDetails();
}
void loop() {
  serialOutput() ;
  if (QS == true) {
    serialOutputWhenBeatHappens();   // A Beat Happened, Output that to serial.
    QS = false;                      // reset the Quantified Self flag for next time
  }
  sensors_event_t event;
  bno.getEvent(&event);
  Serial.print("A ");   //Printing the heading
  Serial.print((int)event.orientation.x);
  Serial.print(" ");
  delay(BNO055_SAMPLERATE_DELAY_MS);

  
  buttonState = digitalRead(buttonPin);
  if (buttonState == HIGH && knapTryk == false) {
    digitalWrite(ledPin, HIGH);
   Serial.print("C "); //Printing the Click
    knapTryk = true;
  } else if (buttonState == LOW) {
    digitalWrite(ledPin, LOW);
    knapTryk = false;
  }
  if (Serial.available() > 0) {
    state = Serial.read();
    flag = 0;
  }
  if (state == '0') {
    digitalWrite(ledPin, LOW);
    if (flag == 0) {
      Serial.println("LED: off");
      flag = 1;
    }
  }
  else if (state == '1') {
    digitalWrite(ledPin, HIGH);
    if (flag == 0) {
      Serial.println("LED: on");
      flag = 1;
    }
  }
}
