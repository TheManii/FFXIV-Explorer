package com.fragmenterworks.ffxivextract.gui.modelviewer;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.fragmenterworks.ffxivextract.gui.components.EXDF_View;

public class ItemChooserDialog extends JDialog {

	public static final int INDEX_ITEM_NAME = 10;
	public static final int INDEX_ITEM_MODEL1 = 29;
	public static final int INDEX_ITEM_MODEL2 = 30;
	public static final int INDEX_ITEM_SLOT = 28;

	private JTextField edtSearch;
	private JList lstItems;
	private int chosenItem = -2;

	ArrayList<ItemIdCombo> masterItemList = new ArrayList<ItemIdCombo>();
	ArrayList<ItemIdCombo> filterItemList = new ArrayList<ItemIdCombo>();

	public ItemChooserDialog(JFrame parent, EXDF_View itemView, int slot) {

		setModal(true);
		setLocationRelativeTo(parent);
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(2, 2, 2, 0));
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		ActionListener searchListener =	new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String filter = edtSearch.getText();

				filterItemList.clear();

				for (int i = 0; i < masterItemList.size(); i++) {
					if (masterItemList.get(i).name.toLowerCase().contains(
							filter.toLowerCase()))
						filterItemList.add(masterItemList.get(i));
				}

				((ItemsListModel) lstItems.getModel()).refresh();
			}
		};
		
		JLabel lblSearch = new JLabel("Search:");
		panel.add(lblSearch, BorderLayout.WEST);

		edtSearch = new JTextField();
		panel.add(edtSearch, BorderLayout.CENTER);
		edtSearch.setColumns(10);
		edtSearch.addActionListener(searchListener);

		JButton btnSearch = new JButton("Search");
		panel.add(btnSearch, BorderLayout.EAST);
		btnSearch.addActionListener(searchListener);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		lstItems = new JList();
		scrollPane.setViewportView(lstItems);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);

		JButton btnSet = new JButton("Set Slot");
		panel_1.add(btnSet);
		btnSet.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chosenItem = ((ItemIdCombo) lstItems.getSelectedValue()).id;
				setVisible(false);
			}
		});

		JButton btnClear = new JButton("Clear Slot");
		panel_1.add(btnClear);
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chosenItem = -1;
				setVisible(false);
			}
		});

		lstItems.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();

				if (evt.getClickCount() == 2) {
					Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex());
					if (r != null && r.contains(evt.getPoint())) {
						int index = list.locationToIndex(evt.getPoint());
						list.setSelectedIndex(index);
						chosenItem = ((ItemIdCombo) lstItems.getSelectedValue()).id;
						setVisible(false);
					}
				} 
			}
		});

		// Load items
		for (int i = 0; i < itemView.getTable().getRowCount(); i++) {
			int iSlot = (Integer) itemView.getTable().getValueAt(i,
					INDEX_ITEM_SLOT);
			String iName = (String) itemView.getTable().getValueAt(i,
					INDEX_ITEM_NAME);

			if (iSlot == slot)
				masterItemList.add(new ItemIdCombo(iName, i));
		}

		filterItemList.addAll(masterItemList);

		lstItems.setModel(new ItemsListModel());

	}

	public static int showDialog(JFrame parent, EXDF_View itemView, int slot) {
		ItemChooserDialog d = new ItemChooserDialog(parent, itemView, slot);
		d.setTitle(getTitle(slot));
		d.pack();
		d.setVisible(true);
		return d.getChosenItem();
	}

	private int getChosenItem() {
		return chosenItem;
	}

	private static String getTitle(int slot) {
		return "Choose body item";
	}

	private class ItemsListModel extends AbstractListModel {
		public int getSize() {

			return filterItemList.size();
		}

		public ItemIdCombo getElementAt(int index) {
			return filterItemList.get(index);
		}

		public void refresh() {
			fireContentsChanged(this, 0, filterItemList.size());
		}

	}

	private class ItemIdCombo {
		public String name;
		public int id;

		public ItemIdCombo(String name, int id) {
			this.name = name;
			this.id = id;
		}

		@Override
		public String toString() {
			return name;
		}
	}

}
