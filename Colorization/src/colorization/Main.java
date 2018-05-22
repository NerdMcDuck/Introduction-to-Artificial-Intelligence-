/**
 * 
 */
package colorization;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

/**
 * @author Ramon
 *
 */
public class Main {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
	
		ArrayList<double[]> input = scaleArray(loadData("DataFiles\\input.csv"));
		ArrayList<double[]> color = scaleArray(loadData("DataFiles\\color.csv"));
		ArrayList<double[]> data = scaleArray(loadData("DataFiles\\data.csv"));

		int numberOfTrainingValues = (int) (input.size()*0.8); //Top 80% of values. index 0 -> 39,114
		int numberOfTestingValues = (int) (input.size()*0.2); //Bottom 20% of values. index 39,115 -> 48,893

		double[][] trainedWeights = new double[3][9];
		Neuron red = new Neuron(10);
		Neuron green = new Neuron(10);
		Neuron blue = new Neuron(10);

		File file = new File("Weights.txt");
			
		if(!file.exists()) //If file "Weights.txt" exists -> skip training
		{
			//Training
			
			for(int i = 0; i<numberOfTrainingValues; i++){

				double[] inputValues = (double[]) input.get(i);
				double[] outputValues = (double[]) color.get(i);		
				
				
				trainedWeights = train(inputValues, outputValues, red, green, blue);				

			}

			//Save the weights to a file
			writeToFile("Weights", trainedWeights);
			
		}
		
		testNetwork(input, numberOfTestingValues);
		predict(data, "Data_Result");
	
		//Color the duck
		ArrayList<double[]> img = scaleArray(gimage("Colorization\\duck with glasses.png", "Colorization\\GrayDuck.png"));
		predict(img, "img_values");
		Colorimage("Colorization\\GrayDuck.png", loadData("img_values.csv"),"Colorization\\ColoredDuck.png");
		
		//Color the tiger
		ArrayList<double[]> img2 = scaleArray(gimage("Colorization\\tiger.png", "Colorization\\GrayTiger.png"));
		predict(img2, "img_values2");
		Colorimage("Colorization\\GrayTiger.png", loadData("img_values2.csv"),"Colorization\\ColoredTiger.png");
		
