package cs440;

import java.util.*;

public class Driver {
	
	public static void main(String[] args) {
		
		//Ask the user for size of grid and probability
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Enter size of grid: ");
		int gridSize = sc.nextInt();
	
		System.out.println("Enter Probability from 0 to 1: ");
		float p = sc.nextFloat();
		
		
		char[][] grid = generateGrid(gridSize, p);
		
		
		Pathcount count = new Pathcount(0);
		//Start time 
		long StartTime = System.currentTimeMillis();
		//int gridSize = 10;
		
		ArrayList<Node> path = new ArrayList<Node>();
		Map<Node, Node> nextNodes = new HashMap<Node, Node>();
		Node node = new Node(0,0);
		
		//Used for pathcounting and answering the questions
		/*for(float p = 0; p < 0.6; p+=0.1){
			for(int trials = 1; trials <= 30; trials++){
				char[][] grid = generateGrid(gridSize, p);
				BFS(grid);
			//	recDfs(grid,0,0, false, nextNodes, node);
				Pathcount.added = false;
			}
			//double pcount = new Pathcount().count;
			System.out.println("p=" + p + " Avg. Expanded Nodes: " + (count.count/30));
			count.count = 0;
			
		}*/
		
		//BFS 
		//printGrid(BFS(grid));
		
		//Search using DFS
		//recDfs(grid,0,0, false, nextNodes, node);
		
		long EndTime = System.currentTimeMillis();
		long TimeTaken = EndTime - StartTime;
		System.out.println("Execution time: " + TimeTaken + " milliseconds");
	}
	
