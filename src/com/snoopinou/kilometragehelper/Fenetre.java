package com.snoopinou.kilometragehelper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Fenetre extends JFrame{
	
	private Path noms = Paths.get("resources/Noms.txt");
	private Path distances = Paths.get("resources/Distances.txt");
	
	public static ArrayList<String> tabNoms = new ArrayList<String>();;
	public static Object[][] tabDistances;
	
	private JPanel contentPane = new JPanel();;
	private static JTable tableau;
	private static DefaultTableModel tm;
	private JScrollPane scrollPane;
	private RemoveListener removeListener = new RemoveListener();
	
	private JMenuBar menuBar = new JMenuBar();
	
	private JMenu file = new JMenu("File");
	private JMenuItem newDestination = new JMenuItem("Nouvelle Destinations");
	private JMenuItem save = new JMenuItem("Save");
	private JMenuItem quit = new JMenuItem("Quit");
	
	private JMenuItem help = new JMenuItem("Help") {
		public Dimension getMaximumSize() {
			Dimension d1 = super.getPreferredSize();
            Dimension d2 = super.getMaximumSize();
            d2.width = d1.width;
            return d2;
		}
	};
	public Fenetre() {
		
		initData();
		initVisu();
		
		this.setTitle("Helper");
		this.setSize(1000,350);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		this.setVisible(true);
		
	}
	
	
	
	private void initData() {
		
		try {
			if(!noms.toFile().exists()) { // SI fichier existe pas -> on cree
				Files.createDirectories(noms.getParent());
				Files.createFile(noms);
			}
			if(!distances.toFile().exists()) {
				Files.createDirectories(distances.getParent());
				Files.createFile(distances);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try(BufferedReader brNoms = Files.newBufferedReader(noms); BufferedReader brDistances = Files.newBufferedReader(distances)){
			
			while(brNoms.ready()) {
				tabNoms.add(brNoms.readLine());
			}
			
			tabDistances = new Object[tabNoms.size()][tabNoms.size()];
			int i = 0;
			while(brDistances.ready()) {
				String ligne = brDistances.readLine();
				int pos = 0; // pos dans le string de la ligne
				int j = 0; // POur les colonnes
				while(pos < ligne.length()-1) {
					int debut = pos+1;
					int fin = ligne.indexOf("|", pos+1);
					
					int dist = Integer.parseInt(ligne.substring(debut, fin));
					
					tabDistances[i][j] = dist;
					j++;
					pos = fin;
				}
				
				i++; // Pour les lignes
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	private void initVisu() {
		
		
		contentPane.setLayout(new BorderLayout());
		
		
		tm = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return column >= 1;
			}
		};
		
		actuTable();
		
		tableau = new JTable(tm);
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		tableau.setDefaultRenderer(Object.class, renderer);
		
		for(int j = 1; j < tableau.getColumnCount(); j++) {
			int minWidth = SwingUtilities.computeStringWidth(tableau.getFontMetrics(tableau.getFont()), tableau.getColumnName(j));
			
			tableau.getColumnModel().getColumn(j).setMinWidth(minWidth);
			tableau.getColumnModel().getColumn(j).setPreferredWidth(minWidth);
		}
		
		tableau.getColumnModel().getColumn(0).setCellRenderer(tableau.getTableHeader().getDefaultRenderer());
		tableau.getColumnModel().getColumn(0).setMinWidth(100);
		tableau.getTableHeader().setReorderingAllowed(false);
		tableau.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		tableau.setRowSelectionAllowed(false);
		tableau.setColumnSelectionAllowed(false);
		
		tableau.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			@Override
			public void columnAdded(TableColumnModelEvent e) {
				tableau.getColumnModel().getColumn(0).setCellRenderer(tableau.getTableHeader().getDefaultRenderer());
				tableau.getColumnModel().getColumn(0).setMinWidth(100);
				
				for(int j = 1; j < tableau.getColumnCount(); j++) {
					int minWidth = SwingUtilities.computeStringWidth(tableau.getFontMetrics(tableau.getFont()), tableau.getColumnName(j));
					
					tableau.getColumnModel().getColumn(j).setMinWidth(minWidth);
					tableau.getColumnModel().getColumn(j).setPreferredWidth(minWidth);
				}
			}
			
			@Override
			public void columnMarginChanged(ChangeEvent e) {}
			@Override
			public void columnMoved(TableColumnModelEvent e) {}
			@Override
			public void columnRemoved(TableColumnModelEvent e) {}
			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {}
			
		});
				
		scrollPane = new JScrollPane(tableau);
		
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		
		newDestination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = new DialogAdd(null, "Nouvelle destination", true).showDialog();
				
				tabNoms.add(name);
				tm.addColumn(name);
				tm.addRow(new Object[tableau.getColumnCount()]);
				
				tm.setValueAt(name, tm.getRowCount()-1, 0);
				for(int i = 1; i < tm.getColumnCount(); i++) {
					tm.setValueAt(0, tm.getRowCount()-1, i); // Filling last line
					tm.setValueAt(0, i-1, tm.getColumnCount()-1); // Filling Last Column
				}		
				
			}
		});
		
		
		
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actuTabDistances();
				String toWrite = "|";
				try {
					for(int i = 0; i < tabDistances.length;i++) {
						for(int j = 1; j <= tabDistances.length; j++) {
							toWrite += Math.abs(Integer.valueOf(tableau.getValueAt(i, j).toString()))+"|"; // To be sure not having a negative value
						}
						toWrite +="\n|";
					}
				}catch(NumberFormatException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Veuillez entrer uniquement des nombres dans toutes les cases", "Erreur lors de la sauvegarde", JOptionPane.ERROR_MESSAGE);
					toWrite = "";
				}
				if(!(toWrite == "")) {					
					toWrite = toWrite.substring(0, toWrite.lastIndexOf("\n")); // Remove last empty lline
				}
				
				save(toWrite);
				
			}
		});
		
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Pour supprimer une destination, faites Ctrl+click sur la destination que vous souhaiter supprimer", "Aide", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		
		tableau.addMouseListener(removeListener);
		tableau.getTableHeader().addMouseListener(removeListener);

		file.add(newDestination);
		file.addSeparator();
		file.add(save);
		file.addSeparator();
		file.add(quit);
		
		
		menuBar.add(file);
		menuBar.add(help);
		
		this.setJMenuBar(menuBar);
		
		this.setContentPane(contentPane);
		
	}
	
	
	public void save(String toWrite) {
		
		
		try(BufferedWriter bw = Files.newBufferedWriter(distances)){
			bw.write(toWrite);					
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		
		try(BufferedWriter bw = Files.newBufferedWriter(noms)){
			String temp = "";
			for(String str : tabNoms) {
				temp += str+"\n";
			}
			bw.write(temp);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void remove(String str) {
		int index = 0;

		index = tabNoms.indexOf(str);
		tabNoms.remove(index);
		
		tm.removeRow(index);
		tableau.removeColumn(tableau.getColumnModel().getColumn(index+1));
		
		actuTabDistances();
	}
	
	private static void actuTabDistances() {
		tabDistances = new Object[tabNoms.size()][tabNoms.size()];
		for(int i = 0; i < tabDistances.length;i++) {
			for(int j = 1; j < tabDistances.length; j++) {
				tabDistances[i][j] = Math.abs(Integer.valueOf(tableau.getValueAt(i, j).toString()));
			}
		}
	}
	
	private void actuTable() {
		tm.addColumn("", tabNoms.toArray());
		int i = 0;
		for(Object[] tab : tabDistances) {
			tm.addColumn(tabNoms.get(i), tab);
			i++;
		}
	}
}









