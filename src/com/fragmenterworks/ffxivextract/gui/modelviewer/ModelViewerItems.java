package com.fragmenterworks.ffxivextract.gui.modelviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.FlowLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import com.fragmenterworks.ffxivextract.gui.components.EXDF_View;
import com.fragmenterworks.ffxivextract.gui.components.ModelRenderer;
import com.fragmenterworks.ffxivextract.gui.components.OpenGL_View;
import com.fragmenterworks.ffxivextract.helpers.SparseArray;
import com.fragmenterworks.ffxivextract.models.Model;
import com.fragmenterworks.ffxivextract.models.SqPack_IndexFile;
import com.fragmenterworks.ffxivextract.storage.HashDatabase;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

public class ModelViewerItems extends JPanel {
	
	public static final int INDEX_ITEM_NAME = 10;
	public static final int INDEX_ITEM_MODEL1 = 54;
	public static final int INDEX_ITEM_MODEL2 = 55;
	public static final int INDEX_ITEM_SLOT = 18;
	
	ModelViewerWindow parent;
	
	ArrayList<ModelItemEntry> entries[] = new ArrayList[22];
	
	ArrayList<ModelItemEntry> filteredEntries = new ArrayList<ModelItemEntry>();
	
	ModelItemEntry addedItems[] = new ModelItemEntry[22];
	
	SparseArray<String> slots = new SparseArray<String>();
	
	SparseArray<String> charIds = new SparseArray<String>();

	int currentBody = 01;
	int currentCategory = 0;
	
	OpenGL_View view3D;
	JList lstItems;	
	JComboBox cmbBodyStyle;
	JComboBox cmbCategory;
	FPSAnimator animator;
	
	//Info Section
	JLabel txtPath;
	JLabel txtModelInfo;
	JButton btnColorSet;
	JButton btnResetCamera;
	JCheckBox chkGlowToggle;
	JButton btnSearch;
	JTextField edtSearch;	
	
	ModelRenderer renderer;
	
	private boolean leftMouseDown = false;
	private boolean rightMouseDown = false;
	
	private int currentLoD = 0;
	private int lastOriginX, lastOriginY;
	private int lastX, lastY;	
	
	SqPack_IndexFile modelIndexFile;
	EXDF_View itemView;
	
