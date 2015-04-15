package assistClass;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

public class RMSCalculator{
	
	public String originFilePath;
	public String recoveredFilePath;
	
	private ArrayList<String> originalFrames;
	private ArrayList<String> recoveredFrames;
	private static SpearmansCorrelation sc = new SpearmansCorrelation();
	
	public RMSCalculator(){
		
	}
	
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
	
	public void doCaculation(){
		
		readOriginalFile();
		readRecoveredFile();
		
		int frameNum=originalFrames.size();
		int lineLen=0;
		
		if(frameNum!=recoveredFrames.size()){
			System.out.println("Not equal # of Frames, system shut down.");
			return;
		}
		
		String[] test = originalFrames.get(0).split("\\s+");
		double[] original = new double[frameNum];
		double[] recovered = new double[frameNum];
		
		lineLen=test.length;
		int divider = lineLen;
		double sum = 0.0;
		
		for (int i=0;i<lineLen;i++){
			
			for(int j=0;j<frameNum;j++){
				String[] originalCurrLine = originalFrames.get(j).split("\\s+");
				String[] recoveredCurrLine = recoveredFrames.get(j).split("\\s+");
				
				original[j] = Double.parseDouble(originalCurrLine[i]);
				recovered[j] = Double.parseDouble(recoveredCurrLine[i]);
			}
			
			double num = sc.correlation(original, recovered);
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
