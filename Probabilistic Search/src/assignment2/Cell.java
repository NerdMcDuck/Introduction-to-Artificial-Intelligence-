package assignment2;

/**
 * The cells that will compose the n*n grid
 * @author Ramon
 *
 */

public class Cell {
	
	private String terrain;
	private boolean isTarget = false;
	private int cellID;
	
	public Cell(String terrain, boolean isTarget, int cellID){
		this.terrain = terrain;
		this.isTarget = isTarget;
		this.cellID = cellID;
	}
	
	public String getTerrain(){
		return terrain;
	}
	
	public boolean getTarget(){
		return isTarget;
	}
	
	public int getCellID(){
		return cellID;
	}
	
	public void setTerrain(String terrain){
		this.terrain = terrain;
	}
	
	public void setTarget(boolean isTarget){
		this.isTarget = isTarget;
	}
	
	public void setCellID(int cellID){
		this.cellID = cellID;
	}
}
