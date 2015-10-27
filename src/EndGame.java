import java.io.Serializable;
import java.util.ArrayList;


public class EndGame extends UpdateGame implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6447609504407735824L;
	private String winnerName;
	private String winnerFormula;
	
	public EndGame(ArrayList<PlayerInfo> arr, String name, String formula){
		super(arr);
		winnerName = name;
		winnerFormula = formula;
	}

	public String getWinnerName() {
		return winnerName;
	}

	public String getWinnerFormula() {
		return winnerFormula;
	}

}
