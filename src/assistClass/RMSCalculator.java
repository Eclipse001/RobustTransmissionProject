package assistClass;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RMSCalculator{
	
	public String originFilePath;
	public String recoveredFilePath;
	
	private ArrayList<String> originalFrames;
	private ArrayList<String> recoveredFrames;
	
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
		
		Double sumSquare=0.0;
		
		for(int i=0;i<frameNum;i++){
			
			String[] originalCurrLine = originalFrames.get(i).split("\\s+");
			String[] recoveredCurrLine = recoveredFrames.get(i).split("\\s+");
			
			lineLen=originalCurrLine.length;
			
			Double lineSumSquare=0.0;
			
			for(int j=0;j<lineLen;j++){
				Double originVal=Double.parseDouble(originalCurrLine[j]);
				Double recoveredVal=Double.parseDouble(recoveredCurrLine[j]);
				
				Double currDiff=Math.abs(originVal-recoveredVal);
				
				lineSumSquare+=Math.pow(currDiff,2);
			}
			
			Double lineMeanSquare=lineSumSquare/(double)lineLen;
			
			sumSquare+=lineMeanSquare;
			
		}
		
		Double rms=Math.pow(sumSquare/(double)frameNum,(double)0.5);
		
		System.out.println(rms.toString());
		
	}
	
}

