package assistClass;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

/**
 * A class which used to check the differential between Original File and Recovered File
 * @author xuping Fang, Ji Cheng, Xiaoran Huang
 *
 */
public class CorrelationCalculator{
	
	public String originFilePath;
	public String recoveredFilePath;
	
	private ArrayList<String> originalFrames;
	private ArrayList<String> recoveredFrames;
	
	private static SpearmansCorrelation sc = new SpearmansCorrelation();
	
	public CorrelationCalculator(){
		
	}
	
	/**
	 * A private method which reads the Original BVH file line by line, stores the frame data of the file in to the attribute of this object.
	 */
	private void readOriginalFile(){
		
		FileReader fileReader;
		BufferedReader bufferedReader;
		
		try{
			fileReader=new FileReader(new File(originFilePath));
			bufferedReader=new BufferedReader(fileReader);
		} 
		catch (FileNotFoundException e){
			e.printStackTrace();
			System.out.println("BVH file not found, exit with error.");
			return;
		}
		
		originalFrames=new ArrayList<String>();
		
		try {
			String line;
			// Read to the frame location
			while ((line=bufferedReader.readLine()) != null){
				
				if (line.contains("Frame Time")){
					break;
				}
			}
			while ((line=bufferedReader.readLine()) != null){
				originalFrames.add(line);
			}
			// Close file
			fileReader.close();
			bufferedReader.close();
		} 
		catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * A private method which reads the Recovered BVH file line by line, stores the frame data of the file in to the attribute of this object.
	 */
	private void readRecoveredFile(){
		
		FileReader fileReader;
		BufferedReader bufferedReader;
		
		try{
			fileReader=new FileReader(new File(recoveredFilePath));
			bufferedReader=new BufferedReader(fileReader);
		} 
		catch (FileNotFoundException e){
			e.printStackTrace();
			System.out.println("BVH file not found, exit with error.");
			return;
		}
		
		recoveredFrames=new ArrayList<String>();
		
		try {
			String line;
			// Read to the frame location
			while ((line=bufferedReader.readLine()) != null){
				
				if (line.contains("Frame Time")){
					break;
				}
			}
			while ((line=bufferedReader.readLine()) != null){
				recoveredFrames.add(line);
			}
			// Close file
			fileReader.close();
			bufferedReader.close();
		} 
		catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * A private method which Use Spearman Correlation Method to compare the differential between original file and recovered file
	 */
	public void doCaculation(){
		
		readOriginalFile();
		readRecoveredFile();
		
		int frameNum=originalFrames.size();
		int lineLen=0;
		
		if(frameNum!=recoveredFrames.size()){
			System.out.println("Not equal # of Frames, system shut down.");
			return;
		}
		
		// initialize the length of each frame
		String[] test = originalFrames.get(0).split("\\s+");
		double[] original = new double[frameNum];
		double[] recovered = new double[frameNum];
		
		lineLen=test.length;
		int divider = lineLen;
		double sum = 0.0;
		
		//First loop, confirm the column of each frame
		for (int i=0;i<lineLen;i++){
			
			//Second loop, Go through all the frames
			for(int j=0;j<frameNum;j++){
				String[] originalCurrLine = originalFrames.get(j).split("\\s+");
				String[] recoveredCurrLine = recoveredFrames.get(j).split("\\s+");
				
				original[j] = Double.parseDouble(originalCurrLine[i]);
				recovered[j] = Double.parseDouble(recoveredCurrLine[i]);
			}
			
			// Core part. Check correlation.
			double num = sc.correlation(original, recovered);
			
			// if result is NaN, remove from testing, else, add to sum
			if(!(Double.isNaN(num))){
				sum += num;
			}
			else{
				divider -= 1;
			}
		}
		
		System.out.println(1-(sum/divider));
		
	}
	
}
