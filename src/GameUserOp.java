import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Interface for defining remote classes fetched by RMI
 * @author User
 */
public interface GameUserOp extends Remote {
	/**
	 * Login function with login name and password on server side
	 * @param username Login name
	 * @param password Password
	 * @return Status of whether login is successful. 0 means successful, otherwise fail.
	 * @throws RemoteException Remote exception occurs
	 * @throws SQLException SQL Exception
	 */
    int login(String username, String password) throws RemoteException, SQLException;
    /**
	 * Register function with login name and password on server side
	 * @param username Login name
	 * @param password Password
	 * @return Status of whether register is successful. 0 means successful, otherwise fail.
	 * @throws RemoteException Remote exception occurs
     * @throws SQLException SQL Exception
	 */
    int register(String username, String password) throws RemoteException, SQLException;
    /**
	 * Logout function with login name on server side
	 * @param username Login name
	 * @return Status of whether logout is successful. 0 means successful, otherwise fail.
	 * @throws RemoteException Remote exception occurs
     * @throws SQLException SQL Exception
	 */
    int logout(String username, ArrayList<PlayerInfo> players) throws RemoteException, SQLException;
    /**
	 * Get user profile with login name on server side
	 * @param username Login name
	 * @return Status of whether logout is successful. 0 means successful, otherwise fail.
	 * @throws RemoteException Remote exception occurs
     * @throws SQLException SQL Exception
	 */
    String [] [] userProfile(String username, ArrayList<PlayerInfo> players) throws RemoteException, SQLException;
    /**
	 * Get user profile with login name on server side
	 * @param username Login name
	 * @return Status of whether logout is successful. 0 means successful, otherwise fail.
	 * @throws RemoteException Remote exception occurs
     * @throws SQLException SQL Exception
	 */
    String [] [] leaderBoard(String username, ArrayList<PlayerInfo> players) throws RemoteException, SQLException;
    void playGame(String username, ArrayList<PlayerInfo> players) throws RemoteException;
    /**
     * Compare whether he wins the game.
     * @param newGame the round of game corresponding to the player
     * @param answer answer of the user input
     * @return whether he wins the game
     * @throws RemoteException Remote exception occurs
     */
    boolean compareWin(String username, NewGame newGame, String answer, long winTime) throws SQLException, RemoteException;
}
