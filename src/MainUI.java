import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.naming.NamingException;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;


/**
 * User interface of main window
 * @author User
 */
public class MainUI extends JFrame {

	private static final long serialVersionUID = -7524564950227284713L;
	private static ArrayList<PlayerInfo> currentPlayers = new ArrayList<PlayerInfo>();
	private JButton userProfileButton;
    private JButton playGameButton;
    private JButton leaderBoardButton;
    private JButton logoutButton;
    private JPanel MenuPanel;
    private JPanel mainPanel;
    private JPanel ContentPanel;
    private String username;
    private GameUserOp gameUserOp;
    private JMSHelper jmshelper;
	private MessageProducer queueSender;
	private MessageConsumer topicReceiver;
    private int status;
    //private String host;
    
    /**
     * Constructor of main window specifying the interface of functions fetched by RMI, the login name and host of server
     * @param loginName Login name
     * @param guo Interface of functions fetched by RMI
     * @param host Host of server for connection
     */
    public MainUI (String loginName, GameUserOp guo, String host) {
        username = loginName;
        gameUserOp = guo;
        try {
			jmshelper = new JMSHelper(host);
			queueSender = jmshelper.createQueueSender();
			topicReceiver = jmshelper.createTopicReader();
		} catch (NamingException | JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //this.host = host;
        setTitle("JPoker 24-Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800,600));
        setResizable(false);
        mainPanel = new MainPanel();
        setContentPane(mainPanel);
        userProfileButton.addActionListener(new UserProfileButtonListener());
        playGameButton.addActionListener(new PlayGameButtonListener());
        leaderBoardButton.addActionListener(new LeaderBoardButtonListener());
        logoutButton.addActionListener(new LogoutButtonListener());
        addWindowListener(new myWindowListener());
        setLocation(100,50);
        pack();
        setVisible(true);
    }
    
    /**
     * Main panel combining menu buttons and content interface
     * @author User
     *
     */
    private class MainPanel extends JPanel {
    	
		private static final long serialVersionUID = 6204775178240754183L;

		/**
		 * Constructor of main panel
		 */
		private MainPanel(){
			MenuPanel = new MyMenuPanel();
			add(MenuPanel, BorderLayout.PAGE_START);
			ContentPanel = new UserProfilePanel(gameUserOp, username);
			add(ContentPanel, BorderLayout.CENTER);
			pack();
    	}
    }
    
    /**
     * Menu buttons layout
     * @author User
     *
     */
    private class MyMenuPanel extends JPanel {
    	
		private static final long serialVersionUID = -3733711632285893549L;

		/**
		 * Constructor for menu buttons layout
		 */
		private MyMenuPanel(){
    		setLayout(new GridLayout(0,4));
    		setPreferredSize(new Dimension(800,40));
    		userProfileButton = new JButton("User Profile");
			add(userProfileButton);
			playGameButton = new JButton("Play Game");
			add(playGameButton);
			leaderBoardButton = new JButton("Leader Board");
			add(leaderBoardButton);
			logoutButton = new JButton("Logout");
			add(logoutButton);
			pack();
    	}
    }
    
    /**
     * Logout when window is closed
     * @author User
     *
     */
    private class myWindowListener extends WindowAdapter {

		/**
		 * Trigger logout action when window is closed
		 */
    	@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub
			logoutButton.doClick();
		}
    	
    }
    
    /**
     * User profile button listener to open user profile in content interface
     * @author User
     *
     */
    private class UserProfileButtonListener implements ActionListener {

		/**
		 * Open user profile in content interface
		 */
    	@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			remove(ContentPanel);
			repaint();
			ContentPanel = new UserProfilePanel(gameUserOp, username);
			add(ContentPanel, BorderLayout.CENTER);
			invalidate();
			validate();
		}
    	
    }
    
    /**
     * Play game button listener to open play game in content interface
     * @author User
     *
     */
    private class PlayGameButtonListener implements ActionListener {

		/**
		 * Open play game in content interface
		 */
    	@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			remove(ContentPanel);
			repaint();
			ContentPanel = new PlayGamePanel(gameUserOp, username, jmshelper, queueSender, topicReceiver);
			add(ContentPanel, BorderLayout.CENTER);
			invalidate();
			validate();
		}
    	
    }
    
    /**
     * Leader board button listener to open leader board in content interface
     * @author User
     *
     */
    private class LeaderBoardButtonListener implements ActionListener {

		/**
		 * Open leader board in content interface
		 */
    	@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			remove(ContentPanel);
			repaint();
			ContentPanel = new LeaderBoardPanel(gameUserOp, username);
			add(ContentPanel, BorderLayout.CENTER);
			invalidate();
			validate();
		}
    	
    }

    /**
     * Logout button listener to trigger RMI logout
     * @author User
     *
     */
    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new LogoutUpdater().execute();
        }
    }

    /**
     * Trigger RMI logout and output result
     * @author User
     *
     */
    private class LogoutUpdater extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            updateStatus();
            return null;
        }
        @Override
        protected void done(){
            if (status >= 0) {
            	setVisible(false);
            	System.exit(0);
                //SwingUtilities.invokeLater(new PostFix(host));
            }
        }
    }

    /**
     * Module responsible for RMI logout
     */
    private void updateStatus() {
        if (gameUserOp != null){
            try {
                status = gameUserOp.logout(username, currentPlayers);
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Logout error",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Register: "+e);
                System.exit(1);
            }
        }
    }

	public static ArrayList<PlayerInfo> getCurrentPlayers() {
		return currentPlayers;
	}

	public static void setCurrentPlayers(ArrayList<PlayerInfo> currentPlayers) {
		MainUI.currentPlayers = currentPlayers;
	}
}
