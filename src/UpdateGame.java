import java.io.Serializable;
import java.util.ArrayList;


public class UpdateGame implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -790245573695476365L;
	private ArrayList<PlayerInfo> players;
	
	public UpdateGame(ArrayList<PlayerInfo> arr){
		players = arr;
	}

	public ArrayList<PlayerInfo> getPlayers() {
		return players;
	}
	
	public int searchByUsername(String username){
		for (PlayerInfo player : players){
			if (player.getName().equals(username))
				return players.indexOf(player);
		}
		return -1;
	}
	
	public void removePlayer(String playerName){
		for (PlayerInfo player : players)
			if (player.getName().equals(playerName)){
				players.remove(player);
				return;
			}
	}
}
