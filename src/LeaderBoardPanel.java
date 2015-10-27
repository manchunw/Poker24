import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;


/**
 * Leader board panel
 * @author User
 *
 */
public class LeaderBoardPanel extends JPanel {

	private static final long serialVersionUID = -4876841188855555071L;
	private JTable jt;
	private GameUserOp gameUserOp;
	private String username;
	private Object [][] strArr;
	private LeaderBoardPanel th;

	/**
	 * Constructor for leader board panel
	 */
	public LeaderBoardPanel(GameUserOp guo, String userName){
		gameUserOp = guo;
		username = userName;
		th = this;
		new LeaderBoardUpdater().execute();
	}
	
	/**
     * Trigger RMI login and output result
     * @author User
     *
     */
    private class LeaderBoardUpdater extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            updateStatus();
            return null;
        }

        @Override
        protected void done(){
        	// If previous player record is not cleared, clear up first
    		MainUI.setCurrentPlayers(new ArrayList<PlayerInfo>());
        	th.removeAll();
        	th.revalidate();
    		String[] columns = {"Rank","Player","Games won","Games played","Avg. winning time"};
    		jt = new JTable(strArr,columns);
    		th.setLayout(new BorderLayout());
    		jt.setPreferredScrollableViewportSize(new Dimension(800,550));
    		th.add(new JScrollPane(jt));
        }
    }

    /**
     * module responsible for RMI login
     */
    private void updateStatus() {
        if (gameUserOp != null){
            try {
                strArr = gameUserOp.leaderBoard(username, MainUI.getCurrentPlayers());
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Leader board error",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Leader board: "+e);
                System.exit(1);
            }
        }
    }
}
