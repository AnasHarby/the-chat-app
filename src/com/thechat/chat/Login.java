package com.thechat.chat;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblUsername;
	private JTextField txtUsername;
	private JLabel lblIpAddress;
	private JTextField txtIPAddress;
	private JLabel lblPort;
	private JTextField txtPort;
	private JLabel lbleg;
	private JLabel lbleg_1;

	public Login() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());   
		} catch (Exception e) {
			e.printStackTrace();
		}
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 330); 
		setLocationRelativeTo(null); 	
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblUsername = new JLabel("Username:");
		lblUsername.setBounds(111, 29, 77, 21);
		contentPane.add(lblUsername);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(51, 52, 198, 25);
		contentPane.add(txtUsername);
		txtUsername.setColumns(10);
		
		lblIpAddress = new JLabel("IP Address:");
		lblIpAddress.setBounds(110, 87, 80, 15);
		contentPane.add(lblIpAddress);
		
		txtIPAddress = new JTextField();
		txtIPAddress.setColumns(10);
		txtIPAddress.setBounds(51, 113, 198, 25);
		contentPane.add(txtIPAddress);
		
		lblPort = new JLabel("Port:");
		lblPort.setBounds(129, 162, 42, 15);
		contentPane.add(lblPort);
		
		txtPort = new JTextField();
		txtPort.setColumns(10);
		txtPort.setBounds(51, 188, 198, 25);
		contentPane.add(txtPort);
		
		lbleg = new JLabel("(eg. 8192)");
		lbleg.setBounds(115, 215, 70, 15);
		contentPane.add(lbleg);
		
		lbleg_1 = new JLabel("(eg. 192.168.1.1)");
		lbleg_1.setBounds(92, 140, 116, 15);
		contentPane.add(lbleg_1);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String username = txtUsername.getText();
				String address = txtIPAddress.getText();
				int port = Integer.parseInt(txtPort.getText());
				login(username, address, port);
			}
		});
		btnLogin.setBounds(49, 240, 200, 50);
		contentPane.add(btnLogin);
	}
	
	private void login(String username, String address, int port) {
		dispose(); 
		new ClientGUI(username, address, port);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
