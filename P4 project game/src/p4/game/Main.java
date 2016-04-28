package p4.game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import jssc.*;

import java.util.Random;
import java.util.Date;

public class Main {
		
		//For vairable change in the game
		static boolean heartRateChoice = true; //There to either switch heart rate on or off
		static int magazine = 12; //There to determine how many bullets the weapon have in a magazine
		static int endKills = 15; //Sets how many kills the player max can get before the game ends
	
	 	//For controls
		static int controllerDirection; //Get controller direction here
		static int headDirection; //Get direction of user's head here
		static int heartRate; //Get send heart rate input here
		static int hitbox = 0; //Helps make the target bigger as it gets close		
		static int bullets = magazine; //How many shots the player have		
		static int monsterKills = 0; //Keeps count of how many monsters there have been killed
		static int shoots = 0; //There to keep count of how many shoots the player have shot
		static int reloadTime; //How long it will take to reload the weapon
		static int tempCloseness = 0; //There to see if the previous closeness is the same as the current one
		static boolean knap = false; //Set knap to false, as in the trigger on the controller is not pressed		
		static boolean reloading = false; //Here so that the player can not fire whilst reloading
		
		//For printing to text file
		static String hit; //Write if the shot was a hit or miss
		static String closeness; //Writes the distance of the monster from the player
		static String pDirection; //Writes the player direction
		static String mDirection; //Writes the monsters direction
		static String newMonster = "--- New Monster ---"; //Writes in the file when a new monster is spawned 
		static String dead = "You died. Eaten by Dna altering virus expriment (D.a.v.e.)"; //For when the monster reach the player
		static String win = "You (didn't) died. (not) Eaten by Dna altering virus expriment (D.a.v.e.)";
		static String reloadingText = "--- Reloading ---"; //Writes  when reloading begins
		static String reloadingDone = "--- Reloading Done ---"; //Writes when reloading ends
 		static String bulletsLeft; //Writes how many bullets are left in the chamber after each shot	
		
 		//For sounds
 		static File shoot = new File ("C:/Users/Mathias/workspace/P4 project game/sounds/barreta_m9.wav"); //Gun sound (Beretta m9) from Freesound.org
 		static File load = new File ("C:/Users/Mathias/workspace/P4 project game/sounds/gun-reloading.wav"); //Gun loading sound (Freesound.org)
 		static File kill = new File ("C:/Users/Mathias/workspace/P4 project game/sounds/fall5.wav"); //Sound of monster falling
 		
 		//Variables for timer
 		static int startTime = 0;
 		static int stopTime = 0;
 		static int endTime = 0;
 		
 		//Variables for connecting to Arduino
 		static SerialPort serialPort = new SerialPort("COM79");// BT: COM79; USB: COM74;
 	    static String message = null;
 	    static String data = "0";
 	    static String shootName = "C";
 	    static String headingName = "A";
 	    static String beatsName = "B";
 	    static int readByte = 17;
 	    
 	    //Variables for connecting to Pure Data
 	    static InetAddress remoteIP;
 	    static int remotePort = 1234;
 	    static OSCPortOut sender;
 	    static String address1 = "/distance";
 	    static String address2 = "/heading";
 	    static Object values1[] = new Object[1];
 	    static Object values2[] = new Object[1];
 	    static int headingV, beatsV, monsterHeading;
 	    static Random r = new Random(); // Just for while there is no compass variable from Arduino
 	    
 	private static void dataSplit(String data, String vName){
 	    	int newData = 0;
 	    	
 	    	String dataArr[] = data.split(" ");
 	    	if (vName.equals(shootName)){
 	    		for(int i = 0; i < (dataArr.length-1); i++){
 	        		if (dataArr[i].equals(vName)){
 	        			knap = true;
 	        			break;
 	        		}
 	        	}
 	    	} else {
 	    		for(int i = 0; i < (dataArr.length-1); i++){
 	        		if (dataArr[i].equals(vName)){
 	        			newData = Integer.parseInt(dataArr[i+1]);
 	        			break;
 	        		}
 	        	}
 	        	dataArr = null;
 	        	if (newData != 0) {
 	        		if (vName.equals(headingName)){
 	        			headingV = newData;
 	        			//System.out.print("new heading ");
 	        		} else if (vName.equals(beatsName)){
 	        			beatsV = newData;
 	        			heartRate = newData;
 	        		} 
 	        	}
 	    	}
 	    }
 		
 	static void toText(BufferedWriter bw, String input) throws IOException{ //A method to write to text file
 	   	if (input.equals("QUIT")){ //Checks if the input is 'QUIT' 
 			bw.close(); //Closes the stream to the text file 
 	   	} else { //If the input is not
 	   		bw.write(input); //Write the input to the text file 
 			bw.newLine(); //New line
 	   	}
 	}	
 	