	/*Generates a grid 
	 * @param size - size of grid
	 * @param p - probability of blocks
	 * */
	public static char[][] generateGrid(int size, float p){
		
		final char[][] grid = new char[size][size];
		Random random = new Random();
	
		//Set (0,0) to the start and (n,n) to goal
		// S: start, x: blocked, ' ': empty, G: goal, +: path, V: visited
		
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[i].length; j++){
				
				if( i==0 && j == 0){
					grid[i][j] = 'S';
				} 
				else if( i == grid.length-1 && j == grid.length-1){
					grid[i][j] = 'G';
				}
				else if( random.nextFloat() <= p){ 
					grid[i][j] = 'x';
				}
				else {
					grid[i][j] = ' ';
				}
			}
		}
		
		return grid;
	}
	
	/* This prints out the grid
	 */
	public static void printGrid(char[][] grid){
		
		for (int i = 0; i < grid.length; i++) {
		    for (int j = 0; j < grid[i].length; j++) {
		        System.out.print(grid[i][j] + " ");
		    }
		    System.out.println();
		}
		System.out.println();
	}
	
	//Breadth-First Search
	public static char[][] BFS(char[][] grid){
		Queue<Node> fringe = new LinkedList<Node>();
		Map<Node, Node> visited = new HashMap<Node, Node>();
		Map<Node, Node> nextNodes = new HashMap<Node, Node>();
		
		//Initialize the start Node(x,y,count) and add it to the fringe
		Node start = new Node(0,0);
		fringe.add(start); 
		Node currentNode = start;
	
		visited.put(currentNode, null);
		//Remove the head node and check to is if its a goal node
		
		while(!fringe.isEmpty()){
			
			currentNode = fringe.remove();
			
			//Goal Found!
			if(grid[currentNode.x][currentNode.y] == 'G'){
				break;
			} 
			
			//Mark as visited and get its neighbors
			if(grid[currentNode.x][currentNode.y] != 'S' || grid[currentNode.x][currentNode.y] != 'v'){
				grid[currentNode.x][currentNode.y] = 'v';
				Pathcount.count++;
			} else {
				continue;
			}
			
			//Get lower neighbor
			if(isValidNeighbor(grid, currentNode.x, currentNode.y +1)){
				Node nextNode = new Node(currentNode.x, currentNode.y +1);
				if(!visited.containsKey(nextNode)){
					fringe.add(nextNode);
					visited.put(nextNode,null); //add the current node
					nextNodes.put(currentNode, nextNode);
					
				}	            
			}			
			//Get left neighbor
			if(isValidNeighbor(grid, currentNode.x -1, currentNode.y)){
				Node nextNode = new Node(currentNode.x -1, currentNode.y);
				if(!visited.containsKey(nextNode)){
					fringe.add(nextNode);
					visited.put(nextNode,null);
					nextNodes.put(currentNode, nextNode);
					
				}
	           
			}	
			//Get right neighbor
			if(isValidNeighbor(grid, currentNode.x +1, currentNode.y)){
				Node nextNode = new Node(currentNode.x +1, currentNode.y);
				if(!visited.containsKey(nextNode)){
					fringe.add(nextNode);
					visited.put(nextNode,null); //add the current node
					nextNodes.put(currentNode, nextNode);
					
				} 
			}
			
			//Get upper neighbor
			if(isValidNeighbor(grid, currentNode.x, currentNode.y -1)){
				Node nextNode = new Node(currentNode.x, currentNode.y -1);
				if(!visited.containsKey(nextNode)){ 
					fringe.add(nextNode);
					visited.put(nextNode,null);
					nextNodes.put(currentNode, nextNode);
					
				}			
			}
		}
		//Check if there's a path
		if(grid[currentNode.x][currentNode.y] != 'G'){
		   // System.out.println("No viable path found!");
			return grid; 
		}
		//Adds the nodes to a path list then modifies the GRID with the appropiate path
		List<Node> path = new LinkedList<Node>();
	    for (Node node = start; node != null; node = nextNodes.get(node)) {
	        path.add(node);
	        
	    }
	   // Pathcount.count = nextNodes.size();
	    for(Node node : path) {
	     //   System.out.println(node.x+","+node.y);
	    	if(grid[node.x][node.y] == 'S' || grid[node.x][node.y] == 'G' ){
	    		continue;
	    	}
	    	grid[node.x][node.y] = '+';
	      }
	   //  System.out.println("BFS: Path found is designated with +" +"\n");
	    //Pathcount.count++; //Count the number of successful maps
		return grid;
	}

	
	
	//Checks to see if the neighbor is valid
		public static boolean isValidNeighbor(char[][] grid, int x, int y){
			
			if( x < 0 || x >= grid.length || y < 0 || y >= grid.length ){
				return false;
			}
			
			if (grid[x][y] == 'x' || grid[x][y] == 'v'){
				return false;
			}
			return true;
		}
		
		
		//recursive DFS
		/*@param grid is a 2d array 
		 *@param x,y are the values 
		 *@param found is used to check if the goal was reached
		 * */
		public static void recDfs(char[][] grid, int x, int y, boolean found, Map<Node, Node> nextNodes, Node prevNode){
			
			//reached the end or encountered a wall
			if( x < 0 || x >= grid.length || y < 0 || y >= grid.length || grid[x][y] == 'x' ){ 
				return;
			} 
			Node currNode = new Node(x,y);
			
			//If it found the Goal print grid 
			if(grid[x][y] == 'G'){
				found = true;
				
				//Adds the nodes to a path list then modifies the GRID with the appropiate path
				List<Node> path = new LinkedList<Node>();
				Set<Node> keys = nextNodes.keySet(); //get the keys from the table
				Node nodes = null; //Used to create a LinkedList 
				
				//Find the start node (0,0)
				//
				Iterator<Node> iter = keys.iterator();
				while(iter.hasNext()){
					Node tmp = iter.next();
					if(tmp.x == 0 && tmp.y == 0){
						nodes = tmp;
						break;
					}
				}
				
				//Follow the nodes src and destinations. Create a LL 
			    for (; nodes != null; nodes = nextNodes.get(nodes)) {
			        path.add(nodes);   
			    }
			    //Follow the path
			    for(Node node : path) {
			    	
					if(Pathcount.count != path.size()){
						Pathcount.count = path.size();
					}

			    	if(grid[node.x][node.y] == 'S' || grid[node.x][node.y] == 'G' ){
			    		continue;
			    	}
			    	grid[node.x][node.y] = '+';
			      }		
			    //Count the number of successful paths
			/*	if(Pathcount.added == false){
					Pathcount.mapcount++;
					Pathcount.added = true;
				}*/
			    
				//Printing the grid and exiting
				
				System.out.println("Path Found: ");
				printGrid(grid);
				System.exit(0);
				return;
			
			}
			//If already visited do not visit it again
			if(grid[x][y] == 'v'){
				return;
			}
			
			 if(grid[x][y] == 'S'){
				
				//Used so S doesn't get copied over after being visited
			}else{
				grid[x][y] = 'v';
				//path.add(new Node(x,y));
				nextNodes.put(prevNode, currNode);
				
				//Count the number of nodes visited/expanded
//				if(Pathcount.added == false){
//				Pathcount.count++;
//				Pathcount.added = true;
//			}
				
			}
			//Actual DFS 
			recDfs(grid, x+1, y,found, nextNodes, currNode); //traverse down
			recDfs(grid, x, y+1,found,nextNodes, currNode);
			
			/*System.out.println("-------------------------------");
			printGrid(grid);
			System.out.println("-------------------------------");*/
			
		return;	
		}
			
		
}

