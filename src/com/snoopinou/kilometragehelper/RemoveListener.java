package com.snoopinou.kilometragehelper;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class RemoveListener extends MouseAdapter{
	
	JTable tableau = null;
	JTableHeader header = null;
	
	String str;
	
	public RemoveListener() {
		super();
	}
	
	public void mouseReleased(MouseEvent e) { // Actually used in Windows
		if(e.isControlDown()) {
			doIt(e);
		}
	}
	
	
	
	private void doIt(MouseEvent e) {
		
		
		str = null;
		
		if(e.getSource() instanceof JTable) {
			tableau = (JTable) e.getSource();
		}else {
			header = (JTableHeader) e.getSource();
			tableau = header.getTable();
		}
		
		if(tableau != null) {
			for(int i = 0; i < tableau.getRowCount(); i++)
				if(tableau.getCellRect(i, 0, false).contains(e.getPoint())) {
					str = (String) tableau.getValueAt(i, 0);
				}
		}
		
		if(header != null) {
			for(int i = 1; i < header.getColumnModel().getColumnCount();i++) {
				if(header.getHeaderRect(i).contains(e.getPoint())) {
					str = (String) header.getColumnModel().getColumn(i).getHeaderValue();
				}
			}
		}

		Fenetre.remove(str);
	
	}
}
