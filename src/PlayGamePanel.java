import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;


/**
 * Play game panel
 * @author User
 *
 */
public class PlayGamePanel extends JPanel implements MessageListener {

	private static final long serialVersionUID = 6577373600382160311L;
	
	private GameUserOp gameUserOp;
	private String username;
	private JPanel pgPanel, scoreui;
	private JMSHelper jmshelper;
	private MessageProducer queueSender;
	private MessageConsumer topicReceiver;
	private JTextField jt;
	private PlayerInfo myPlayer;
	private long startTime = 0, endTime = 0;
	private NewGame newGame;

	/**
	 * Constructor for play game panel
	 */
	public PlayGamePanel(GameUserOp guo, String userName, JMSHelper jmshelper, MessageProducer queueSender, MessageConsumer topicReceiver){
		gameUserOp = guo;
		username = userName;
		// In order not to call these again, these are initiated in MainUI.java
		this.jmshelper = jmshelper;
		this.queueSender = queueSender;
		this.topicReceiver = topicReceiver;
		pgPanel = new NewGamePanel();
		setPreferredSize(new Dimension(800, 550));
		add(pgPanel, BorderLayout.CENTER);
		// receive a message
		try {
			this.topicReceiver.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new PlayGameUpdater().execute();
	}
	
	/**
     * Trigger RMI login and output result
     * @author User
     *
     */
    private class PlayGameUpdater extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            updateStatus1();
            return null;
        }

