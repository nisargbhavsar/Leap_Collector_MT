//inPin connected to a data pin on the parallel port.
int inPin = 12;
int led = 13; //For testing purposes
int count = 1; //Even pulses start the trial data collection, odd pulses stop trial data collection

byte pin2 = 2; // connect to D2

//Current logic state. 0 is LOW(Ideal of 0V) and 1 is HIGH (Ideal of 5V).
int logicState = 1; //Lab computer has pins at 3.40V by default
int prev_logicState = 1;


#define DEBUG 1 // change to 1 to debug 
int i =0;

void setup()
{
  pinMode (inPin,INPUT); //Initialize pin# 12 for input
 
  //Initialize the built-in LED (assuming the Arduino board has one)
  pinMode(led, OUTPUT);
  pinMode(9,OUTPUT);
  pinMode (pin2, OUTPUT); // set pin as an output
digitalWrite (pin2, LOW); // set pin as low output to start
  //Start a serial connection at a baud rate of 38,400.
  Serial.begin(38400);
  
}


void loop()
{  
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
        Serial.write(1);
      }
      prev_logicState=logicState;
      count++;
    }

    if (Serial.available() > 0) {
        // read the incoming byte
        analogWrite(8,255);
        delay(1000);//Delay so we can see the pulse
        analogWrite(8,0);
        int incomingByte =0;
        incomingByte = Serial.read(); //Clear the serial buffer
        } 
} 
  /*Testing Code
    digitalWrite(led, HIGH);
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
        digitalWrite(led,LOW);  
        Serial.write(1);
      }
      prev_logicState=logicState;
      count++;
    }
    //Leap app writes back to serial buffer
    if (Serial.available() > 0) {
        // read the incoming byte
        analogWrite(8,255);
        delay(1000);//Delay so we can see the pulse
        analogWrite(8,0);
        int incomingByte =0;
        incomingByte = Serial.read(); //Clear the serial buffer
        } 
   */
