import java.io.Serializable;


public class PlayerInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5059448543899620960L;
	private String username;
	private int numWin;
	private int gamePlayed;
	private double avg;
	public PlayerInfo(String username){
		this.username = username;
		numWin = 0;
		avg = 0;
	}
	public PlayerInfo(String username, int numWin, double avg){
		this.username = username;
		this.numWin = numWin;
		this.avg = avg;
	}
	public String getName() {
		return username;
	}
	public int getNumWin() {
		return numWin;
	}
	public void setNumWin(int numWin) {
		this.numWin = numWin;
	}
	public int getGamePlayed() {
		return gamePlayed;
	}
	public void setGamePlayed(int gamePlayed) {
		this.gamePlayed = gamePlayed;
	}
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return username;
	}
	
}