		System.out.println("Done!");
	}

	/**
	 * Train the network using supervised learning
	 * @param input the data for the network to be trained on 
	 * @param output the expected output for the input data 
	 * @return a trained network
	 */
	public static double[][] train(double[] input, double[] output, Neuron red, Neuron green, Neuron blue){
		double[][] result = new double[3][9]; //returns an array of weights
		double bias = 1.0; //x0 always one for the bias
		double[] inputWithBias = new double[10];
		inputWithBias[0] = bias;

		for(int i = 0; i< inputWithBias.length; i++){
			if(i == 0){
				inputWithBias[i] = bias;	
			} else{
				inputWithBias[i] = input[i-1];
			}

		}

		red.setInputs(inputWithBias);
		green.setInputs(inputWithBias);
		blue.setInputs(inputWithBias);
		
		
		
		result[0] = red.train(inputWithBias, output[0]);
		result[1] = green.train(inputWithBias, output[1]);
		result[2] = blue.train(inputWithBias, output[2]);


		return result;		
	}

	/**
	 * Use the bottom 20% of the data to test the network
	 * @param input the input data used to train
	 * @param color the expected output after testing
	 * @param numberOfTestingValues the bottom 20% of the data
	 */
	public static void testNetwork(ArrayList<double[]> input, int numberOfTestingValues){

		//Load the weights
		double[] weights_R = loadWeights("Weights.txt").get(0);
		double[] weights_G = loadWeights("Weights.txt").get(1);
		double[] weights_B = loadWeights("Weights.txt").get(2);

		int[][] rgb = new int[ numberOfTestingValues+1][3];

		//Testing
		for(int i = 39115; i < input.size(); i++) //Where the testing begins
		{
			double r=0.0, g = 0.0, b = 0.0;
			double[] inputValues = (double[]) input.get(i);

			for(int k = 0; k < 9; k++)
			{
				r += weights_R[k]*inputValues[k];
				g += weights_G[k]*inputValues[k];
				b += weights_B[k]*inputValues[k];

			}
			rgb[i-39115][0] = (int)( Sigmoid(r) * 255 );
			rgb[i-39115][1] = (int)( Sigmoid(g) * 255 );
			rgb[i-39115][2] = (int)( Sigmoid(b) * 255 );


		}
		
		writeToFile("Testing_Results", rgb);
	}
	
	/**
	 * Use what the network learned to predict rgb values for new data
	 * @param input the new data of grayscale values
	 * @param filename the name of the output file
	 */
	public static void predict(ArrayList<double[]> input, String filename){

		//Load the weights
		double[] weights_R = loadWeights("Weights.txt").get(0);
		double[] weights_G = loadWeights("Weights.txt").get(1);
		double[] weights_B = loadWeights("Weights.txt").get(2);

		int[][] rgb = new int[ input.size()][3];

		
		for(int i = 0; i < input.size(); i++)
		{
			double r=0.0, g = 0.0, b = 0.0;
			double[] inputValues = (double[]) input.get(i);

			for(int k = 0; k < 9; k++)
			{
				r += weights_R[k]*inputValues[k];
				g += weights_G[k]*inputValues[k];
				b += weights_B[k]*inputValues[k];

			}
			rgb[i][0] = (int)( Sigmoid(r) * 255 );
			rgb[i][1] = (int)( Sigmoid(g) * 255 );
			rgb[i][2] = (int)( Sigmoid(b) * 255 );


		}
		
		writeToFile(filename, rgb);
	}

	/**
	 * Load the data from the CSV files
	 * @param filename The name of the csv file
	 * @return data The information in the csv as an array 
	 */
	public static ArrayList<int[]> loadData(String filename){
		File file = new File(filename);
		ArrayList<int[]> data = new ArrayList<>();
		BufferedReader reader = null;
		try{

			reader = new BufferedReader(new FileReader(file));

			String line;

			while( (line = reader.readLine()) != null){

				int[] values = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();

				data.add(values);

			}

		}
		catch(NullPointerException e){

			e.printStackTrace();
		}
		catch (FileNotFoundException e) {

			System.out.println(filename + " could not be found");
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				reader.close();
			} catch (IOException e) {
				System.out.println("Could not close the file");
				e.printStackTrace();
			}
		}

		return data;
	}

	/**
	 * Load the data from the TXT files
	 * @param filename The name of the txt file
	 * @return data The information in the txt as an array 
	 */
	public static ArrayList<double[]> loadWeights(String filename){
		File file = new File(filename);
		ArrayList<double[]> data = new ArrayList<>();
		BufferedReader reader = null;
		try{

			reader = new BufferedReader(new FileReader(file));

			String line;

			while( (line = reader.readLine()) != null){

				double[] values = Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray();

				data.add(values);

			}

		}
		catch(NullPointerException e){

			e.printStackTrace();
		}
		catch (FileNotFoundException e) {

			System.out.println(filename + " could not be found");
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				reader.close();
			} catch (IOException e) {
				System.out.println("Could not close the file");
				e.printStackTrace();
			}
		}

		return data;
	}

	/**
	 * Print the array values
	 * @param arr
	 */
	public static <T> void printArray(ArrayList<T> arr){

		for(int i = 0; i < arr.size(); i++){
			double[] temp = (double[]) arr.get(i);

			for(int j = 0; j < temp.length; j++){
				System.out.print(temp[j] + " ");
			}
			System.out.println();
		}	

	}

	/**
	 * Scales the input values to be between 0 and 1
	 * @param arrayList a scaled array has values between 0 and 1
	 * @return 
	 */
	public static <T> ArrayList<double[]> scaleArray(ArrayList<int[]> arrayList){
		ArrayList<double[]> data = new ArrayList<>();

		for(int i = 0; i < arrayList.size(); i++){

			int[] temp =  arrayList.get(i);
			double[] newArray = new double[temp.length];

			for(int j = 0; j < temp.length; j++){

				newArray[j] = temp[j]/255.0;

			}
			data.add(newArray);
		}
		return data;	

	}

	/**
	 * Write the data to a file
	 * @param filename the desired filename
	 * @param data the data to be written
	 */
	public static void writeToFile(String filename, double[] data){
		BufferedWriter writer = null;

		File file = new File(filename +".txt");

		try{
			if(!file.exists())
			{
				file.createNewFile();
			}

			writer = new BufferedWriter(new FileWriter(file));	

			for(int i = 0; i<data.length; i++){
				writer.write((int) data[i]*255);
			}


		}catch(Exception e){
			System.out.println("An error occured creating file" + e.getMessage());
		}finally{
			try {
				writer.close();

			} catch (IOException e) {
				System.out.println("Could not close the file!");
				e.printStackTrace();

			}
		}
	}

	/**
	 * Write the weights to a file
	 * @param filename the desired filename
	 * @param data the weights to be written
	 */
	public static void writeToFile(String filename, double[][] data){
		BufferedWriter writer = null;
		StringBuilder sb = new StringBuilder();
		File file = new File(filename +".txt");

		try{
			if(!file.exists())
			{
				file.createNewFile();
			}

			writer = new BufferedWriter(new FileWriter(file));	


			for(int i = 0; i<data.length; i++)
			{
				for(int j = 0; j < data[i].length; j++)
				{
					sb.append(data[i][j] + "");
					if(j < data[i].length){
						sb.append(',');
					}
				}
				sb.append("\n");
			}
			writer.write(sb.toString());

		}catch(Exception e){
			System.out.println("An error occured creating file" + e.getMessage());
		}finally{
			try {
				writer.close();

			} catch (IOException e) {
				System.out.println("Could not close the file!");
				e.printStackTrace();

			}
		}
	}

	/**
	 * Write the testing results to a file
	 * @param filename the desired filename
	 * @param data the weights to be written
	 */
	public static void writeToFile(String filename, int[][] data){
		BufferedWriter writer = null;
		StringBuilder sb = new StringBuilder();
		File file = new File(filename +".csv");

		try{
			if(!file.exists())
			{
				file.createNewFile();
			}

			writer = new BufferedWriter(new FileWriter(file));	


			for(int i = 0; i<data.length; i++)
			{
				for(int j = 0; j < data[i].length; j++)
				{
					sb.append(data[i][j] + "");
					if(j < data[i].length){
						sb.append(',');
					}
				}
				sb.append("\n");
			}
			writer.write(sb.toString());

		}catch(Exception e){
			System.out.println("An error occured creating file" + e.getMessage());
		}finally{
			try {
				writer.close();

			} catch (IOException e) {
				System.out.println("Could not close the file!");
				e.printStackTrace();

			}
		}
	}

	/**
	 * The Sigmoid Activation function
	 * @param x
	 * @return the result of the function
	 */
	private static double Sigmoid(double x){ 
		return (1.0 / (1.0 + Math.exp(-x)));
	}
	
	/**
	 * This takes in an image, and converts it to grayscale
	 * @param filename is the full path of the file
	 * @param outputFilename the name of the new image will have
	 * @return grayscaleInputs are the grayscale value of the middle pixel of every 3x3 patch
	 */
	public static ArrayList<int[]> gimage(String filename, String outputFilename){
		File image = new File(filename);
		ArrayList<int[]> color = new ArrayList<int[]>();
		ArrayList<int[]> grayC = new ArrayList<int[]>();
		
		try{
			
			BufferedImage img = ImageIO.read(image);
			BufferedImage grayScaleImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			for(int i = 0; i < img.getWidth()-3; i+=3){
				for(int j = 0; j < img.getHeight()-3; j+=3){
					
					int[] grayArray = new int[9];
					int[] rgb = new int[3];
					int ctr = 0;
					
					//3x3 pixel patch
					for(int k = 0; k < 3; k++){
						for(int n = 0; n < 3;n++ ){
							Color c = new Color(img.getRGB(i+k, j+n));
							int r = c.getRed();
							int g = c.getGreen();
							int b = c.getBlue();
							int a = c.getAlpha();
							
							if(k==1 && n ==1){
								rgb[0] = r; rgb[1] = g; rgb[2] = b;
								color.add(rgb);
							}
							
							
							//Grayscaling
							double Dgr = (0.21*r)+(0.72*g)+(0.07*b);
							int gr = (int) Dgr;
							grayArray[ctr] = gr;
							ctr++;
							
							//Create graycolor
							Color gColor = new Color(gr,gr,gr,a);
							grayScaleImg.setRGB(i+k, j+n, gColor.getRGB());
						}
					}
					grayC.add(grayArray);
					
				}
			}
			ImageIO.write(grayScaleImg, "png", new File(outputFilename));
			
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return grayC;
	}
	
	/**
	 * This takes an array of values and color a given image
	 * @param filename is the full path of the file to color
	 * @param colorValue are the values to color the image with
	 * @param outputFilename the name the new image will have
	 */
	public static void Colorimage(String filename, ArrayList<int[]> colorValues, String outputFilename){
		File image = new File(filename);
		
		try{
			
			BufferedImage grayScaleImg = ImageIO.read(image);
			BufferedImage colorImg = new BufferedImage(grayScaleImg.getWidth(), grayScaleImg.getHeight(), BufferedImage.TYPE_INT_RGB);
			int ctr = 0;
			for(int i = 0; i <  grayScaleImg.getWidth()-3; i+=3){
				int[] rgb = colorValues.get(ctr);
				
				for(int j = 0; j <  grayScaleImg.getHeight()-3; j+=3){
					
					//Color the image in 3x3 patches
					for(int k = 0; k < 3; k++){
						
						for(int n = 0; n < 3;n++ ){
			
							Color color = new Color(rgb[0],rgb[1],rgb[2]);
							colorImg.setRGB(i+k, j+n, color.getRGB());
						}

					}
					ctr++;
				}
			}
			ImageIO.write(colorImg, "png", new File(outputFilename));
			
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
}
