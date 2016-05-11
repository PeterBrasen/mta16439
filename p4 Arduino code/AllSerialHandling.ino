void serialOutput(){   // Decide How To Output Serial. 
      sendDataToSerial('S', Signal);     // goes to sendDataToSerial function     
}
//  Decides How To OutPut BPM and IBI Data
void serialOutputWhenBeatHappens(){    
    //Serial.print("*** Heart-Beat Happened *** ");  //ASCII Art Madness
    Serial.print("B ");
    Serial.print(BPM);
    Serial.print(" "); 
}
//  Sends Data to Pulse Sensor Processing App, Native Mac App, or Third-party Serial Readers. 
void sendDataToSerial(char symbol, int data ){              
}
