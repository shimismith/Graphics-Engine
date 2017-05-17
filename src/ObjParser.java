import java.io.BufferedReader;
import java.io.FileReader;

public class ObjParser {
	
	//reads the .obj file and returns a mesh
	public static Point[][] readFile(String fileName) throws Exception{
		FileReader fr = new FileReader(ObjParser.class.getResource(fileName).getFile());
		BufferedReader br = new BufferedReader(fr);
	    
	    String line;
	    
	    //read the the number of verticies and number of triangles
	    line = br.readLine();
	    String[] parts = line.split(" ");  //["#", "number of verticies", "number of triangles"]
	    int numVerticies = Integer.parseInt(parts[1]);
	    int numTriangles = Integer.parseInt(parts[2]);
	    	    
	    Point[] verticies = new Point[numVerticies];  //the 3d points
	    
	    Point[][] triangles = new Point[numTriangles][3];  //the 3d points in triangle order for the mesh
	    
	    //get all the points
	    for(int i = 0; i<numVerticies; i++){
	    	line = br.readLine();
	    	parts = line.split(" ");

	    	double n= 0.15; //was .15  3.15
	    	verticies[i] = new Point(Double.parseDouble(parts[1])*n, Double.parseDouble(parts[2])*n, Double.parseDouble(parts[3])*n);
	    }
	    
	    line = br.readLine();  //takes care of a blank line between list of v and list of f
	    
	    //create the mesh by ordering the points as triangles
	    for(int i = 0; i<numTriangles; i++){
	    	line = br.readLine();
//	    	System.out.println(line + "   hey");
	    	parts = line.split(" ");
	    	
	    	triangles[i][0] = verticies[Integer.parseInt(parts[1])-1];
	    	triangles[i][1] = verticies[Integer.parseInt(parts[2])-1];
	    	triangles[i][2] = verticies[Integer.parseInt(parts[3])-1];
	    }
	    
	    br.close();
	    
	    return triangles;
	}
	
	public static void main(String[] args){
		
		try {
			ObjParser.readFile("bunny.obj");
			System.out.println("FILE LOAD SUCCESFULL");
		} catch (Exception e) {
			   System.out.println("FILE LOAD FAILED");
			e.printStackTrace();
		}
		
		
	}

}
