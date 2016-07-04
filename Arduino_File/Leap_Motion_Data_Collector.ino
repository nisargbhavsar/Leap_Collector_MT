//inPin connected to a data pin on the parallel port.
int inPin = 12;
int led = 13; //For testing purposes
int count = 1; //Even pulses start the trial data collection, odd pulses stop trial data collection

//Current logic state. 0 is LOW(Ideal of 0V) and 1 is HIGH (Ideal of 5V).
//Assuming that the logic level has been set to LOW before start of first trial
int logicState = 1; //Lab computer has pins at 3.40V by default
int prev_logicState = 1;


#define DEBUG 1 // change to 1 to debug 


void setup()
{
  pinMode (inPin,INPUT); //Initialize pin# 12 for input
 
  //Initialize the built-in LED (assuming the Arduino board has one)
  pinMode(led, OUTPUT);
  //Start a serial connection at a baud rate of 38,400.
  Serial.begin(38400);
}


void loop()
{  
  //digitalWrite(led, LOW);
  
  
    digitalWrite(led, HIGH);
    //if (pulseIn(inPin, LOW)>0)
    //Depending on which pulse computer sends in: When 3.4V->0V 0V->3.4V use LOW, When 0V->3.4V 3.4V->0V use HIGH  
    logicState = digitalRead(12);
    
    if(logicState ==HIGH)
    {
     logicState=1;
    }
    else
      logicState=0;
    if (logicState != prev_logicState)
    {
      if(count%2==0)
      {
        pinMode(8, LOW);
      digitalWrite(led,LOW);  
      Serial.write(1);
      pinMode(8, HIGH);
      delay(10);
      }
      pinMode(8,prev_logicState);
      prev_logicState=logicState;
      count++;
    }
  
} 
   
//  pinMode(led, HIGH);
//  delay(2000);
//  Serial.write(1);
//  delay(10000);
//  pinMode(led, LOW);
//  Serial.write(1);
//  delay(5000);
// if(DEBUG ==1)
//      digitalWrite(led, LOW);
//  while(Serial.available())
//  {
//    if (pulseIn(inPin, LOW)>0)
//    {
//    digitalWrite(led,HIGH);
//      if(count%2==0)//Start the data collection
//      {
//        if(DEBUG ==1)
//          digitalWrite(led, HIGH);
//        logicState=1;
//      }
//      else if(count%2==1)//Stop the data collection
//      {
//        if(DEBUG ==1)
//          digitalWrite(led, LOW);
//        logicState=0;
//      }
//    count+=1;
//    }
//    
//   
//   
//   if(prev_logicState != logicState)
//   {
//     if (logicState == 1)
//     {
//        Serial.println("Leap Motion Started !!");
//        //Make Leap Motion start recording
//        Serial.write(2);
//     }
//     else if (logicState == 0)
//     {
//        Serial.println("Leap Motion Ended !!");
//        //Make Leap Motion stop recording
//        Serial.write(1);
//     }
//     prev_logicState = logicState;
//   }
//}

//} 
////Get the current system time in milliseconds.
//unsigned long currentTime = millis();
////Check if it's time to toggle the LED on or off.
//if (currentTime - previousTime >= blinkRate)
//{
//   previousTime = currentTime;
//  
//   if (ledState == LOW) ledState = HIGH;
//   else ledState = LOW;
//  
//   digitalWrite(led, ledState);
//}
////Check if there is serial data available.
//if (Serial.available())
//{
//   //Wait for all data to arrive.
//   delay(20);
//  
//   //Our data.
//   String data = "";
//  
//   //Iterate over all of the available data and compound it into 
//      a string.
//   while (Serial.available())
//     data += (char) (Serial.read());
//  
//   //Set the blink rate based on our newly-read data.
//   blinkRate = abs(data.toInt() * 2);
//  
//   //A blink rate lower than 30 milliseconds won't really be 
//      perceptable by a human.
//   if (blinkRate < 30) blinkRate = 30;
//  
//   //Echo the data.
//   Serial.println("Leapduino Client Received:");
//   Serial.println("Raw Leap Data: " + data + " | Blink Rate (MS): 
//      " + blinkRate);
//}