        @Override
        protected void done(){
    		// If previous player record is not cleared, clear up first
    		MainUI.setCurrentPlayers(new ArrayList<PlayerInfo>());
        }
    }

    /**
     * module responsible for RMI login
     */
    private void updateStatus1() {
        if (gameUserOp != null){
            try {
                gameUserOp.playGame(username, MainUI.getCurrentPlayers());
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Leader board error",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Leader board: "+e);
                System.exit(1);
            }
        }
    }
	
	private class NewGamePanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3347842112036698322L;
		private JButton jb;
		
		public NewGamePanel(){
			jb = new JButton("New Game");
			jb.setPreferredSize(new Dimension(800, 550));
			jb.addActionListener(new JbListener());
			add(jb, BorderLayout.CENTER);
		}
	}
	
	private class JbListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			remove(pgPanel);
			repaint();
			pgPanel = new WaitingPanel();
			add(pgPanel, BorderLayout.CENTER);
			invalidate();
			validate();
			// Load the game
			new LoadingGameUI().execute();
		}
		
		private class LoadingGameUI extends SwingWorker<Void, Void>{

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				// start the waiting player process
				start();
				return null;
			}

			@Override
			protected void done() {
				// TODO Auto-generated method stub
				super.done();
			}
			
		}
		
	}
	
	private void start() throws JMSException, NamingException{
		// send a message
		if (myPlayer == null)
			myPlayer = new PlayerInfo(username);
		try {
			Message message = null;
			message = jmshelper.createMessage(myPlayer);
			if(message != null) queueSender.send(message);
			System.out.println("Message Sent");
		} catch (JMSException e1) {
			System.err.println("Failed to send message");
		}
	}
	
	private class WaitingPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1052335901427257273L;
		private JLabel jl;
		public WaitingPanel(){
			jl = new JLabel("Waiting for Players...", SwingConstants.CENTER);
			setPreferredSize(new Dimension(800, 550));
			add(jl, BorderLayout.CENTER);
		}
	}
	
	private class GameUI extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3976704373354850259L;
		private JPanel gui;
		public GameUI(String [] cards, ArrayList<PlayerInfo> players){
			gui = new GameInterface(cards);
			setPreferredSize(new Dimension(800,550));
			add(gui, BorderLayout.CENTER);
			scoreui = new ScoreInterface(players);
			add(scoreui, BorderLayout.EAST);
		}
	}
	
	private class GameInterface extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1329590925018654857L;

		public GameInterface(String [] cards){
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			setPreferredSize(new Dimension(600,550));
			add(new CardsInterface(cards));
			add(new InputInterface(cards));
		}
	}
	
	private class CardsInterface extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3997239031184604401L;

		public CardsInterface(String [] cards){
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			setPreferredSize(new Dimension(600,400));
			for (int i = 0; i < 4; i++){
				add(new CardInterface(cards[i]+".png"));
			}
		}
		
		private class CardInterface extends JPanel{
			/**
			 * 
			 */
			private static final long serialVersionUID = -6090151149320591931L;

			private CardInterface(String file){
				setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
				URL url = null;
				try {
					url = getClass().getResource(file).toURI().toURL();
				} catch (MalformedURLException | URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ImageIcon ii = new ImageIcon(url);
				Image img = ii.getImage();
				Image resized = img.getScaledInstance(100, 145, Image.SCALE_DEFAULT);
				setPreferredSize(new Dimension(65,400));
				add(new JLabel(new ImageIcon(resized)));
			}

		}
	}
	
	private class InputInterface extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7614731296153659803L;
		private JLabel jb;
		private InputInterface(String [] cards){
			setPreferredSize(new Dimension(600,40));
			jt = new JTextField();
			jt.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					jt.setEnabled(false);
					endTime = System.nanoTime();
					new GameOverUpdater().execute();
				}
				
			});
			jt.setPreferredSize(new Dimension(550,40));
			add(jt, BorderLayout.CENTER);
			jb = new JLabel("= 24");
			add(jb, BorderLayout.EAST);
		}
	}
	
	private class ScoreInterface extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3151915280061718009L;

		private ScoreInterface(ArrayList<PlayerInfo> players){
			setPreferredSize(new Dimension(180, 550));
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			for (PlayerInfo player : players)
				add(new ScoreChildInterface(player));
		}
	}
	
	private class ScoreChildInterface extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1728021671718453374L;

		private ScoreChildInterface(PlayerInfo playerInfo){
			setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			setMaximumSize(new Dimension(200,75));
			add(new JLabel(playerInfo.getName()));
			add(new JLabel("Win: "+playerInfo.getNumWin()+"/"+playerInfo.getGamePlayed()+" avg: "+playerInfo.getAvg()+"s"));
		}
	}
	
	private class GameOverUI extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8233917305233811592L;
		private JButton jb;
		private GameOverUI(String username, String answer){
			setPreferredSize(new Dimension(800, 550));
			add(new GameResultPanel(username, answer), BorderLayout.CENTER);
			jb = new JButton("Next Game");
			jb.setPreferredSize(new Dimension(800, 40));
			jb.addActionListener(new JbListener());
			add(jb, BorderLayout.SOUTH);
		}
	}
	
	private class GameResultPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4560820115765990953L;
		private JLabel jl, jl2;
		private GameResultPanel(String username, String answer){
			setPreferredSize(new Dimension(800, 470));
			jl = new JLabel("Winner: "+username, SwingConstants.CENTER);
			jl2 = new JLabel(answer, SwingConstants.CENTER);
			add(new ResultJLabelPanel());
		}
		private class ResultJLabelPanel extends JPanel {
			/**
			 * 
			 */
			private static final long serialVersionUID = 3398008146687858007L;

			public ResultJLabelPanel(){
				setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
				add(jl);
				add(jl2);
			}
		}
	}
	
	private boolean status;
	
    /**
     * Trigger game over and output result
     * @author User
     *
     */
    private class GameOverUpdater extends SwingWorker<Void, Void>{

        @Override
        protected Void doInBackground() throws Exception {
            updateStatus();
            return null;
        }

        @Override
        protected void done(){
        	jt.setEnabled(true);
            if (status){
                System.out.println("Correct answer.");
                //setVisible(false);
                //new MainUI(regName.getText(), gameUserOp, host);
            } else {
                JOptionPane.showMessageDialog(null, "Try again",
                        "Wrong answer", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Module responsible for RMI game over
     */
    private void updateStatus() {
        if (gameUserOp != null){
            try {
                status = gameUserOp.compareWin(username, newGame, jt.getText(), (endTime - startTime)/1000000000);
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Check answer error",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Check answer: "+e);
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
    
    private String[] getPlayerNames(){
    	String[] playerNames = new String[newGame.getPlayers().size()];
    	for (int i = 0; i < newGame.getPlayers().size(); i++)
    		playerNames[i] = newGame.getPlayers().get(i).getName();
    	return playerNames;
    }

	@Override
	public void onMessage(Message jmsMessage) {
		// TODO Auto-generated method stub
		try {
			Object output = ((ObjectMessage)jmsMessage).getObject();
			if (output instanceof NewGame){
				NewGame tmpGame = (NewGame) output;
				if (tmpGame.searchByUsername(username) > -1){
					newGame = (NewGame) output;
					System.out.println("Received!! Start the game.");
					System.out.println("I am player "+newGame.searchByUsername(username)+".");
					MainUI.setCurrentPlayers(newGame.getPlayers());
					remove(pgPanel);
					repaint();
					pgPanel = new GameUI(newGame.getCards(), newGame.getPlayers());
					add(pgPanel, BorderLayout.CENTER);
					startTime = System.nanoTime();
					invalidate();
					validate();
				}
			} else if (output instanceof EndGame){
				EndGame endGame = (EndGame) output;
				int idx = endGame.searchByUsername(username);
				if (idx > -1){
					System.out.println("Received!! End the game.");
					myPlayer = endGame.getPlayers().get(idx);
					MainUI.setCurrentPlayers(new ArrayList<PlayerInfo>());
					remove(pgPanel);
					repaint();
					pgPanel = new GameOverUI(endGame.getWinnerName(), endGame.getWinnerFormula());
					add(pgPanel, BorderLayout.CENTER);
					invalidate();
					validate();
				}
			} else if (output instanceof UpdateGame){
				UpdateGame updateGame = (UpdateGame) output;
				if (updateGame.searchByUsername(username) > -1){
					System.out.println("Received!! Update the game.");
					MainUI.setCurrentPlayers(updateGame.getPlayers());
					String [] playerNames = getPlayerNames();
					for (int i = 0; i < playerNames.length; i++)
						if (updateGame.searchByUsername(playerNames[i]) == -1)
							newGame.removePlayer(playerNames[i]);
					pgPanel.remove(scoreui);
					repaint();
					scoreui = new ScoreInterface(updateGame.getPlayers());
					pgPanel.add(scoreui, BorderLayout.EAST);
					invalidate();
					validate();
				}
			}
		} catch (JMSException e) {
			System.err.println("Failed to receive message");
		}
	}
}
