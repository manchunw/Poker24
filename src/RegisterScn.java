import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Register screen of JPoker 24-Game
 * @author User
 */
public class RegisterScn extends JFrame{

	private static final long serialVersionUID = -3088768109304199936L;
	private JTextField regName;
    private JPasswordField regPassword;
    private JPasswordField regPasswordCfm;
    private JButton registerSuccessButton;
    private JButton cancelButton;
    private JPanel registerPanel;
    private GameUserOp gameUserOp;
    private JFrame loginScn;
    private int status;
    private String host;

    /**
     * Constructor of register screen specifying the interface of functions fetched by RMI, the login screen and host of server
     * @param guo interface of functions fetched by RMI
     * @param ls login screen
     * @param host host of server for connection
     */
    public RegisterScn(GameUserOp guo, JFrame ls, String host) {
        gameUserOp = guo;
        loginScn = ls;
        this.host = host;
        setTitle("Register");
        registerPanel = new RegisterPanel();
        setContentPane(registerPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registerPanel.setBorder(
                new CompoundBorder(new EmptyBorder(10, 10, 10, 10),
                        new CompoundBorder(BorderFactory.createTitledBorder("Register"), new EmptyBorder(10, 10, 10, 10))));
        registerSuccessButton.addActionListener(new RegisterSuccessButtonListener());
        cancelButton.addActionListener(new CancelButtonListener());
        setSize(250, 260);
        setLocation(500,200);
        setResizable(false);
        setVisible(true);
    }
    
    /**
     * Register panel layout
     * @author User
     *
     */
    private class RegisterPanel extends JPanel{

		private static final long serialVersionUID = 8211329278377837151L;
		
		private RegisterPanel(){
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 3;
			this.add(new JLabel("Login Name"), c);
			regName = new JTextField();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 3;
			this.add(regName, c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 3;
			this.add(new JLabel("Password"), c);
			regPassword = new JPasswordField();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 3;
			this.add(regPassword, c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 4;
			c.gridwidth = 3;
			this.add(new JLabel("Confirm Password"), c);
			regPasswordCfm = new JPasswordField();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 5;
			c.gridwidth = 3;
			this.add(regPasswordCfm, c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 6;
			c.weightx = 0.5;
			c.gridwidth = 1;
			registerSuccessButton = new JButton("Register");
			this.add(registerSuccessButton, c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 6;
			c.weightx = 0.5;
			c.gridwidth = 1;
			this.add(new JLabel(""), c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 2;
			c.gridy = 6;
			c.weightx = 0.5;
			c.gridwidth = 1;
			cancelButton = new JButton("Cancel");
			this.add(cancelButton, c);
		}
    	
    }

    /**
     * Register button listener to check validity of fields and trigger RMI register
     * @author User
     *
     */
    private class RegisterSuccessButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (regName.getText().equals("")){
                JOptionPane.showMessageDialog(null, "Login name should not be empty",
                        "Empty login name", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String regPasswordStr = new String(regPassword.getPassword());
            String regPasswordCfmStr = new String(regPasswordCfm.getPassword());
            if (regPasswordStr.equals("")){
                JOptionPane.showMessageDialog(null, "Password should not be empty",
                        "Empty password", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (regPasswordCfmStr.equals("")){
                JOptionPane.showMessageDialog(null, "Confirm password should not be empty",
                        "Empty confirm password", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (! regPasswordStr.equals(regPasswordCfmStr)){
                JOptionPane.showMessageDialog(null, "Password fields do not match",
                        "Password not matching", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new RegisterUpdater().execute();
        }
    }

    /**
     * Cancel button listener to return to login screen
     * @author User
     *
     */
    private class CancelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            loginScn.setVisible(true);
        }
    }

    /**
     * Trigger RMI register and output result
     * @author User
     *
     */
    private class RegisterUpdater extends SwingWorker<Void, Void>{

        @Override
        protected Void doInBackground() throws Exception {
            updateStatus();
            return null;
        }

        @Override
        protected void done(){
            if (status == 0){
                setVisible(false);
                new MainUI(regName.getText(), gameUserOp, host);
            } else if (status == 1){
                JOptionPane.showMessageDialog(null, "Same login name has already been registered",
                        "Login name not available", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Module responsible for RMI register
     */
    private void updateStatus() {
        if (gameUserOp != null){
            try {
            	String passwordStr = new String(regPassword.getPassword());
                status = gameUserOp.register(regName.getText(), passwordStr);
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Register error",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Register: "+e);
                System.exit(1);
            }
        }
    }
}
