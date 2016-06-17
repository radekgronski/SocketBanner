package banner;

import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import tools.*;

public class MessagesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private String[] headers = { "Message", "Timeout (ms)" };
	List<MessageInfo> list;
	
	public MessagesTableModel() {
		list = new LinkedList<>();
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return list.size();
	}
	
	@Override
	public String getColumnName(int col) {
        return headers[col];
    }

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return list.get(rowIndex).getMessage();
		} else {
			return list.get(rowIndex).getTimeout();
		}
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {		
		if (col == 0) {
			list.get(row).setMessage((String) value);
		} else {
			list.get(row).setTimeout((int) value);
		}
		
        fireTableCellUpdated(row, col);
    }
	
	public void addMessage(String message, int milliseconds) {
		list.add(new MessageInfo(message, milliseconds));
		fireTableDataChanged();
	}
	
	public void addMessage(MessageInfo info) {
		list.add(info);
		fireTableDataChanged();
	}
	
	public int removeMessages(String s) {
		List<MessageInfo> tempList = new LinkedList<>();
		
		for (MessageInfo info : list) {
			if (!info.getMessage().equals(s)) {
				tempList.add(info);
			}
		}
		
		if (tempList.size() != list.size()) {
			int removed = list.size() - tempList.size();
			list.clear();
			list = tempList;
			fireTableDataChanged();
			return removed;
		} else {
			return 0;
		}
	}
	
	public List<String> getAllSentences() {
		List<String> sList = new LinkedList<>();
		
		for (MessageInfo info : list) {
			sList.add(info.getMessage());
		}
		
		return sList;
	}
	
	public void setAllSentences(List<MessageInfo> newList) {
		list.clear();
		list = newList;
		fireTableDataChanged();
	}
	
	public void setTimeout(String s, int timeout) {
		for (MessageInfo info : list) {
			if (info.getMessage().equals(s)) {
				info.setTimeout(timeout);
			}
		}
		
		fireTableDataChanged();
	}
	
	public void updateSentence(String oldS, String newS, int timeout) {
		for (MessageInfo info : list) {
			if (info.getMessage().equals(oldS)) {
				info.setMessage(newS);
				info.setTimeout(timeout);
			}
		}
		
		fireTableDataChanged();
	}
	
	public MessageInfo popMessage() {
		if (list.size() == 0) {
			return null;
		}
		
		MessageInfo info = list.get(0);
		list.remove(0);
		fireTableDataChanged();
		
		return info;
	}

}
