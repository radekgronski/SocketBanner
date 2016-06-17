package banner;

import tools.MessageInfo;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.GridBagConstraints;
import javax.swing.JTextPane;
import java.awt.Insets;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Banner extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextPane displayPane;
	private JScrollPane scrollContainer;
	private JTable table;
	private boolean running;
	private ServerSocket server;
	private Socket socket;
	private int port;
	
	public Banner(String name, int port) {
		this(port);
		setTitle(name + " (port: " + port + ")");
	}

	public Banner(int port) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 400, 500);
		setMinimumSize(new Dimension(400, 500));
		setTitle("Banner");
		
		this.port = port;
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0};
		gbl_contentPane.rowWeights = new double[]{0.4, 0.6};
		contentPane.setLayout(gbl_contentPane);
		
		displayPane = new JTextPane();
		displayPane.setEditable(false);
		GridBagConstraints gbc_displayPane = new GridBagConstraints();
		gbc_displayPane.insets = new Insets(5, 5, 5, 5);
		gbc_displayPane.fill = GridBagConstraints.BOTH;
		gbc_displayPane.gridx = 0;
		gbc_displayPane.gridy = 0;
		contentPane.add(displayPane, gbc_displayPane);
	
		table = new JTable(new MessagesTableModel());
		table.getColumnModel().getColumn(0).setPreferredWidth(280);
		table.getColumnModel().getColumn(1).setPreferredWidth(110);
		scrollContainer = new JScrollPane(table);
		GridBagConstraints gbc_scrollContainer = new GridBagConstraints();
		gbc_scrollContainer.fill = GridBagConstraints.BOTH;
		gbc_scrollContainer.insets = new Insets(5, 5, 5, 5);
		gbc_scrollContainer.gridx = 0;
		gbc_scrollContainer.gridy = 1;
		contentPane.add(scrollContainer, gbc_scrollContainer);
		
		running = true;
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        stop();
		    }
		});
	}
	
	private void displayThread() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (running) {
					MessageInfo info = ((MessagesTableModel) table.getModel()).popMessage();
					
					if (info != null) {
						displayPane.setText(info.getMessage());
						try {
							Thread.sleep(info.getTimeout());
						} catch (InterruptedException e) { }
					} else {
						displayPane.setText("");
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) { }
					}
				}
			}
		};
		
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	private void readSocketThread() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (running) {
					try {
						socket = server.accept();
						String[] read = new DataInputStream(socket.getInputStream())
										.readUTF().split(";");
						
						if (read != null && read.length > 0) {

							switch (read.length) {
							case 1: // REMOVE SENTENCE --------------------------------------------
								if (read[0].length() < 3 || !read[0].substring(0, 2).equals("d:")) {
									break;
								}									
								removeSentence(read[0].substring(2));
								break;								
								
							case 2: // ADD SENTENCE ------------------------------------------------
								if (read[0].length() < 3 || !read[0].substring(0, 2).equals("a:")) {
									break;
								}
								if (read[1].length() < 3 || !read[1].substring(0, 2).equals("t:")) {
									break;
								}
								try {
									int timeout = Integer.parseInt(read[1].substring(2));
									addSentence(read[0].substring(2), timeout);
								} catch (NumberFormatException e) {
									break;
								}
								break;
								
							case 3: // UPDATE SENTENCE ---------------------------------------------
								if (read[0].length() < 3 || !read[0].substring(0, 2).equals("u:")) {
									break;
								}
								if (read[1].length() < 3 || !read[1].substring(0, 2).equals("n:")) {
									break;
								}
								if (read[2].length() < 3 || !read[2].substring(0, 2).equals("t:")) {
									break;
								}
								try {
									int timeout = Integer.parseInt(read[2].substring(2));
									updateSentence(read[0].substring(2), read[1].substring(2), 
											timeout);
								} catch (NumberFormatException e) {
									break;
								}
								break;
							}
							
						}

					} catch (IOException e) {}
				}
			}
		};
		
		Thread thread = new Thread(runnable);
		thread.start();
	}

	public void addSentence(String s, int timeout) {
		((MessagesTableModel)table.getModel()).addMessage(s, timeout);
	}

	public void removeSentence(String s) {
		((MessagesTableModel)table.getModel()).removeMessages(s);
	}

	public List<String> getAllSentences() {
		return ((MessagesTableModel)table.getModel()).getAllSentences();
	}
	
	public void updateSentence(String oldS, String newS, int timeout) {
		((MessagesTableModel)table.getModel()).updateSentence(oldS, newS, timeout);
	}

	public void setTimeout(String s, int milliseconds) {
		((MessagesTableModel)table.getModel()).setTimeout(s, milliseconds);	
	}

	public void start() {
		setVisible(true);
		
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Server error.", 
					"Error", JOptionPane.ERROR_MESSAGE);
			stop();
		}
		
		displayThread();
		readSocketThread();
	}

	public void stop() {
		running = false;
		
		if (socket != null && !socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {}
		}
		
		if (server != null && !server.isClosed()) {
			try {
				server.close();
			} catch (IOException e) {}
		}
		
		dispose();
	}

}