 	static void playSound(String choice){ //Method to play the different sound used on the Java side of the project. Takes a String as only input
    	try{
    		Clip clip = AudioSystem.getClip(); //Creates a Clip object
    		if (choice.equals("gun")){ //Checks if the input is equal to the word "gun"
				clip.open(AudioSystem.getAudioInputStream(shoot)); //Opens the file shoot
    		} else if (choice.equals("reload")){ //Checks if the input is equal to the word "reload"
				clip.open(AudioSystem.getAudioInputStream(load)); //Opens the file load
				reloadTime = (int) ((clip.getMicrosecondLength()/1000)-1000); //Sets the sleep time, used in reloading, to the length of the sound clip
    		} else if (choice.equals("kill")){ //Checks if the input is equal to the word "kill"
				clip.open(AudioSystem.getAudioInputStream(kill)); //Opens the file kill
    		}
    		clip.start(); //Player clip, which one is chosen by the input
    	}catch (LineUnavailableException | IOException | UnsupportedAudioFileException e){
			System.out.println("You make me sad. Goodday, sir. I SAID GOODDAY!"); //If an exception should be thrown, this will be written in the console
		}
	}
 	
 	static void timer (String choice){
    	Date time = new Date(); //Creates a new data object every time method is called, as to update the time
		if (choice.equals("start")){ //Checks if the String input is start
			startTime = (int) time.getTime(); //The variable startTime is given a value (the time)
		} else if (choice.equals("stop")){
			stopTime = (int) time.getTime();
		} else if (choice.equals("end")){
			endTime = (stopTime - startTime)/1000; //End time is being calculated and turned into seconds from milliseconds
		} else if (choice.equals("NewMonster")){ 
			timer("stop"); //Calls it self with a new input
			timer("end");
			timer("start");
		}
	}
 	
