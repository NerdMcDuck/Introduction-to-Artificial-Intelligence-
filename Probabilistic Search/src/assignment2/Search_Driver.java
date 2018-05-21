/**
 * CS440: Probabilistic Search
 * Assignment 2
 * @author Ramon
 */

package assignment2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Search_Driver {
	
	/**
	 * P (Target not found in Cell i | Target is in Cell i)
	 */
	final static double FLAT = 0.2;
	final static double HILLY = 0.4;
	final static double FORESTED = 0.6;
	final static double MAZE_OF_CAVES = 0.9;
	

	public static void main(String[] args) {
		
		//Size of grid
		int gridSize = 2;
		int numCells = gridSize*gridSize;
		/**
		 * Represents the belief state 
		 * Belief[Cell(i)] = P (Target in Cell(i)| Observations through time t) = 
		 * time t = 0, we have Belief[Cell(i)] = 1/2500 for a 50x50 grid
		 */
		double[] belief = new double[numCells]; //P( Target in Cell i )
		Arrays.fill(belief, 1.0/numCells);
		Cell[][] grid = generateGridCells(gridSize);
		double[] reset = new double[numCells];
		
		//Keep a copy of the original array
		for(int i = 0; i<belief.length; i++){
			reset[i] = belief[i];
		}
		
		//A simple linear search 
		mySearch(grid, belief);
		
		//Reset the belief state 
		for(int i = 0; i<belief.length; i++){
			belief[i] = reset[i];
		}
		
		//Rule 1
		rule1(grid, belief);
		for(int i = 0; i<belief.length; i++){
			belief[i] = reset[i];
		}
		
		
		//Rule 2
		rule2(grid, belief);
		for(int i = 0; i<belief.length; i++){
			belief[i] = reset[i];
		}
		
		
		question4(grid, belief);
		
		System.exit(0);

	}
	
	/**
	 * Searches the grid for the target linearly 
	 * @param grid The array containing the target to be searched for.
	 * @param belief The probability that the target is in a given cell.
	 */
	public static void mySearch(Cell[][] grid, double[] belief){
		int t = 0; //the time it takes 
		
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid.length; j++){		
				

				//Return success if the target is found
				if(containsTarget(grid[i][j]))
				{ 
					t = (t == 0) ? t++ : t;
					System.out.println("mySearch: Target found with a probability of " + belief[t]*100.0 + "%" + "\nTotal Searches: " + t);
					return;
				}
				else
				{
					//Target not found in Cell i -> update belief 
					updateBelief(belief, grid[i][j], t);
				}
				
				t++; //Increase the number of searches done
			}
		}
		
		System.out.println("mySearch failed to find the target.");	
	}
	
	/**
	 * At any time, search the cell with the highest probability of containing the target.
	 * @param grid The array containing the target to be searched for.
	 * @param belief The probability that the target is in a given cell.
	 */
	public static void rule1(Cell[][] grid, double[] belief){
		int t = 0; 
		int maxIndex = 0;
		double max = belief[0];
		
		for(int i = 0; i < belief.length; i++){
			
			if(max < belief[i]){
				max = belief[i];
				maxIndex = i;
			}else{
				continue;
			}
		}
		
		for(int row = 0; row < grid.length; row++)
		{
			for(int col = 0; col < grid.length; col++)
			{
				if(grid[row][col].getCellID() == maxIndex)
				{
					if(containsTarget(grid[row][col]))
					{	
						System.out.println("Rule 1: Target found with a probability of " + belief[t]*100.0 + "%" + "\nTotal Searches: " + ++t);
						return;
					}
					else
					{
						updateBelief(belief, grid[row][col], t);
						t++;
						
						for(int i = 0; i < belief.length; i++){
							
							if(max < belief[i]){
								max = belief[i];
								maxIndex = i;
							}else{
								continue;
							}
						}
					}
							
				}
				
			}
		}
		
		System.out.println("Rule 1 failed to find the target.");
		
	}
	
	/**
	 * At any time, search the cell with the highest probability of finding the target.
	 * @param grid The array containing the target to be searched for.
	 * @param belief The probability that the target is in a given cell.
	 */
	public static void rule2(Cell[][] grid, double[] belief){
		int t = 0;
		
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid.length; j++){		
				
 
				if( grid[i][j].getTerrain().equals("FLAT")) //P(target is found | target is in cell) = 0.8
				{
					
					if(containsTarget(grid[i][j]))
					{ 	
						System.out.println("Rule 2: Target found with a probability of " + belief[t]*100.0 + "%" + "\nTotal Searches: " + ++t);
						return;
					}
					else
					{
						//Target not found in Cell i -> update belief 
						updateBelief(belief, grid[i][j], t);
						t++; //Increase the number of searches done
					}
				}
				
			}
		}
		
		System.out.println("Rule 2 failed to find the target.");
	}
	
	/**
	 * Uses rules 1 and 2 to search neighboring cells 
	 * @param grid
	 * @param belief
	 */
	public static void question4(Cell[][] grid, double[] belief){
		int t = 0;
		
		int maxIndex = 0;
		double max = belief[0];
		ArrayList<Integer> maxBelief = new ArrayList<Integer>();
		
		for(int i = 0; i < belief.length; i++){
			
			if(max < belief[i]){
				max = belief[i];
				maxIndex = i;
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <grid.length; i++){
			for(int j = 0; j < grid.length; j++){
				
				if(grid[i][j].getCellID() == maxIndex)
				{
					if(grid[i][j].getTerrain().equals("FLAT") || grid[i][j].getTerrain().equals("HILLY") )
					{
						if(containsTarget(grid[i][j]))
						{
							t++;
							System.out.println("Q4: Target found with a probability of " + belief[t]*100.0 + "%" + "\nTotal Searches: " + t);
							return;
						} 
						else 
						{
							updateBelief(belief, grid[i][j], t);
							t++;
							
							if(isValidCell(grid, i, j+1)){
								maxBelief.add(grid[i][j+1].getCellID());
							}
							if(isValidCell(grid, i, j-1)){
								maxBelief.add(grid[i][j-1].getCellID());
							}
							if(isValidCell(grid, i+1, j)){
								maxBelief.add(grid[i+1][j].getCellID());
							}
							if(isValidCell(grid, i-1, j)){
								maxBelief.add(grid[i-1][j].getCellID());
							}
							
							for(int maxI = 0; maxI < maxBelief.size(); maxI++){
								
								if(max < belief[maxBelief.get(maxI)]){
									max = belief[maxBelief.get(maxI)];
									maxIndex = maxBelief.get(maxI);
								}else{
									continue;
								}
							}
							maxBelief.clear();
							
							
						}
						
					}
				}

				
				
			}
		}
		System.out.println("Q4 failed to find the target.");
	}
	
	/**
	 * Check if a cell is valid
	 * @param grid The map to check within
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @return True if its valid, false otherwise
	 */
	public static boolean isValidCell(Cell[][] grid, int x, int y){
		if( x < 0 || x >= grid.length || y < 0 || y >= grid.length ){
			return false;
		}
		return true;
	}
	
	/**
	 * Checks the current cell to see if it contains the target
	 * with probability depending on its terrain
	 * @param gridCell The current cell being checked
	 * @return true If the terrain contains the target
	 */
	public static boolean containsTarget(Cell gridCell){
		Random rand = new Random();
		double val = rand.nextDouble();
		
		if(gridCell.getTerrain().equals("FLAT")){
			if(val <= FLAT){
				return false;
			}else if(gridCell.getTarget() == true){
				return true;
			}
		} else if(gridCell.getTerrain().equals("HILLY")){
			if(val <= HILLY){
				return false;
			}else if(gridCell.getTarget() == true){
				return true;
			}
		} else if(gridCell.getTerrain().equals("FORESTED")){
			if(val <= FORESTED){
				return false;
			}else if(gridCell.getTarget() == true){
				return true;
			}
		}else if(gridCell.getTerrain().equals("MAZE_OF_CAVES")){
			if(val <= MAZE_OF_CAVES){
				return false;
			}else if(gridCell.getTarget() == true){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Update the observations 
	 * @param belief  The array to be searched and updated
	 * @param grid
	 * @param t_value  The time value, how many searches have been conducted
	 * @return an updated belief array
	 */
	public static double[] updateBelief(double[] belief, Cell cell, int t_value){
		/*
		 * Target not found in cell 0: 
		      P( Target in Cell 0 | Failed to Find in Cell 0 ) = P( Target in Cell 0 )P( Failed to Find in Cell 0 | Target in Cell 0 ) / P( Failed to Find in Cell 0)
		 * 
		 * Other non-0 cells
		      P( Target in Cell i | Failed to Find in Cell 0 ) = P( Target in Cell i )P( Failed to Find in Cell 0 | Target in Cell i ) / P( Failed to Find in Cell 0)
		 */
		
		//Find Cell i in the belief
		String terrainType = cell.getTerrain();
		int cell_i = cell.getCellID();
		double terrain = 0.0; // P (Target not found in Cell i | Target is in Cell i) <-> P( Failed to Find in Cell 0 | Target in Cell i )
		
		if(terrainType.equals("FLAT")) terrain = FLAT; 
		else if(terrainType.equals("HILLY")) terrain = HILLY;  
		else if(terrainType.equals("FORESTED")) terrain = FORESTED;
		else terrain = MAZE_OF_CAVES;
				
		 // P( Target in Cell i | Failed to Find in Cell 0 ) = P( Target in Cell i )P( Failed to Find in Cell 0 | Target in Cell i ) / P( Failed to Find in Cell 0)
			belief[cell_i] = (belief[cell_i] * terrain);
		
		//Normalize
		double norm_factor = 0.0;
		
		for(int i = 0; i<belief.length; i++){
			norm_factor += belief[i];
		}
		
		for(int i = 0; i<belief.length; i++){
			belief[i] *= 1/norm_factor;
		}
		
		return belief;
	}
	
	/**
	 * Generates the grid to be used
	 * 
	 * @param gridSize
	 * @return Cell[][]
	 */
	public static Cell[][] generateGridCells(int gridSize){

		Cell[][] grid = new Cell[gridSize][gridSize];
		int numCells = gridSize*gridSize;
		int cellID = 0; //Used to identify the i-th cell
		
		boolean TargetAssigned = false;
		String terrain = null;
		
		Random rand = new Random(); //For determining the probability
		
		
		for(int row = 0; row < grid.length; row++){
			for(int col = 0; col < grid.length; col++){
				
				/**
				 * randomly choose a number between 0 and 3 inclusive
				 * Each cell has a 1/4 chance to be any of the terrain
				 */
				
				int terrainType = rand.nextInt(4); 
				
				switch(terrainType){
				
				case 0: 
					terrain = "FLAT";
					break;
				case 1:
					terrain = "HILLY";
					break;
				case 2:
					terrain = "FORESTED";
					break;
				case 3:
					terrain = "MAZE_OF_CAVES";
					break;
					
				}
				
				//Assign the target to a cell with probability 1/# of cells
				
				if(rand.nextInt(numCells)==0 && !TargetAssigned){
					grid[row][col] = new Cell(terrain, true, cellID);
					TargetAssigned = true;
				} else{
					grid[row][col] = new Cell(terrain, false, cellID);
				}
				cellID++;
			}
			
		}
		
		//Guarantees that a cell has a target
		if(!TargetAssigned){
			int t_value = rand.nextInt(numCells);
			
			for(int row = 0; row < grid.length; row++){
				for(int col = 0; col < grid.length; col++){
					
					if(grid[row][col].getCellID() == t_value){
						grid[row][col].setTarget(true);
						TargetAssigned = true;
					}
						continue;			
				}
			}
		}
		
		return grid;
		
	}
	
	/**
	 * Print the grid
	 * @param grid
	 */
	public static void printGrid(Cell[][] grid){
		String dash = "-------";
		  char[] repeat = new char[grid.length*22];
		  Arrays.fill(repeat, '-');
		  dash += new String(repeat);
		
		for (int i = 0; i < grid.length; i++) {
			System.out.println(dash);
		    for (int j = 0; j < grid[i].length; j++) {
		    	
		        System.out.print("| " + grid[i][j].getTerrain() + ", isTarget: " + grid[i][j].getTarget() + ", cellID: " + grid[i][j].getCellID() +" | ");
		    }
		    System.out.println();
		}
		System.out.println();
	}

}
