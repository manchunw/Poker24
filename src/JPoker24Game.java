import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * Client program for JPoker 24-Game
 * @author User
 */
public class JPoker24Game extends JFrame implements Runnable {

    private static final long serialVersionUID = -6355353774367607051L;
	private GameUserOp gameUserOp;
    private JTextField loginName;
    private JPasswordField password;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel loginPanel;
    private int status;
    private JFrame ls;
    private String host;

    /**
     * Main program
     * @param args arguments of program
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SwingUtilities.invokeLater(new JPoker24Game(args[0]));
    }

    /**
     * Constructor of client program specifying the host of server
     * @param host Host of server for connection
     */
    public JPoker24Game(String host){
        ls = this;
        this.host = host;
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            gameUserOp = (GameUserOp) registry.lookup("GameServer");
        } catch (Exception e){
            System.err.println("Failed accessing RMI: "+e);
        }
    }

    /**
     * runnable procedure to define login UI
     */
    @Override
    public void run() {
        setTitle("Login");
        loginPanel = new LoginPanel();
        this.setContentPane(loginPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginPanel.setBorder(
                new CompoundBorder(new EmptyBorder(10, 10, 10, 10),
                        new CompoundBorder(BorderFactory.createTitledBorder("Login"), new EmptyBorder(10, 10, 10, 10))));
        loginButton.addActionListener(new LoginButtonListener());
        registerButton.addActionListener(new RegisterButtonListener());
        setLocation(500,200);
        setSize(250, 220);
        setResizable(false);
        setVisible(true);
    }
    
    /**
     * Login panel layout
     * @author User
     *
     */
    private class LoginPanel extends JPanel{

		private static final long serialVersionUID = -6529174439873849852L;
		
		private LoginPanel(){
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 3;
			this.add(new JLabel("Login Name"), c);
			loginName = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 3;
			this.add(loginName, c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 3;
			this.add(new JLabel("Password"), c);
			password = new JPasswordField();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 3;
			this.add(password, c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 4;
			c.weightx = 0.5;
			c.gridwidth = 1;
			loginButton = new JButton("Login");
			this.add(loginButton, c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 4;
			c.weightx = 0.5;
			c.gridwidth = 1;
			this.add(new JLabel(""), c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 2;
			c.gridy = 4;
			c.weightx = 0.5;
			c.gridwidth = 1;
			registerButton = new JButton("Register");
			this.add(registerButton, c);
		}
    	
    }

    /**
     * Login button listener to check validity of fields and trigger RMI login
     * @author User
     *
     */
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (loginName.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Login name should not be empty",
                        "Empty login name", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String passwordStr = new String(password.getPassword());
            if (passwordStr.equals("")){
                JOptionPane.showMessageDialog(null, "Password should not be empty",
                        "Empty password", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new LoginUpdater().execute();
        }
    }

    /**
     * Register button listener to go to register screen
     * @author User
     *
     */
    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            new RegisterScn(gameUserOp, ls, host);
        }
    }

    /**
     * Trigger RMI login and output result
     * @author User
     *
     */
    private class LoginUpdater extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            updateStatus();
            return null;
        }

        @Override
        protected void done(){
            if (status == 0){
                setVisible(false);
                new MainUI(loginName.getText(), gameUserOp, host);
            } else if (status == 1){
                JOptionPane.showMessageDialog(null, "Login name and/or password is not correct",
                        "Invalid login name/password", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "You have already logged in",
                        "User already logged in", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }

    /**
     * module responsible for RMI login
     */
    private void updateStatus() {
        if (gameUserOp != null){
            try {
            	String passwordStr = new String(password.getPassword());
                status = gameUserOp.login(loginName.getText(), passwordStr);
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Login error",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Login: "+e);
                System.exit(1);
            }
        }
    }
}