	public ModelViewerItems(ModelViewerWindow parent, SqPack_IndexFile modelIndex, EXDF_View itemView) {
		
		this.parent = parent;
		this.modelIndexFile = modelIndex;
		this.itemView = itemView;
		
		//Fill the Equipment Slots
		slots.append(1, "One-Handed Weapon");
		slots.append(13, "Two-Handed Weapon");
		slots.append(2, "Offhand");
		slots.append(3, "Head");
		slots.append(4, "Body");
		slots.append(5, "Hands");
		slots.append(7, "Legs");
		slots.append(8, "Feet");
		slots.append(9, "Earings");
		slots.append(10, "Necklace");
		slots.append(11, "Wrists");
		slots.append(12, "Rings");
		
		slots.append(15, "Body + Head");
		slots.append(16, "All - Head");
		//slots.append(17, "Soulstone");
		slots.append(18, "Legs + Feet");
		slots.append(19, "All");
		slots.append(20, "Body + Hands");
		slots.append(21, "Body + Legs + Feet");
		
		slots.append(0, "Food");
		
		//Fill the char ids		
		charIds.append(1, "Midlander Male");
		charIds.append(2, "Midlander  Female");
		charIds.append(3, "Highlander Male");
		charIds.append(4, "Highlander Female");
		charIds.append(5, "Elezen Male");
		charIds.append(6, "Elezen Female");
		charIds.append(7, "Miqo'te Male");
		charIds.append(8, "Miqo'te Female");
		charIds.append(9, "Roegadyn Male");
		charIds.append(10, "Roegadyn  Female");
		charIds.append(11, "Lalafell Male");
		charIds.append(12, "Lalafell Female");
		
		for (int i = 0; i < entries.length; i++)
			entries[i] = new ArrayList<ModelItemEntry>();
		
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.NORTH);
		panel_2.setBorder(new TitledBorder(null, "Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		
		JPanel panelInfo_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelInfo_1.getLayout();
		flowLayout.setVgap(1);
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_2.add(panelInfo_1);
		
		JLabel lblBleh = new JLabel("Path: ");
		panelInfo_1.add(lblBleh);
		
		txtPath = new JLabel("-");
		panelInfo_1.add(txtPath);
		
		JPanel panelInfo_2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelInfo_2.getLayout();
		flowLayout_1.setVgap(1);
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_2.add(panelInfo_2);
		
		JLabel label = new JLabel("Info: ");
		panelInfo_2.add(label);
		
		txtModelInfo = new JLabel("Type: -, Id: -, Model: -, Variant: -");
		panelInfo_2.add(txtModelInfo);
		
		JPanel panelInfo_3 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panelInfo_3.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panel_2.add(panelInfo_3);
		
		btnResetCamera = new JButton("Reset Camera");
		panelInfo_3.add(btnResetCamera);
		
		chkGlowToggle = new JCheckBox("Glow Shader", true);
		panelInfo_3.add(chkGlowToggle);
			
		chkGlowToggle.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				renderer.toggleGlow(chkGlowToggle.isSelected());
			}
		});
		
		btnResetCamera.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				renderer.resetCamera();
			}
		});
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Items", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_4 = new JPanel();
		panel.add(panel_4, BorderLayout.NORTH);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));
		
		JPanel panel_5 = new JPanel();
		panel_5.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_5.setBorder(new EmptyBorder(2, 1, 0, 1));
		panel_4.add(panel_5);
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.X_AXIS));
		
		cmbBodyStyle = new JComboBox();
		panel_5.add(cmbBodyStyle);
		
		cmbBodyStyle.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
		          int selected = cmbBodyStyle.getSelectedIndex();		          
		          currentBody = charIds.keyAt(selected);
		          
		          if (currentCategory != -1 && lstItems.getSelectedIndex() != -1)
		        	  loadModel(-1, lstItems.getSelectedIndex());
		          
				}
			}
		});
		
		JPanel panel_6 = new JPanel();
		panel_6.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_6.setBorder(new EmptyBorder(2, 1, 2, 1));
		panel_4.add(panel_6);
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.X_AXIS));
		
		cmbCategory = new JComboBox();
		panel_6.add(cmbCategory);
		
		cmbCategory.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
		          int selected = cmbCategory.getSelectedIndex();
		          
		          currentCategory = slots.keyAt(selected);
		          		          
		          filteredEntries.clear();
		          filteredEntries.addAll(entries[currentCategory]);
		          ((ItemsListModel)lstItems.getModel()).refresh();
				}
			}
		});
		
		JPanel panel_7 = new JPanel();
		panel_7.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_4.add(panel_7);
		panel_7.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);
		
		lstItems = new JList();
		
		scrollPane.setViewportView(lstItems);

		ActionListener searchListener =	new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentCategory == -1)
					return;
				
				lstItems.clearSelection();
				
				String filter = edtSearch.getText();

				filteredEntries.clear();

				for (int i = 0; i < entries[currentCategory].size(); i++) {
					if (entries[currentCategory].get(i).name.toLowerCase().contains(filter.toLowerCase()))
						filteredEntries.add(entries[currentCategory].get(i));
				}

				((ItemsListModel) lstItems.getModel()).refresh();
			}
		};
		
		edtSearch = new JTextField();
		edtSearch.addActionListener(searchListener);
		edtSearch.setColumns(10);
		
		btnSearch = new JButton("Search");
		btnSearch.addActionListener(searchListener);	
		
		JPanel panel_8 = new JPanel();
		panel.add(panel_8, BorderLayout.SOUTH);
		panel_8.setLayout(new BorderLayout(0, 0));
		
		panel_8.add(edtSearch, BorderLayout.CENTER);
		panel_8.add(btnSearch, BorderLayout.EAST);
		
		GLProfile glProfile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glProfile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );
        renderer = new ModelRenderer();
        glcanvas.addGLEventListener(renderer);
        animator = new FPSAnimator(glcanvas, 30);
        animator.start();
        glcanvas.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (leftMouseDown)
				{					
					renderer.pan((e.getX() - lastX), (e.getY() - lastY));
					lastX = e.getX();
					lastY = e.getY();
				}
				if (rightMouseDown)
				{
					renderer.rotate(e.getX() - lastX, e.getY() - lastY);
					lastX = e.getX();
					lastY = e.getY();
				}
			}
		});
        glcanvas.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)				
					leftMouseDown = false;				
				if (e.getButton() == MouseEvent.BUTTON3)
					rightMouseDown = false;
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)	
				{
					leftMouseDown = true;
					lastOriginX = e.getX();
					lastOriginY = e.getY();
					lastX = lastOriginX;
					lastY = lastOriginY;
				}
				if (e.getButton() == MouseEvent.BUTTON3)
				{
					rightMouseDown = true;
					lastOriginX = e.getX();
					lastOriginY = e.getY();
					lastX = lastOriginX;
					lastY = lastOriginY;
				}
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int notches = e.getWheelRotation();		
				renderer.zoom(-notches);				
			}
		});
        
        try {
        	if (!loadItems())
        	{
        		removeAll();
    			JLabel errorLabel = new JLabel("There was an error loading the item list.");
    			add(errorLabel);
    			return;
        	}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}    
        
        for (int i = 0; i < charIds.size(); i++)
        	cmbBodyStyle.addItem(charIds.valueAt(i));
        
        for (int i = 0; i < slots.size(); i++)
        	cmbCategory.addItem(slots.valueAt(i));
        
        //Add all the slots        
        lstItems.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent event) {				
												
				if (event.getValueIsAdjusting() || lstItems.getModel().getSize() == 0 || currentCategory == -1)
					return;				
				
				int selected = lstItems.getSelectedIndex();
				
				if (selected == -1)
					return;
				
				loadModel(-1, selected);
			}
		});		
        
        panel_3.add( glcanvas, BorderLayout.CENTER);
                

        ((ItemsListModel)lstItems.getModel()).refresh();
        lstItems.clearSelection();
        
        filteredEntries.addAll(entries[currentCategory]);
        
	}
	
	private void loadModel(int charNumberOverride, int selected)
	{

		if (selected == -1)
			return;
		
		String modelPath = null;
		byte[] modelData = null;
		ModelItemEntry currentItem = filteredEntries.get(selected);
		
		int characterNumber = ((charNumberOverride == -1 ? currentBody * 100+ 01: charNumberOverride)); 
		
		try {
			
			switch (currentCategory)
			{
			case 13:
			case 0:
			case 1:
			case 2:
				modelPath = String.format("chara/weapon/w%04d/obj/body/b%04d/model/w%04db%04d.mdl", currentItem.id, currentItem.model, currentItem.id, currentItem.model);
				break;
			case 3:
				modelPath = String.format("chara/equipment/e%04d/model/c%04de%04d_met.mdl", currentItem.id, characterNumber, currentItem.id);	
				break;
			case 4:					
				modelPath = String.format("chara/equipment/e%04d/model/c%04de%04d_top.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 5:
				modelPath = String.format("chara/equipment/e%04d/model/c%04de%04d_glv.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 7:
				modelPath = String.format("chara/equipment/e%04d/model/c%04de%04d_dwn.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 8:
				modelPath = String.format("chara/equipment/e%04d/model/c%04de%04d_sho.mdl", currentItem.id, characterNumber, currentItem.id);
				break;	
			case 9:
				modelPath = String.format("chara/accessory/a%04d/model/c%04da%04d_ear.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 10:
				modelPath = String.format("chara/accessory/a%04d/model/c%04da%04d_nek.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 11:
				modelPath = String.format("chara/accessory/a%04d/model/c%04da%04d_wrs.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 12:
				modelPath = String.format("chara/accessory/a%04d/model/c%04da%04d_rir.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 15:
				modelPath = String.format("chara/equipment/e%04d/model/c%04de%04d_top.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 16:
				modelPath = String.format("chara/equipment/e%04d/model/c%04de%04d_top.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 18:
				modelPath = String.format("chara/equipment/e%04d/model/c%04de%04d_dwn.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 19:
				modelPath = String.format("chara/equipment/e%04d/model/c%04de%04d_top.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 20:
				modelPath = String.format("chara/equipment/e%04d/model/c%04de%04d_top.mdl", currentItem.id, characterNumber, currentItem.id);
				break;
			case 21:
				modelPath = String.format("chara/equipment/e%04d/model/c%04de%04d_top.mdl", currentItem.id, characterNumber, currentItem.id);
				break;			
			}
			
			modelData = modelIndexFile.extractFile(modelPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (modelData == null && (characterNumber != 101 && characterNumber != 201))
		{
			System.out.println(String.format("Model for charId %04d not detected, falling back to %s Hyur model.", characterNumber, currentBody % 2 == 0 ? "female" : "male"));
			
			if (currentBody % 2 == 0)
				loadModel(201, selected);
			else
				loadModel(101, selected);
			
			
			return;			
		}
		
		if (modelData != null)
		{
			System.out.println("Adding Entry: " + modelPath);
			HashDatabase.addPathToDB(modelPath, "040000");
			
			Model model = new Model(modelPath,modelIndexFile,modelData);
			model.loadVariant(currentItem.varient == 0 ? 1 : currentItem.varient);
			renderer.setModel(model);
		}
		
		txtPath.setText(modelPath);
		txtModelInfo.setText(String.format("Id: %d, Model: %d, Variant: %d", currentItem.id, currentItem.model, currentItem.varient));
				
	}
	
	private boolean loadItems() throws FileNotFoundException, IOException
	{		
		EXDF_View view = itemView;
		view.setLangOverride(1);
		try{
			for (int i = 0; i < view.getTable().getRowCount(); i++){
				if (view.getTable().getValueAt(i, 0) instanceof Integer && !((String)view.getTable().getValueAt(i, INDEX_ITEM_MODEL1)).equals("0, 0, 0, 0"))
				{									
					String model1Split[] = ((String)view.getTable().getValueAt(i, INDEX_ITEM_MODEL1)).split(",");
					String model2Split[] = ((String)view.getTable().getValueAt(i, INDEX_ITEM_MODEL1)).split(",");										
					
					int slot = (Integer) view.getTable().getValueAt(i, INDEX_ITEM_SLOT);
					
					String name = (String)view.getTable().getValueAt(i, INDEX_ITEM_NAME);
					int id = Integer.parseInt(model1Split[0].trim());
						
					boolean isWeap = false;
					if (slot == 0 || slot == 1 || slot == 2 || slot == 13)
						isWeap = true;
					
					int model = !isWeap ? Integer.parseInt(model1Split[2].trim()) : Integer.parseInt(model1Split[1].trim());
					int varient = !isWeap ? Integer.parseInt(model1Split[1].trim()) : Integer.parseInt(model1Split[2].trim());
					
					int type = slot;				
					entries[slot].add(new ModelItemEntry(name, id, model, varient, type));			
				}
			}		
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		lstItems.setModel(new ItemsListModel());
				
		return true;
	}
	
	private int fallback(int characterCode)
	{
		switch (characterCode)
		{
			
		}
		
		return 101;
	}
	
	class ItemsListModel extends AbstractListModel
	{			
			public int getSize() {
				
				if (currentCategory == -1)
					return 0;
				
				return filteredEntries.size();
			}
			public String getElementAt(int index) {
				
				if (currentCategory == -1)
					return "";
				
				return filteredEntries.get(index).name;
			}
			
			public void refresh()
			{
				if (currentCategory == -1)
					fireContentsChanged(this, 0, 0);
				else
					fireContentsChanged(this, 0, filteredEntries.size());
			}
	}
	
}
