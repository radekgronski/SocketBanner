package client;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblSentence;
	private JScrollPane scrollPaneRight;
	private JTextArea textAreaSentence;
	private JLabel lblTimeout;
	private JFormattedTextField txtTimeout;
	private JRadioButton rdbtnAddSentence;
	private JRadioButton rdbtnRemoveSentence;
	private JRadioButton rdbtnUpdate;
	private JButton btnExecute;
	private Runnable runner;
	private JTextField txtHost;
	private JFormattedTextField txtPort;
	private JLabel lblHost;
	private JLabel lblPort;
	private Socket client;
	private boolean update;
	private String old;

	public Client() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 400, 450);
		setMinimumSize(new Dimension(400, 450));
		setTitle("Client");
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		contentPane.setLayout(gbl_contentPane);
		
		lblHost = new JLabel("Host:");
		GridBagConstraints gbc_lblHost = new GridBagConstraints();
		gbc_lblHost.anchor = GridBagConstraints.WEST;
		gbc_lblHost.insets = new Insets(0, 0, 5, 5);
		gbc_lblHost.gridx = 0;
		gbc_lblHost.gridy = 0;
		contentPane.add(lblHost, gbc_lblHost);
		
		lblPort = new JLabel("Port:");
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.WEST;
		gbc_lblPort.insets = new Insets(0, 0, 5, 0);
		gbc_lblPort.gridx = 1;
		gbc_lblPort.gridy = 0;
		contentPane.add(lblPort, gbc_lblPort);
		
		txtHost = new JTextField("localhost");
		GridBagConstraints gbc_txtHost = new GridBagConstraints();
		gbc_txtHost.insets = new Insets(0, 0, 10, 5);
		gbc_txtHost.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtHost.gridx = 0;
		gbc_txtHost.gridy = 1;
		contentPane.add(txtHost, gbc_txtHost);
		txtHost.setColumns(10);
		
		txtPort = new JFormattedTextField(new Integer(1201));
		GridBagConstraints gbc_txtPort = new GridBagConstraints();
		gbc_txtPort.insets = new Insets(0, 0, 10, 0);
		gbc_txtPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPort.gridx = 1;
		gbc_txtPort.gridy = 1;
		contentPane.add(txtPort, gbc_txtPort);
		
		txtPort.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				int value = (int) txtPort.getValue();
				if (value < 0) {
					txtPort.setValue(-value);
				}
			}
		});
		
		lblSentence = new JLabel("Sentence:");
		GridBagConstraints gbc_lblSentence = new GridBagConstraints();
		gbc_lblSentence.insets = new Insets(0, 0, 5, 0);
		gbc_lblSentence.gridx = 0;
		gbc_lblSentence.gridy = 2;
		gbc_lblSentence.gridwidth = 2;
		gbc_lblSentence.anchor = GridBagConstraints.WEST;
		contentPane.add(lblSentence, gbc_lblSentence);
		
		scrollPaneRight = new JScrollPane();
		GridBagConstraints gbc_scrollPaneRight = new GridBagConstraints();
		gbc_scrollPaneRight.insets = new Insets(0, 0, 15, 0);
		gbc_scrollPaneRight.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneRight.gridx = 0;
		gbc_scrollPaneRight.gridy = 3;
		gbc_scrollPaneRight.gridwidth = 2;
		contentPane.add(scrollPaneRight, gbc_scrollPaneRight);
		
		textAreaSentence = new JTextArea();
		scrollPaneRight.setViewportView(textAreaSentence);
		
		lblTimeout = new JLabel("Timeout (ms):");
		GridBagConstraints gbc_lblTimeout = new GridBagConstraints();
		gbc_lblTimeout.anchor = GridBagConstraints.WEST;
		gbc_lblTimeout.insets = new Insets(0, 0, 5, 10);
		gbc_lblTimeout.gridx = 0;
		gbc_lblTimeout.gridy = 4;
		contentPane.add(lblTimeout, gbc_lblTimeout);
		
		txtTimeout = new JFormattedTextField(new Integer(5000));
		GridBagConstraints gbc_txtTimeout = new GridBagConstraints();
		gbc_txtTimeout.insets = new Insets(0, 0, 10, 0);
		gbc_txtTimeout.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTimeout.gridx = 1;
		gbc_txtTimeout.gridy = 4;
		contentPane.add(txtTimeout, gbc_txtTimeout);
		
		txtTimeout.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				int value = (int) txtTimeout.getValue();
				if (value < 0) {
					txtTimeout.setValue(-value);
				} else if (value == 0) {
					txtTimeout.setValue(1);
				}
			}
		});
		
		rdbtnAddSentence = new JRadioButton("Add sentence");
		GridBagConstraints gbc_rdbtnAddSentence = new GridBagConstraints();
		gbc_rdbtnAddSentence.insets = new Insets(5, 10, 5, 0);
		gbc_rdbtnAddSentence.gridx = 0;
		gbc_rdbtnAddSentence.gridy = 5;
		gbc_rdbtnAddSentence.gridwidth = 2;
		gbc_rdbtnAddSentence.anchor = GridBagConstraints.WEST;
		contentPane.add(rdbtnAddSentence, gbc_rdbtnAddSentence);
		
		rdbtnRemoveSentence = new JRadioButton("Remove sentence");
		GridBagConstraints gbc_rdbtnRemoveSentence = new GridBagConstraints();
		gbc_rdbtnRemoveSentence.insets = new Insets(0, 10, 5, 0);
		gbc_rdbtnRemoveSentence.gridx = 0;
		gbc_rdbtnRemoveSentence.gridy = 6;
		gbc_rdbtnRemoveSentence.gridwidth = 2;
		gbc_rdbtnRemoveSentence.anchor = GridBagConstraints.WEST;
		contentPane.add(rdbtnRemoveSentence, gbc_rdbtnRemoveSentence);
		
		rdbtnUpdate = new JRadioButton("Update sentence");
		GridBagConstraints gbc_rdbtnUpdate = new GridBagConstraints();
		gbc_rdbtnUpdate.insets = new Insets(0, 10, 5, 0);
		gbc_rdbtnUpdate.gridx = 0;
		gbc_rdbtnUpdate.gridy = 7;
		gbc_rdbtnUpdate.gridwidth = 2;
		gbc_rdbtnUpdate.anchor = GridBagConstraints.WEST;
		contentPane.add(rdbtnUpdate, gbc_rdbtnUpdate);
		
		ButtonGroup rdbGroup = new ButtonGroup();
		rdbGroup.add(rdbtnAddSentence);
		rdbGroup.add(rdbtnRemoveSentence);
		rdbGroup.add(rdbtnUpdate);
		
		rdbtnAddSentence.setSelected(true);
		
		btnExecute = new JButton("Execute");
		GridBagConstraints gbc_btnExecute = new GridBagConstraints();
		gbc_btnExecute.insets = new Insets(10, 0, 0, 0);
		gbc_btnExecute.gridx = 1;
		gbc_btnExecute.gridy = 8;
		gbc_btnExecute.anchor = GridBagConstraints.EAST;
		contentPane.add(btnExecute, gbc_btnExecute);
		
		rdbtnAddSentence.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtTimeout.setEnabled(true);
			}
		});
		
		rdbtnRemoveSentence.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtTimeout.setEnabled(false);
			}
		});
		
		rdbtnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtTimeout.setEnabled(true);
			}
		});
		
		btnExecute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        stop();
		    }
		});
		
		update = false;
		old = "";
	}
	
	private void error() {
		JOptionPane.showMessageDialog(null, "Communication error.", 
				"Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private void execute() {
		if (!txtHost.getText().trim().equals("")) {
			try {
				client = new Socket(txtHost.getText(), (int) txtPort.getValue());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Host communication error.", 
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (!textAreaSentence.getText().trim().equals("")) {
				if (rdbtnAddSentence.isSelected()) {
					addSentence(textAreaSentence.getText().trim(), (int) txtTimeout.getValue());
				} else if (rdbtnRemoveSentence.isSelected()) {
					removeSentence(textAreaSentence.getText().trim());
				} else if (rdbtnUpdate.isSelected()) {
					if (update) {
						txtHost.setEnabled(true);
						txtPort.setEnabled(true);
						txtTimeout.setEnabled(true);
						rdbtnAddSentence.setEnabled(true);
						rdbtnRemoveSentence.setEnabled(true);
						rdbtnUpdate.setEnabled(true);
						update = !update;
						updateSentence(old, textAreaSentence.getText().trim(), 
								(int) txtTimeout.getValue());
					} else {
						old = textAreaSentence.getText().trim();
						txtHost.setEnabled(false);
						txtPort.setEnabled(false);
						txtTimeout.setEnabled(false);
						rdbtnAddSentence.setEnabled(false);
						rdbtnRemoveSentence.setEnabled(false);
						rdbtnUpdate.setEnabled(false);
						talkToSocket(";;;;;");
						textAreaSentence.setText("Enter new sentence...");
						update = !update;
					}		
				}
			} else {
				talkToSocket(";;;;;");
				JOptionPane.showMessageDialog(null, "Sentence field must not be empty.");
			}
		} else {
			JOptionPane.showMessageDialog(null, "Host address must be specified.");
		}
	}
	
	private void addSentence(String sentence, int timeout) {
		talkToSocket("a:" + sentence + ";t:" + timeout + ";");
	}
	
	private void removeSentence(String sentence) {
		talkToSocket("d:" + sentence + ";");
	}
	
	private void updateSentence(String old, String sentence, int timeout) {
		talkToSocket("u:" + old + ";n:" + sentence + ";t:" + timeout + ";");
	}
	
	private void talkToSocket(String s) {
		try {
			new DataOutputStream(client.getOutputStream()).writeUTF(s);
		} catch (IOException e) {
			error();
		}
	}
	
	public void start() {
		runner = new Runnable() {
			public void run() {
				try {
					setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		EventQueue.invokeLater(runner);
	}
	
	public void stop() {
		if (client != null && !client.isClosed()) {
			try {
				client.close();
			} catch (IOException e) {}
		}
		
		System.exit(0);
	}
	
}
