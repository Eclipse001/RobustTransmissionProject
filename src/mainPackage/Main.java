package mainPackage;
import assistClass.Boundary;
import assistClass.InputData;
import assistClass.CorrelationCalculator;
import singleRecoveryMethods.AveragingMethod;
import singleRecoveryMethods.BezierCurve;
import singleRecoveryMethods.CombinationMethod;
import singleRecoveryMethods.TempReplacement;
import singleRecoveryMethods.TempReplacementBackward;
import singleRecoveryMethods.ZeroInsertion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.System;


public class Main {
	
	// Settings of the whole simulation will be adjust here.
	static String originFilePath="/cshome/jcheng1/414/2.bvh";
	static String badFilePath="/cshome/jcheng1/414/bad.bvh";
	
	static int packageCapacity=10;
	static double errorRate=0.5;
	
	static String zeroInsertionRecoveredFilePath="/cshome/jcheng1/414/ZeroInsertion.bvh";
	
	static String averagingMethodRecoveredFilePath="/cshome/jcheng1/414/AveragingMethod.bvh";
	
	static String tempReplacementRecoveredFilePath="/cshome/jcheng1/414/TempReplacement.bvh";
	static String tempReplacementBackwardRecoveredFilePath="/cshome/jcheng1/414/TempReplacementBack.bvh";
	
	
	static String bezierCurveRecoveredFilePath="/cshome/jcheng1/414/bezierCurve.bvh";
	
	static String combinedRecoveredFilePath="/cshome/jcheng1/414/combined.bvh";
	

	public static void main(String[] args) {
		
		// Run simulation.
		int times = 0;
		String input = null;
		System.out.println("=======================Test Results For each methods==========================");
		System.out.println("at:Temporary and Average");
		System.out.println("bt:Beizer and Temporary");
		System.out.println("ba:Beizer and Average");
		System.out.println("other:Beizer, Average and Temporary");
		System.out.print("Please choose combined method by enter number:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try{
			input = br.readLine();
		}catch (IOException e){
			
		}

		while (times<5){
			PackageLossSimulator pls=new PackageLossSimulator(originFilePath,badFilePath,packageCapacity,errorRate);			
			InputData inputData=pls.simulatePackageLoss();
			// Run different recovery methods.
			
			runBezierCurve(inputData);
			runAveragingMethod(inputData);		
			runTempReplacement(inputData);
			runBackwardTempReplacement(inputData);
			runZeroInsertion(inputData);
			//runCombinationMethod(inputData,"at");
			//runCombinationMethod(inputData,"bt");
			//runCombinationMethod(inputData,"ba");
			runCombinationMethod(inputData,input);
			times+=1;
		}


	}
	
	// Each function below takes same input, runs the specific recovery method and print the correlation value of original file and the recovered file.
	// ==================================================================================================================================================
	private static void runCombinationMethod(InputData inputData, String combinednumber){
		// Setting of the combination method will be adjust here:
		Boundary avgBoundary=new Boundary(packageCapacity,packageCapacity+packageCapacity);
		Boundary bezierCurveBoundary=new Boundary(packageCapacity+packageCapacity,Integer.MAX_VALUE);
		Boundary tempReplacementBoundary=new Boundary(0,packageCapacity);
		if (combinednumber=="at"){
			System.out.println("You have chosen average and temporal combined\n");
			avgBoundary=new Boundary(packageCapacity+1,Integer.MAX_VALUE);
			tempReplacementBoundary=new Boundary(0,packageCapacity);
			//bezier curve get ignored
			bezierCurveBoundary=new Boundary(0,0);
		}else if (combinednumber=="bt"){
			System.out.println("You have chosen bezier and temporal combined\n");
			bezierCurveBoundary=new Boundary(packageCapacity+1,Integer.MAX_VALUE);
			tempReplacementBoundary=new Boundary(0,packageCapacity);
			//average curve get ignored
			avgBoundary=new Boundary(0,0);
		}else if (combinednumber=="ba"){
			System.out.println("You have chosen average and bezier combined\n");
			avgBoundary=new Boundary(0,packageCapacity);
			bezierCurveBoundary=new Boundary(packageCapacity+1,Integer.MAX_VALUE);
			//temporal replacement get ignored
			tempReplacementBoundary=new Boundary(0,0);
		}else{
			System.out.println("You have chosen average,temporal and bezier combined\n");
			avgBoundary=new Boundary(packageCapacity+1,packageCapacity+packageCapacity);
			bezierCurveBoundary=new Boundary(packageCapacity+packageCapacity+1,Integer.MAX_VALUE);
			tempReplacementBoundary=new Boundary(0,packageCapacity);
		}
		
		CombinationMethod cm=new CombinationMethod(inputData,combinedRecoveredFilePath,
				true,avgBoundary,bezierCurveBoundary,tempReplacementBoundary);
		
		cm.doRecovery();
		
		CorrelationCalculator rmsC=new CorrelationCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=combinedRecoveredFilePath;
		
		System.out.print("Correlation value for combined method"+combinednumber+": ");
		
		rmsC.doCaculation();
	}
	
	private static void runBezierCurve(InputData inputData){
		
		BezierCurve bc=new BezierCurve(inputData,bezierCurveRecoveredFilePath);
		
		bc.runRecovery();
		
		CorrelationCalculator rmsC=new CorrelationCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=bezierCurveRecoveredFilePath;
		
		System.out.print("Correlation value for bezier curve with 4 control points: ");
		
		rmsC.doCaculation();
	}
	
	private static void runAveragingMethod(InputData inputData){
		
		AveragingMethod am=new AveragingMethod(inputData,averagingMethodRecoveredFilePath);
		
		am.runRecovery();
		
		CorrelationCalculator rmsC=new CorrelationCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=averagingMethodRecoveredFilePath;
		
		System.out.print("Correlation value for averaging methods: ");
		rmsC.doCaculation();
	}
	
	private static void runTempReplacement(InputData inputData){
		
		TempReplacement tr=new TempReplacement(inputData,tempReplacementRecoveredFilePath);
		
		tr.runRecovery();
		
		CorrelationCalculator rmsC=new CorrelationCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=tempReplacementRecoveredFilePath;
		
		System.out.print("Correlation value for temp replacement (previous): ");
		rmsC.doCaculation();
	}
	
	private static void runBackwardTempReplacement(InputData inputData){
		
		TempReplacementBackward tr=new TempReplacementBackward(inputData,tempReplacementBackwardRecoveredFilePath);
		
		tr.runRecovery();
		
		CorrelationCalculator rmsC=new CorrelationCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=tempReplacementBackwardRecoveredFilePath;
		
		System.out.print("Correlation value for backward temp replacement: ");
		rmsC.doCaculation();
	}
	
	private static void runZeroInsertion(InputData inputData){
		
		ZeroInsertion zi=new ZeroInsertion(inputData,zeroInsertionRecoveredFilePath);
		
		zi.runRecovery();
		
		CorrelationCalculator rmsC=new CorrelationCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=zeroInsertionRecoveredFilePath;
		
		System.out.print("Correlation value for zero insertion: ");
		rmsC.doCaculation();
	}

}
