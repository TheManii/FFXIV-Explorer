package com.fragmenterworks.ffxivextract.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.fragmenterworks.ffxivextract.models.ParameterInfo;
import com.fragmenterworks.ffxivextract.models.SHCD_File;
import com.fragmenterworks.ffxivextract.models.SHPK_File;
import com.fragmenterworks.ffxivextract.models.directx.D3DXShader_ConstantTable;

public class Shader_View extends JPanel {

	JTree treeParameters;
	JComboBox cmbShaderIndex;
	Hex_View hexView;
	
	SHCD_File shader = null;
	SHPK_File shaderPack = null;

	DefaultMutableTreeNode root;
	
	JPanel panel;
	
	public Shader_View(SHCD_File shader) {
				
		this.shader = shader;
		
		initUi();
		
		cmbShaderIndex.addItem(shader.getShaderType() == 0 ? "Vertex Shader #0" : "Pixel Shader #0");
		cmbShaderIndex.setEnabled(false);
				
		loadShader(0);
	}	

	/**
	 * @wbp.parser.constructor
	 */
	public Shader_View(SHPK_File shaderPack) {		
				
		this.shaderPack = shaderPack;
		
		initUi();
		
		String[] list = new String[shaderPack.getNumVertShaders() + shaderPack.getNumPixelShaders()];
		
		for (int i = 0; i < shaderPack.getNumVertShaders(); i++)
			cmbShaderIndex.addItem("Vertex Shader #" + i);
		
		for (int i = 0; i < shaderPack.getNumPixelShaders(); i++) 
			cmbShaderIndex.addItem("Pixel Shader #" + i);
		
		cmbShaderIndex.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
		          loadShader(cmbShaderIndex.getSelectedIndex());
			    }
			}
		});
		
		loadShader(0);
	}

	private void loadShader(int i) {
		if (shader != null)
		{			
			processCTable(0, shader.getShaderType(), shader.getConstantTable());						
			hexView.setBytes(shader.getShaderBytecode());
			
			for (ParameterInfo pi:shader.getShaderHeader().paramInfo)
				System.out.println(String.format("Name: %s, Id: 0x%04x", pi.parameterName, pi.id));
		}
		else
		{			
			processCTable(i, shaderPack.getShaderType(i), shaderPack.getConstantTable(i));
			hexView.setBytes(shaderPack.getShaderBytecode(i));
			
			for (ParameterInfo pi:shaderPack.getShaderHeader(i).paramInfo)
				System.out.println(String.format("Name: %s, Id: 0x%04x", pi.parameterName, pi.id));
		}
	}

	private void processCTable(int num, int type, D3DXShader_ConstantTable ctab)
	{				
		DefaultMutableTreeNode root =  new DefaultMutableTreeNode();
		root.setUserObject(type == 0 ? "Vertex Shader":"Pixel Shader");
		
		for (int i = 0; i < ctab.constantInfo.length; i++)
		{
			String paramId = "";
			
			if (shader == null){
				for (int j = 0; j < shaderPack.getShaderHeader(num).paramInfo.length; j++){
					if (shaderPack.getShaderHeader(num).paramInfo[j].parameterName.equals(ctab.constantInfo[i].Name)){
						paramId = String.format("0x%x",shaderPack.getShaderHeader(num).paramInfo[j].id);
						break;
					}
				}
			}
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(ctab.constantInfo[i].Name + "["+ ctab.constantInfo[i].TypeInfo.Columns +"x"+ ctab.constantInfo[i].TypeInfo.Rows +"]" + " Index: " + ctab.constantInfo[i].RegisterIndex + ", ParamId: " + paramId);
			root.add(node);
			
			for (int j = 0; j < ctab.constantInfo[i].TypeInfo.StructMemberInfo.length; j++)
			{
				DefaultMutableTreeNode node2 = new DefaultMutableTreeNode(ctab.constantInfo[i].TypeInfo.StructMemberInfo[j].Name + "["+ ctab.constantInfo[i].TypeInfo.StructMemberInfo[j].TypeInfo.Columns +"x"+ ctab.constantInfo[i].TypeInfo.StructMemberInfo[j].TypeInfo.Rows +"]");
				node.add(node2);
			}
		}
		treeParameters = new JTree(root);
		panel.removeAll();
		panel.add(treeParameters);
		treeParameters.updateUI();
	}	
	
	private void initUi() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new EmptyBorder(0, 5, 0, 0));
		FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_2.add(panel_3, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Shader: ");
		panel_3.add(lblNewLabel);
		
		cmbShaderIndex = new JComboBox();
		panel_3.add(cmbShaderIndex);
		
		panel = new JPanel();
		panel_2.add(panel, BorderLayout.CENTER);
		panel.setBorder(new TitledBorder(null, "Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 300));
		panel.add(scrollPane, BorderLayout.NORTH);
		
		
		treeParameters = new JTree(new DefaultMutableTreeNode());		
		treeParameters.setShowsRootHandles(true);				

		treeParameters.setVisibleRowCount(8);
		
		scrollPane.setViewportView(treeParameters);
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Bytecode", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		hexView = new Hex_View(16);
		panel_1.add(hexView);
	}

}
