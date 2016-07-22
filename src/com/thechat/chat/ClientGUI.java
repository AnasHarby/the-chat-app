package com.thechat.chat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientGUI extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea txtrChatbox, txtrUserlist;
	private DefaultCaret caret;
	
	private Thread listenThread, runThread;
	
	private Client client;
	
	public ClientGUI(String clientUsername, String clientAddress, int clientPort) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		client = new Client(clientUsername, clientAddress, clientPort);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 520);
		setLocationRelativeTo(null);
		setTitle("Chat Window");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{60, 690, 140}; 
		gbl_contentPane.rowHeights = new int[]{10, 470, 40}; 
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		txtrChatbox = new JTextArea();
		txtrChatbox.setEditable(false);
		
		GridBagConstraints gbc_scroll = new GridBagConstraints();
		gbc_scroll.insets = new Insets(0, 0, 5, 5);
		gbc_scroll.fill = GridBagConstraints.BOTH;
		gbc_scroll.gridx = 0;
		gbc_scroll.gridy = 0;
		gbc_scroll.gridwidth = 2;
		gbc_scroll.gridheight = 2;
		gbc_scroll.insets = new Insets(0, 5, 0, 0); 
		JScrollPane scroll = new JScrollPane(txtrChatbox); 
		caret = (DefaultCaret) txtrChatbox.getCaret(); 
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); 
		contentPane.add(scroll, gbc_scroll);
		
		txtrUserlist = new JTextArea();
		txtrUserlist.setEditable(false);
		txtrUserlist.setText("Connected users: \n\n");
		
		GridBagConstraints gbc_scrollUserlist = new GridBagConstraints();
		gbc_scrollUserlist.insets = new Insets(0, 5, 0, 0);
		gbc_scrollUserlist.fill = GridBagConstraints.BOTH;
		gbc_scrollUserlist.gridx = 2;
		gbc_scrollUserlist.gridy = 0;
		gbc_scrollUserlist.gridheight = 2;
		JScrollPane scrollUserlist = new JScrollPane(txtrUserlist);
		contentPane.add(scrollUserlist, gbc_scrollUserlist);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER) {
					showMessage(txtMessage.getText());
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				showMessage(txtMessage.getText());
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		boolean connected = client.connect();
		if (!connected) {
			System.err.println("Connection failed!");
			console("Connection failed!");
		}
		
		setVisible(true);
		txtMessage.requestFocus();
		console("Connecting to " + clientAddress + ":" + clientPort + "...");
		String connection = "/c/" + client.getUsername(); 
		client.send(connection.getBytes());
		runThread = new Thread(this, "Run");
		runThread.start();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { 
				String disconnect = "/d/" + client.getID();
				client.send(disconnect.getBytes());
				client.close();
				
			}
		});
	}
	
	public void run() {
		listen();
	}
	
	private void console(String message) {
		if (message.equals(""))
			return;
		txtrChatbox.append(message + "\n");
	}
	
	private void showMessage(String message) {
		if (message.equals(""))
			return;
		client.send(("/m/" + client.getUsername() + ": " + message).getBytes());
		txtMessage.setText("");
	}
	
	private void listen() {
		listenThread = new Thread() { 
			public void run() {
				while (true) {
					String message = client.receive();
					if (message.startsWith("/c/")) {
						client.setID(Integer.parseInt(message.substring(3, message.length()).trim()));
						console("Connection Successful!\n");
					} else if (message.startsWith("/m/")) {
						console(message.substring(3, message.length()).trim());
					} else if (message.startsWith("/i/")) {
						client.send(("/i/" + client.getID()).getBytes());
					}
					else if (message.startsWith("/u/")) {
						showUserlist(message);
					}
				}
			}
		};
		listenThread.start();
	}
	
	private void showUserlist(String message) {
		txtrUserlist.setText("Connected Users: \n\n" + message.substring(3, message.length()).trim());
	}
}
