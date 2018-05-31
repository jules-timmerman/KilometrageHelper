package com.snoopinou.kilometragehelper;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class DialogAdd extends JDialog{
	
	private JLabel label = new JLabel("Entrez le nom de la nouvelle destination : ");
	private JTextField jtf = new JTextField();
	private JButton button = new JButton("OK");

	public DialogAdd(JFrame parent, String title, boolean modal) {
		super(parent,title,modal);
		
		this.setSize(300,150);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		initComponents();
	}
	
	public String showDialog() {
		this.setVisible(true);
		return jtf.getText();
	}
	
	private void initComponents() {
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(0,1));
		
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		jtf.setHorizontalAlignment(SwingConstants.CENTER);
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		contentPane.add(label);
		contentPane.add(jtf);
		contentPane.add(button);
		
		this.setContentPane(contentPane);
		
		
	}
	
}