	public static void main(String[] args) throws IOException, SerialPortException, UnknownHostException, SocketException, java.io.IOException, InterruptedException {
		remoteIP = InetAddress.getLocalHost();
		sender = new OSCPortOut(remoteIP, remotePort);
		
		try {
			System.out.println("Trying to open port");
			serialPort.openPort();//Open serial port
	    	System.out.println("Port opened");
	        serialPort.setParams(9600, 8, 1, 0);//Set params.
	        System.out.println("Parameters set");
		} catch (SerialPortException ex) {
	        System.out.println(ex);
	        serialPort.closePort();
	        System.exit(1);
	    }
		Monster dave = new Monster(); //Create a object of Monster
		Movement movement = new Movement(dave.getCloseness()); //Create a object of Monster
		
		serialPort.addEventListener(new SerialPortReader());
		
		File file = new File("C:/Users/Mathias/Desktop/test.txt"); //Creates a File object
		if (!file.exists()) { //If there is not a file named test.txt in the location given to the object
			file.createNewFile(); //Create a file named test.txt in the location
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile()); //Creates a file writer object, with a File object as input
		BufferedWriter bw = new BufferedWriter(fw); //BufferedWriter object with FileWriter as input
		
		movement.heartRateSwitch(heartRateChoice); //Choose to have use the heart rate or not (false = not in use || true = in use)
		movement.start(); //start the runnable method in Movement
		serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
		timer("start"); //starts the timer before the loop
		do{			
			
			//For updating heart rate
			if (heartRateChoice == true){ //If the game are to use the heart rate as an input
				movement.setHeartRate(heartRate); //Add the heart rate to the movement object
			}
						
			//Writes the distance to the text file
			if (!(movement.getCloseness() == tempCloseness)){ //Check to see that movement's closeness is NOT the same as tempcloseness
				tempCloseness = movement.getCloseness(); //Makes tempcloseness the same as the current closeness of the monster
				closeness = "Closeness: " + String.valueOf(movement.getCloseness()); //Updates the text that is to be written to the text file 
				bw.newLine(); //For making the layout of the text file easier to overlook
				toText(bw, closeness);  //Calls the toText method with two inputs (a BufferedWriter and a String input)
				bw.newLine(); //For making the layout of the text file easier to overlook
			}
			
			//Used to make it easier to hit the monster the closer it gets (like in real life where the target gets bigger the closer it is)
			if (movement.getCloseness() == 5) //If the monster is a distance of 5 away
				hitbox = 5; //The player have to hit within monster direction +/- 5
			else if (movement.getCloseness() == 4) 
				hitbox = 9;
			else if (movement.getCloseness() == 3)
				hitbox = 14;
			else if (movement.getCloseness() == 2)
				hitbox = 18;
			else if (movement.getCloseness() == 1)
				hitbox = 23;
			
			//Get input for knap
			//input from bluetooth (knap)
			if (knap == true && reloading == false){ //If the button is pressed and the player is not reloading
				System.out.println("Monster is: " + dave.getDirection() + "; You are: " + controllerDirection);
				playSound("gun"); //calls the playSound method, with input "gun"
				pDirection = "Player Direction: " + String.valueOf(controllerDirection);
			    toText(bw, pDirection);
			    mDirection = "Monster Direction: " + dave.getDirection();
			    toText(bw, mDirection);
				if (controllerDirection <= dave.getDirection()+hitbox && controllerDirection >= dave.getDirection()-hitbox){ //There to check if the the controller is hitting the monster
					System.out.println("Hit!"); //Writes "Hit" if the monster is hit
					dave.kill(); //Calls the monster kill method
					playSound("kill"); //calls the playSound method, with input "kill"
					hit = "Hit!"; //Sets the String hit to Hit
					timer("NewMonster");
					toText(bw, "Time: " + String.valueOf(endTime));
				} else { //If it was not a hit
					System.out.println("Miss"); //Prints out "Miss" if the player misses the monster
					hit = "Miss!"; //Sets the String hit to Hit
				} 
				shoots++; //Adds one to the shoots variable, which keeps count on how many shoots the user have used
				
				toText(bw, hit);
				
				//Reload function start (Optional)
				if (bullets <= 0){ //If the player have 0 or less bullets left
					playSound("reload"); //calls the playSound method, with input "reload"
		        	toText(bw, reloadingText); 
		        	System.out.println("---- RELOADING ----"); //Prints reloading in the console
		        	reloading = true; //Sets reloading to true
		        	try { //Try function
						Thread.sleep(reloadTime); //Puts the thread into a sleep state for reloadTime milliseconds
					} catch (InterruptedException e1) { 
						e1.printStackTrace();
					}
		        	bullets = magazine; //Sets bullets back to the original size of the magazine
		        	System.out.println("---- RELOADING DONE ----"); //Prints to the console when reloading is finished
		        	toText(bw, reloadingDone); 
		        	serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
					reloading = false; //Sets reloading boolean to false
		        } else { //If there are bullets left in 'the magazine'
		        	bullets--; //Subtract one bullet from 'the magazine'
		        	System.out.println("	Shots left: " + bullets); //Prints to the console how many bullets are left in 'the magazine'
		        	bulletsLeft = "Bullets left: " + String.valueOf(bullets);
		        	toText(bw, bulletsLeft);
		        }
				//Reload function end
				bw.newLine(); //For making the layout of the text file easier to overlook
				knap = false; //Sets knap to false as to not shoot every time the program goes over it again
			}
			
			//send retninger til PD
			if (dave.getStatus() == 1){ //If there is a monster 'alive' these will be the outputs to PD
				//send headDirecion
				//send dave.getDirection()
				//send movement.getCloseness()
			} else if (dave.getStatus() == 0){ //If there is no monsters 'alive', create a new one
				System.out.println("-------------------New monster-------------------"); //Prints to console when new monster is created
				dave.newMonster(); //Call monster newMonster method
				movement.setClosness(dave.getCloseness()); //Call movement setClosness method, with input of monster's closeness				
				monsterKills++; //Adds one to integer variable keeping track of hoe many monsters killed
				toText(bw, newMonster); 
				bw.newLine(); //For making the layout of the text file easier to overlook
				System.out.println("Monster direction: "+dave.getDirection());
			}
			
			if (movement.getCloseness() <= 0  || monsterKills >= endKills){ //Checks if the monster have reached the player or if the player have reached the amount of kills required 
				timer("stop");
				timer("end");
				if ( movement.getCloseness() <= 0){
				     toText(bw, dead);
				     System.out.println(dead);
				    } else if (monsterKills >= endKills){
				     toText(bw, win);
				     System.out.println(win);
				    }
				toText (bw, "Time: " + String.valueOf(endTime));
				toText(bw, "Kills: " + String.valueOf(monsterKills));
			    toText(bw, "Shots shot: " + String.valueOf(shoots));
			    toText(bw, "QUIT");
			    System.exit(0);
			}
			int distancePD = movement.getCloseness();
			monsterHeading = dave.getDirection();
			distancePD = 5-distancePD;
			values1[0] = new Integer(distancePD);
	        OSCMessage message1 = new OSCMessage(address1, values1);
	        sender.send(message1); // Sends distance to Pure Data
			
		}while(true);

	}
	static class SerialPortReader implements SerialPortEventListener {
	    public void serialEvent(SerialPortEvent event) {
	        if(event.isRXCHAR()){//If data is available
	        	
	            if(event.getEventValue() >= readByte){//Check bytes count in the input buffer
	                try {
	        			data = serialPort.readString(readByte);
	        			serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
	        			//controllerDirection = r.nextInt(360);
	        			controllerDirection = Math.round(headingV);
	        			dataSplit(data,headingName);
	        	        dataSplit(data,beatsName);
	        	        dataSplit(data,shootName);

	        	        
	        	        try {
	        	        	int monsterPlayerHeading = (monsterHeading-headingV);
	        	        	if (monsterPlayerHeading >= 360) {monsterPlayerHeading -= 360;}
	        	        	else if (monsterPlayerHeading < 0) {monsterPlayerHeading += 360;}
	        	        	values2[0] = new Integer(monsterPlayerHeading);
		        	        OSCMessage message2 = new OSCMessage(address2, values2);
							sender.send(message2);
						} catch (IOException e) {}
	        	        
	                }
	                catch (SerialPortException ex) {
	                    System.out.println(ex);
	                }
	            }
	        }
	    }// serialEvent end
	} // SerialPortReader end
}