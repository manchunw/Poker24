import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;


/**
 * User profile panel
 * @author User
 *
 */
public class UserProfilePanel extends JPanel {

	private static final long serialVersionUID = -9007592237086532201L;
	private JLabel[] ja = new JLabel[5];
	private GameUserOp gameUserOp;
	private String username;
	private String [][] strArr;
	JPanel th;

	/**
	 * Constructor for user profile panel
	 */
	public UserProfilePanel(GameUserOp guo, String userName){
		gameUserOp = guo;
		username = userName;
		new UserProfileUpdater().execute();
		th = this;
	}
	
	/**
     * Trigger RMI register and output result
     * @author User
     *
     */
    private class UserProfileUpdater extends SwingWorker<Void, Void>{

        @Override
        protected Void doInBackground() throws Exception {
            updateStatus();
            return null;
        }

        @Override
        protected void done(){
        	// If previous player record is not cleared, clear up first
    		MainUI.setCurrentPlayers(new ArrayList<PlayerInfo>());
        	Font playerNameFont = new Font("Arial", Font.BOLD, 25);
    		Font paragraphFont = new Font("Arial", Font.PLAIN, 15);
    		Font rankFont = new Font("Arial", Font.PLAIN, 20);
    		ja[0] = new JLabel(strArr[0][0]);
    		ja[1] = new JLabel("Number of wins: "+strArr[0][2]);
    		ja[2] = new JLabel("Number of games: "+strArr[0][3]);
    		ja[3] = new JLabel("Average time to win: "+strArr[0][4]+"s");
    		ja[4] = new JLabel("Rank: #"+strArr[0][5]);
    		int counter = 0;
    		th.invalidate();
    		setBorder(new EmptyBorder(10,10,10,10));
    		setLayout(new BoxLayout(th, BoxLayout.Y_AXIS));
    		setPreferredSize(new Dimension(800, 550));
    		for (JLabel jt : ja){
    			jt.setAlignmentX(LEFT_ALIGNMENT);
    			jt.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    			if (counter == 0)
    				jt.setFont(playerNameFont);
    			else if (counter == 4)
    				jt.setFont(rankFont);
    			else jt.setFont(paragraphFont);
    			counter++;
    			add(jt);
    		}
        }
    }

    /**
     * Module responsible for RMI register
     */
    private void updateStatus() {
        if (gameUserOp != null){
            try {
                strArr = gameUserOp.userProfile(username, MainUI.getCurrentPlayers());
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "User profile error",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("User profile: "+e);
                System.exit(1);
            }
        }
    }
}
