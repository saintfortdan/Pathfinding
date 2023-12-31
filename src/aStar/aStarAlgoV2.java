package aStar;

import java.util.ArrayList;
import frameBuild.planeBuild;

public class aStarAlgoV2{
	
	//Nodes
	private class nodeInfo{
		private int G = 0;//Distance from current node to start node 
		private int H = 0;//Estimated distance from current node to target, end, node. H = a**2 + b**2
		private int F = 0;//Total Cost of the node
		
		public nodeInfo parentNode;//Position of parent node, the node that immediately precedes it
		public String nodePosition;//Id: Uses a string to get exact position
		
		nodeInfo(String position, nodeInfo parent){this.nodePosition = position; this.parentNode = parent;}
	}
	
	private int n;
	private int [][] map;
	private boolean targetRight = false;
	
	//All the nodes that have been tested, but not added to closed list, are inserted into this list.
	//The first added position will always be the testing node.
	private ArrayList<aStarAlgoV2.nodeInfo> openList;
	
	//Once the node with the smallest F has been found, and it's path is usable, place it inside of this arrayList
	private ArrayList<aStarAlgoV2.nodeInfo> closedList;
	
	//The starting position node and the target position node
	private aStarAlgoV2.nodeInfo startNode;
	private aStarAlgoV2.nodeInfo targetNode;
	
	//Filler information
	private int targetNodeRow;
	private int targetNodeColumn;
	private Boolean noPath = false;//In the odd scenario of no path being found
	
	//This is strictly used, to keep track of node position in the ArrayList. The alternative was to sort the array every time something was added to the list
	private ArrayList<String> openListPosition;
	private ArrayList<String> closedListPosition;
	
	private ArrayList<int []> shortPath;
	private int pathSize;
	private String startChild;
	private boolean loop = false;
	private int loopNodePosition;
	
