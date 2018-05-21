package cs440;

public class Node {
	int x;
	int y;
	boolean visited = false;
	
	Node(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	Node(int x, int y, boolean visited){
		this.x = x;
		this.y = y;
		this.visited = visited;
	}

}
