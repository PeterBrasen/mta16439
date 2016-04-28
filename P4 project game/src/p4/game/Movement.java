package p4.game;

public class Movement extends Thread {
	
	private int sleepTime;
	private int closeness;
	private int heartRate;
	private boolean heartRateSwitch = false;
	
	
	public Movement(int closeness){
		this.closeness = closeness;
	}
	
	public int getCloseness(){
		return closeness;
	}
	
	public void setClosness(int closeness){
		this.closeness = closeness;
	}
	
	public void heartRateSwitch(boolean heartRateSwitch){
		this.heartRateSwitch = heartRateSwitch;
	}
	
	public void setHeartRate (int heartRate){
		this.heartRate = heartRate;
	}
	
	public void run(){
		do{
			sleepTime = 1000+(heartRate*10);
		
			if (heartRateSwitch == false){	
				System.out.println("Closeness: " + closeness);
				if (closeness == 0){
					System.out.println("You died. Eaten by a Dna altering virus expriment (D.a.v.e.)");
				}
				try {
					sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}			
				closeness--;
			} else if (heartRateSwitch == true){
			//High heart rate:
				if (heartRate > 80){ //Find a proper heart rate threshold		
					System.out.println("Closeness: " + closeness + ". sleep time: " + sleepTime);
					if (closeness == 0){
						System.out.println("You died. Eaten by a Dna alteret virus expriment (D.a.v.e.)");
					}
					try {
						sleep(sleepTime - 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
					closeness--;
			//Low heart rate:
				} else if (heartRate <= 80){ //Find a proper heart rate threshold	
					System.out.println("Closeness: " + closeness);
					if (closeness == 0){
						System.out.println("You died. Eaten by a Dna alteret virus expriment (D.a.v.e.)");
					}
					try {
						sleep(sleepTime + 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					closeness--;
				}
			}
		
		}while(true);
	}
}
