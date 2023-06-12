package frameBuild;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import aStar.aStarAlgo;
import aStar.aStarAlgoV2;
import start.start;



public class planeBuild extends JFrame{
	
	public static JPanel nodeContainer;
	private final int N;
	private Dimension buttonSize = new Dimension(100, 120);
	private boolean blocksON = true;
	private boolean showPON = true;
	
	//Information for A*
	private String startNode = "";
	private int startPosition = -1;
	private String targetNode = "";
	private int targetPosition = -1;
	private ArrayList<String> blockedNodes;
	
	
	public planeBuild(int n){ 
		this.N = n;
		this.setFrame();	
		blockedNodes = new ArrayList<String>();
	}
	
	private void setFrame(){
		this.setLayout(new BorderLayout());
		this.add(this.buttons(), BorderLayout.PAGE_START);
		this.add(this.createBoard(), BorderLayout.CENTER);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	private JPanel createBoard(){
		planeBuild.nodeContainer = new JPanel();
		planeBuild.nodeContainer.setLayout(new GridLayout(this.N, this.N));
		int position = 0;
		
		for(int x = 0; x < this.N; x++)
		{
			for(int y = 0; y < this.N; y++)
			{
				JButton temp = new JButton("");
				temp.setName(position + "_" + x + "-" + y);
				temp.setPreferredSize(new Dimension(30, 30));
							
				//TESTING PURPOSES;
				temp.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(temp.getName() + "\n");
					}
				});
				
				planeBuild.nodeContainer.add(temp);
				position++;
			}
		}
		return planeBuild.nodeContainer;
	}
	
	
	private JPanel buttons() {
		JPanel container = new JPanel();
		
		container.add(this.placeA());
		container.add(this.placeB());
		container.add(this.placeObstructions());
		container.add(this.showPath());
		container.add(this.reset());
		container.add(this.newBoard());
		container.setLayout(new FlowLayout());
		container.setBorder(new EmptyBorder(2, 0, 10, 0));
		return container;
	}
	
	
	private JButton showPath() {
		JButton showPath = new JButton("Show Path");
		showPath.setSize(this.buttonSize);
		
		showPath.addActionListener(new ActionListener() {	
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if( ( !(startNode.equals("")) && !(startPosition == -1) ) && ( !(targetNode.equals("")) && !(targetPosition == -1) ) )//&& !blocksON )
				{
					showPON = false;
					long startTime = System.nanoTime();
					aStarAlgo temp = new aStarAlgo();
					//aStarAlgoV2 temp = new aStarAlgoV2();
					highlightPath(temp.setup(blockedNodes, startNode, targetNode, N));
					long endTime = System.nanoTime();
					System.out.println("TIME TO RUN CODE: " + (endTime - startTime)/1000000 + " MS");
					
				}
			}
		});
		
		return showPath;
	}
	
	
	private JButton placeA() {
		JButton A = new JButton("Place Start");
		A.setSize(this.buttonSize);
		
		A.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < nodeContainer.getComponentCount(); i++)
				{
					JButton node = (JButton) nodeContainer.getComponent(0);
					nodeContainer.remove(0);
					
					node.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if(startNode.equals("") && startPosition == -1)
							{
								node.setBackground(Color.green);
								startNode = node.getName().split("_")[1];
								startPosition = Integer.parseInt(node.getName().split("_")[0]);
							}
							else if((!startNode.equals("") && !(startPosition == -1)) && targetNode.equals("") && targetPosition == -1)
							{
								nodeContainer.getComponent(startPosition).setBackground(null);
								node.setBackground(Color.green);
								startNode = node.getName().split("_")[1];
								startPosition = Integer.parseInt(node.getName().split("_")[0]);
							}
						}
					});
					nodeContainer.add(node);
				}
				A.setEnabled(false);
			}
		});
		return A;
	}

	
	private JButton placeB() {
		JButton B = new JButton("Place End");
		B.setSize(this.buttonSize);
		
		//IMPORTANT NOTE: B CANNONT BE PLACED UNTIL A HAS BEEN PLACED. THUS, DO NOT ACTIVATE THE 
		//'SELECT TARGET' STATE UNTIL IT IS NOTED THAT 'A' HAS BEEN SELECTED 
		B.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!(startNode.equals("")) && !(startPosition == -1)) {
					for(int i = 0; i < nodeContainer.getComponentCount(); i++)
					{
						JButton node = (JButton) nodeContainer.getComponent(0);
						nodeContainer.remove(0);
						
						node.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								if(targetNode.equals("") && targetPosition == -1)
								{
									if(node.getBackground() != Color.green) {
										node.setBackground(Color.red);
										targetNode = node.getName().split("_")[1];
										targetPosition = Integer.parseInt(node.getName().split("_")[0]);
									}
								}
								else if((!targetNode.equals("") && !(targetPosition == -1)) && blocksON && showPON)
								{
									if(node.getBackground() != Color.green)
									{
										nodeContainer.getComponent(targetPosition).setBackground(null);
										node.setBackground(Color.red);
										targetNode = node.getName().split("_")[1];
										targetPosition = Integer.parseInt(node.getName().split("_")[0]);
										System.out.println("StartNode: " + startNode + "\tTargetNode: " + targetNode);
										System.out.println("StartPosition: " + startPosition + "\tTargetPosition: " + targetPosition);
									}
								}
							}
						});
						nodeContainer.add(node);
					}
					B.setEnabled(false);
				}
			}
		});
		return B;
	}

	
	private JButton placeObstructions() {
		JButton blocks = new JButton("Place Blocks");
		blocks.setSize(this.buttonSize);
		
		blocks.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( ( !(startNode.equals("")) && !(startPosition == -1) ) && ( !(targetNode.equals("")) && !(targetPosition == -1) ) ) {
					blocksON = false;
					for(int i = 0; i < nodeContainer.getComponentCount(); i++)
					{
						JButton node = (JButton) nodeContainer.getComponent(0);
						nodeContainer.remove(0);
						
						node.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								if(Integer.parseInt(node.getName().split("_")[0]) == startPosition || Integer.parseInt(node.getName().split("_")[0]) == targetPosition)
								{}
								else if(blockedNodes.contains(node.getName()) && showPON)
								{	
									node.setBackground(null);
									blockedNodes.remove(node.getName());
								}
								else if(showPON)
								{	
									node.setBackground(Color.gray);
									blockedNodes.add(node.getName());
								}
							}
						});
						nodeContainer.add(node);
					}
					blocks.setEnabled(false);
				}
			}
		});
		
		return blocks;
	}
	
	
	private JButton reset() {
		JButton reset = new JButton("Reset");
		reset.setSize(this.buttonSize);
		
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeWindow();
				new planeBuild(N);
			}
		});
		return reset;
	}
	
	private JButton newBoard() {
		JButton newBoard = new JButton("New Board");
		newBoard.setSize(this.buttonSize);
		newBoard.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				closeWindow();
				start.main(null);
			}
		});
		return newBoard;
	}
	private void closeWindow() {this.dispose();}
	
	public static void checkedNodes(int position) { planeBuild.nodeContainer.getComponent(position).setBackground(Color.magenta); }
	
	private void highlightPath(int [] path) {
		for(int i = 1; i < path.length-1; i++)
		{
			planeBuild.nodeContainer.getComponent(path[i]).setBackground(Color.yellow);
		}
	}
}
