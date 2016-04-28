package p4.game;

import java.util.Random;

public class Monster {
	Random r = new Random();
	
	private int direction;
	private int closeness;
	private boolean alive = false;
	
	public Monster() {
		
		direction = r.nextInt(360);
		closeness = 5;
		alive = true;
	}

	public int getDirection(){
		return direction;
	}
	
	public int getCloseness(){
		return closeness;
	}	
	
	public int getStatus(){
		if (alive == true)
				return 1;
		else
				return 0;
	}
	
	public void kill(){
		alive = false;
	}

	public void newMonster() {
		direction = r.nextInt(360);
		closeness = 5;	
		alive = true;
	}

}