	private final int [][] ADJACENTNODES = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};
	
	//Constructor
	public aStarAlgoV2(){
		this.openList = new ArrayList<>();
		this.closedList = new ArrayList<>();
		this.openListPosition = new ArrayList<>();
		this.closedListPosition = new ArrayList<>();
		shortPath = new ArrayList<int[]>();
	}
	
	
	private void setMap(ArrayList<String> blockedNodes, String startN, String targetN, int n)//Pass the n value, and the array
	{
		this.map = new int [n][n];
		int position = -1;
		
		for(int x = 0; x < n; x++)
		{
			for(int y = 0; y < n; y++)
			{
				position++;
				if( startN.equals(x+"-"+y))
				{
					map[x][y] = 1;
				}
				else if( targetN.equals(x+"-"+y) )
				{
					map[x][y] = 2;
				}
				else if( blockedNodes.contains((position + "_" + x+"-"+y)) )
				{
					map[x][y] = -1;
				}
				else
				{
					map[x][y] = 0;
				}
			}
		}
	}
	
	
	//Set the starting nodePosition and targetNode position
	//Neither have a parent node
	private void setStartEnd(String start, String target){		
		this.startNode = new nodeInfo(start, null);
		this.targetNode = new nodeInfo(target, null);
		this.targetNodeRow = Integer.parseInt(this.targetNode.nodePosition.split("-")[0]);//x; parentRow
		this.targetNodeColumn = Integer.parseInt(this.targetNode.nodePosition.split("-")[1]);//y; parentColumn
		this.closedList.add(startNode);
	}
	
	
	private void getStartNodes(){
		int pRow = Integer.parseInt(this.startNode.nodePosition.split("-")[0]);
		int pColumn = Integer.parseInt(this.startNode.nodePosition.split("-")[1]);
		
		
		for(int i = 0; i < this.ADJACENTNODES.length; i++)
		{
			int row = pRow + this.ADJACENTNODES[i][0];//Current Node Row (x) Position
			int column = pColumn + this.ADJACENTNODES[i][1];//Current Node Column (y) Position;
			int cost = 10;//If the movement is vertical or horizontal, the cost is 10
			if(i == 0 || i == 2 || i ==  5 || i == 7) {cost = 14;}//Otherwise the cost is 14 //!!!!!!!!!!!!CHANGE BACK TO 14!!!!!!!!!!!!
			String tempPosition = row + "-" + column;
			
			
			//Inbounds(?)
			if( ( (row >= 0) && row < map.length) && ( (column >=0) && column < map.length) ){
				//Blocked(?)
				
				if(this.map[row][column] != -1) {
					this.createNode(tempPosition, this.startNode);//Create New Node with current parentNode as parent	
					this.openList.get(0).G = this.calculateG(cost, this.startNode);//set G
					this.openList.get(0).H = this.calculateH(row, column, this.startNode.nodePosition);//set H
					this.openList.get(0).F = this.calculateF(this.openList.get(0));//set F
				
					this.getAdjacentNodes();
					this.print();
					if(this.pathSize == 0)
					{
						this.shortPath.clear();
						this.shortPath.add(this.returnPath());
						pathSize = this.shortPath.get(0).length;
					}
					else if(this.pathSize >= this.returnPath().length) {
						this.shortPath.clear();
						this.shortPath.add(this.returnPath());
						pathSize = this.shortPath.get(0).length;
					}
				}
				this.noPath = false;
				this.openList.clear();
				this.closedList.clear();
				this.closedList.add(this.startNode);
			}
		}
	}
	
	
	//The meats and potatoes of the algorithm
	//The function responsible for locating all possible paths to the target node
	private void getAdjacentNodes(){	
		
		if(!this.noPath) {
			int pRow = Integer.parseInt(this.openList.get(0).nodePosition.split("-")[0]);//x; parentRow
			int pColumn = Integer.parseInt(this.openList.get(0).nodePosition.split("-")[1]);//y; parentColumn
			nodeInfo parent = this.openList.get(0);//The parent node. This node will be the central point, where you get the other 8 nodes around it
			
			this.closedList.add(this.openList.remove(0));//Add the parent node to closedList	
			this.updatePosition();//Call this here for the change done to the parent node; moving it to closedList.
			
			//For recursion. If the targetNode is in closedList, end recursive call	
			if( !this.closedList.get(this.closedList.size()-1).nodePosition.equals(this.targetNode.nodePosition) ) {
				for(int i = 0; i < this.ADJACENTNODES.length; i++)
				{
					int row = pRow + this.ADJACENTNODES[i][0];//Current Node Row (x) Position
					int column = pColumn + this.ADJACENTNODES[i][1];//Current Node Column (y) Position;
					int cost = 10;//If the movement is vertical or horizontal, the cost is 10
					if(i == 0 || i == 2 || i ==  5 || i == 7) {cost = 14;}//Otherwise the cost is 14 //!!!!!!!!!!!!CHANGE BACK TO 14!!!!!!!!!!!!
					String tempPosition = row + "-" + column;
					
					
					//Inbounds(?)
					if( ( (row >= 0) && row < map.length) && ( (column >=0) && column < map.length) ) {
						//The -1 is being utilized to indicate a blockade, set by the user; Node -1(?)
						if(this.map[row][column] != -1) {
							if(!tempPosition.equals(startNode.nodePosition) && !tempPosition.equals(targetNode.nodePosition)) {
								int nodePosition = ((row * n)-1) + column + 1;
								planeBuild.checkedNodes(nodePosition);
							}
							
							//Node Exists(?)
							if(!this.openListPosition.contains(tempPosition) && !this.closedListPosition.contains(tempPosition)) {
								this.createNode(tempPosition, parent);//Create New Node with current parentNode as parent	
								this.openList.get(openList.size()-1).G = this.calculateG(cost, parent);//set G
								this.openList.get(openList.size()-1).H = this.calculateH(row, column, parent.nodePosition);//set H
								this.openList.get(openList.size()-1).F = this.calculateF(this.openList.get(this.openList.size()-1));//set F
							}
							
							else {	
								//Node Exists(?) Yes- Is it in the open or closed List(?)
								if(!this.closedListPosition.contains(tempPosition)) {
									int nodeLocationtemp = this.openListPosition.indexOf(tempPosition);//Position
									
									//If the tested node is also a child of the startNode
									//if(this.openList.get(nodeLocationtemp).parentNode.nodePosition.equals(this.startNode.nodePosition))
									//	this.startChild = this.openList.get(nodeLocationtemp).nodePosition;
									
									
									//If the current Node already Exists and has been tested with the given parent, but has failed to find a path, ignore this node
									if(this.openList.get(nodeLocationtemp).parentNode.nodePosition.equals(parent.nodePosition) && this.openList.get(nodeLocationtemp).G == -1) {}
									else {
										int holdOldG = this.openList.get(nodeLocationtemp).G;//Get already set G of currentNode
										int holdPotentialG = this.calculateG(cost, parent);//Get G relative to new parent
	
										if(holdOldG >= holdPotentialG) {	
											this.openList.get(nodeLocationtemp).G = holdPotentialG;//Set new G
											this.openList.get(nodeLocationtemp).H = this.calculateH(
													Integer.parseInt(this.openList.get(nodeLocationtemp).nodePosition.split("-")[0]), 
													Integer.parseInt(this.openList.get(nodeLocationtemp).nodePosition.split("-")[1]), 
													parent.nodePosition);
											this.openList.get(nodeLocationtemp).F = this.calculateF(this.openList.get(nodeLocationtemp));//Set New F
											this.openList.get(nodeLocationtemp).parentNode = parent;//Set new parent
										}
									}
								}
							}
						}
					}
					this.updatePosition();
				}
				
			if(openList.size() == 0) { this.noPath = true; }
			else {
				int temp = this.getSmallestF(parent);
				
				
				if(temp >= 0){ 
					/*if(this.openList.get(temp).nodePosition.equals(this.startChild))
					{
						this.loop = true;
						this.loopNodePosition = this.closedList.size();
					}*/
					
					this.openList.add(0, this.openList.remove(temp));
				}
				else {
					//It has been determined that the Node with this parent has failed to find a path, 
					//thus reset it's G, H, F values to indicate a dead path
					this.closedList.get(this.closedList.size()-1).G = -1;//Dead Path Reset Everything but parent
					this.closedList.get(this.closedList.size()-1).H = -1;
					this.closedList.get(this.closedList.size()-1).F = -1;
					if(this.closedList.size() < 2)
						this.noPath = true;
					else {
						this.openList.add(this.closedList.remove(this.closedList.size()-1));//Last node added gets removed from closed list, added back to openList
						this.openList.add(0, this.closedList.remove(this.closedList.size()-1));//The node that supersedes the previously removed node, SHOULD be it's parent, do the same
					}
				}
			}
			this.getAdjacentNodes();//Recursion Call
		}
			else {
					this.print();
			}
		}
		else {
			System.err.println("Bad Path OR Blockade Blocks All Nodes From Target");	
		}
	}
	
	
	//Create new Node
	private void createNode(String position, nodeInfo parent){	this.openList.add(new nodeInfo(position, parent)); }
	
	
	//Update the ArrayList(s) responsible for keep track of position/location of nodes
	//To avoid using this, sort the Array, but I'd rather not. OR, the more obvious solution, use hashmaps rather than arrays
	private void updatePosition(){
		this.closedListPosition.clear();
		this.openListPosition.clear();
		for (int i = 0; i < this.closedList.size(); i++) { this.closedListPosition.add(this.closedList.get(i).nodePosition); }
		for (int i = 0; i < this.openList.size(); i++) { this.openListPosition.add(this.openList.get(i).nodePosition); }
	}
	
	
	//Distance from current node to start node 
	private int calculateG(int cost, nodeInfo parent){ return cost + parent.G; };
	
	
	//cost = 10 in this case, because when determining the distance between the test node and the
	//target node it will always use vertical and horizontal movements.
	private int calculateH(int cRow, int cColumn, String parentPosition){ 		
		//TODO:
		//DETERMINE IF THE TARGET NODE IS AHEAD OF THE START NODE, OR BEHIND THE START NODE
		//ASSUMING THE TARGET NODE IS IN FRONT OF START NODE, TO THE RIGHT OF START NODE
		//THEN DETERMINE IF THE CURRENT CHILD IS INFRONT OF OR BEHIND THE PARENT NODE
		//IF THE CHILD IS BEHIND(LEFT OF) THE PARENT NODE, IT WILL MOSTLIKELY UTILIZE THE PARENT NODE, AGAIN, TO GET TO THE TARGET NODE ENACT PENALTY COUNT
		//IF THE CHILD NODE IS IN FRONT(RIGHT OF) OF THE PARENT NODE, IT WILL MOSTLIkELY NOT USE THE PARENT NODE TO GET TO THE TARGET NODE
		int leftPenalty = 0;
		int rightPenalty = 0;
		int upDownPenalty = 0;
		
		if(this.targetRight)
		{
			if(Integer.parseInt(parentPosition.split("-")[0]) == cRow && Integer.parseInt(parentPosition.split("-")[1]) > cColumn)
			{
				leftPenalty = 10;
			}
			//else if(cColumn Integer.parseInt(this.startNode.nodePosition.split("-")[0]) ) {}
		}
		else
		{
			if(Integer.parseInt(parentPosition.split("-")[0]) == cRow && Integer.parseInt(parentPosition.split("-")[1]) < cColumn)
			{
				rightPenalty = 10;
			}
		}
		
		//In the scenario where the child node is in the same column as the parent node and target node 
		//But the target node is above the parent node with the child node being below the parent
		//Or, the target node is below the parent node with the child being above the parent node 
		if(parentPosition.split("-")[1].equals(targetNode.nodePosition.split("-")[1]) && Integer.parseInt(parentPosition.split("-")[1]) == cColumn)
		{
			int temp = Integer.parseInt(parentPosition.split("-")[0]);
			if((temp > Integer.parseInt(targetNode.nodePosition.split("-")[0]) &&  temp < cRow) || (temp < Integer.parseInt(targetNode.nodePosition.split("-")[0]) && temp > cRow))
			{
				upDownPenalty = 10;
			}
		}
		
		return (10*(Math.abs(this.targetNodeRow - cRow) + Math.abs(this.targetNodeColumn - cColumn))) + (leftPenalty + rightPenalty + upDownPenalty); 
	};
	
	
	//Total Cost of the node
	private int calculateF(nodeInfo currentNode){ return currentNode.G + currentNode.H; };

	
	//Get the smallest F in the closed list
	//Return the position of the Node in the list
	//Only consider nodes that are children of the given parentNode
	private int getSmallestF(nodeInfo parent){
		int smallest = -1;
		int position = -1;
		for(int i = 0; i < this.openList.size(); i++)
		{
			//Testing Purposes
			/*System.out.println("getSmallestF Func-2 ??  Parent.nodePosition: " + parent.nodePosition 
					+ "\tcurrentNode's parentNodePosition: " + this.openList.get(i).parentNode.nodePosition 
					+ "\tcurrentNode's Position: " + this.openList.get(i).nodePosition 
					+ "\tcurrentNode's G: " + this.openList.get(i).G
					+ "\tcurrentNode's H: " + this.openList.get(i).H
					+ "\tcurrentNode's F: " + this.openList.get(i).F);*/
			
			if(this.openList.get(i).parentNode.nodePosition.equals(parent.nodePosition))//currentNode must be a child of parentNode 
			{
				if(this.openList.get(i).F == -1)//If currentNode has been tested before, ignore
				{}
				else if(smallest < 0)//If currentNode is the first testable node found, set values
				{
					smallest = this.openList.get(i).F;
					position = i;
				}
				else if(smallest > this.openList.get(i).F)//If better F found, set values
				{
					smallest = this.openList.get(i).F;
					position = i;
				}
			}
		}
		if(position >= 0)
			return position;//Return the new parent node to the starting position
		else
			return -1;//If nothing is found, this node is a dead end, back track
	}
		
	
	public int [] returnPath(){
		int [] path = new int [this.closedList.size()]; 
		/*ArrayList<nodeInfo> temp;
		if(this.loop)
		{
			temp = new ArrayList<aStarAlgoV2.nodeInfo>();
			temp.add(this.closedList.get(0));
			for(int i = this.loopNodePosition; i < this.closedList.size(); i++)
			{
				temp.add(this.closedList.get(i));
			}
			this.closedList.clear();
			this.closedList.addAll(temp);
		}*/

		for(int i = 0; i < this.closedList.size(); i++)
		{
			path[i] = ((Integer.parseInt(this.closedList.get(i).nodePosition.split("-")[0]) * n)-1) + Integer.parseInt(this.closedList.get(i).nodePosition.split("-")[1]) + 1;
		}
		this.openList.clear();
		return path;
	}
	

	
	//Testing Purposes //Ignore
	public void print(){
		/*System.out.print("\n\n\n**************************************");
		System.out.print("\n\n\n**************************************");
		System.out.print("\n\n\n**************************************\n");
		System.out.println("\n\n\n*********OpenList**********\n\n\n");
		
		for(int i = 0; i < this.openList.size(); i++)
		{
			System.out.println("NodePosition: " + this.openList.get(i).nodePosition + "\nThis is G: " + this.openList.get(i).G);
			System.out.println("This is H: " + this.openList.get(i).H);
			System.out.println("This is F: " + this.openList.get(i).F + "\n");
		}
		
		System.out.println("\n\n\n*********ClosedList********\n\n\n");
		
		for(int i = 0; i < this.closedList.size(); i++)
		{
			System.out.println("closedList - NodePosition: " + this.closedList.get(i).nodePosition); 
			System.out.println("closedList - This is G: " + this.closedList.get(i).G);
			System.out.println("closedList - This is H: " + this.closedList.get(i).H);
			System.out.println("closedList - This is F: " + this.closedList.get(i).F + "\n");
		}
		*/
		/*System.out.println("\n\n\n*********openPosition********\n\n\n");
		for(int i = 0; i < this.openListPosition.size(); i++)
		{
			System.out.println("Hold Position: i = " + i + ": " + openListPosition.get(i) + "\n");
		}
		*/
		System.out.println("*********closedPosition********");
		for(int i = 0; i < this.closedListPosition.size(); i++)
		{
			System.out.println("Hold Position: i = " + i + ": " + closedListPosition.get(i) + "\tParent: " + closedList.get(i).nodePosition);
		}
		System.out.print("**************************************\n\n\n");
	}
	
	
	//This is annoying at this point, ignore for now!
	/*private nodeInfo restart(nodeInfo pNode){
		//this.openList.clear();
		
		for(int i = 1; i < this.closedList.size(); i++)
		{
			int row = Integer.parseInt(this.closedList.get(0).nodePosition.split("-")[0]);
			int column = Integer.parseInt(this.closedList.get(0).nodePosition.split("-")[1]);
			if(!(row+"-"+"column").equals(this.startNode.nodePosition))
				planeBuild.checkedNodes( (((row * n)-1) + column + 1) );
			
			if(this.closedList.get(i).parentNode.nodePosition.equals(pNode.parentNode.nodePosition))
			{
				this.closedList.get(i).F = -1;
				this.closedList.get(i).G = -1;
				this.closedList.get(i).H = -1;
				this.openList.add(this.closedList.remove(i));
			}
			else
			{
				this.closedList.remove(i);
			}
		}
			this.updatePosition();
			return this.closedList.get(0);
		}
	*/

	
	
	//Setup 
	public int [] setup(ArrayList<String> blockedNodes, String startN, String targetN, int n){
		this.n = n;
		
		if(Integer.parseInt(startN.split("-")[0]) < Integer.parseInt(targetN.split("-")[0]))
			this.targetRight = true;
		
		this.setMap(blockedNodes, startN, targetN, n);
		this.setStartEnd(startN, targetN);
		this.getStartNodes();
		//this.getAdjacentNodes();
		
		
		return this.shortPath.get(0);
		//return null;
	}

	
}