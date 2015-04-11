package mainPackage;
import assistClass.Boundary;
import assistClass.InputData;
import assistClass.RMSCalculator;
import singleRecoveryMethods.AveragingMethod;
import singleRecoveryMethods.BezierCurve;
import singleRecoveryMethods.CombinationMethod;
import singleRecoveryMethods.TempReplacement;
import singleRecoveryMethods.TempReplacementBackward;
import singleRecoveryMethods.ZeroInsertion;


public class Main {
	
	static String originFilePath="C:\\Users\\Xuping Fang\\Documents\\origin.bvh";
	static String badFilePath="C:\\Users\\Xuping Fang\\Documents\\bad.bvh";
	
	static int packageCapacity=200;
	static double errorRate=0.5;
	
	static String zeroInsertionRecoveredFilePath="C:\\Users\\Xuping Fang\\Documents\\ZeroInsertion.bvh";
	
	static String averagingMethodRecoveredFilePath="C:\\Users\\Xuping Fang\\Documents\\AveragingMethod.bvh";
	
	static String tempReplacementRecoveredFilePath="C:\\Users\\Xuping Fang\\Documents\\TempReplacement.bvh";
	static String tempReplacementBackwardRecoveredFilePath="C:\\Users\\Xuping Fang\\Documents\\TempReplacementBack.bvh";
	
	
	static String bezierCurveRecoveredFilePath="C:\\Users\\Xuping Fang\\Documents\\bezierCurve.bvh";
	
	static String combinedRecoveredFilePath="C:\\Users\\Xuping Fang\\Documents\\combined.bvh";
	

	public static void main(String[] args) {
		
		PackageLossSimulator pls=new PackageLossSimulator(originFilePath,badFilePath,packageCapacity,errorRate);
		
		InputData inputData=pls.simulatePackageLoss();
		
		System.out.println("=======================Test Results For each methods==========================");
		
		runBezierCurve(inputData);
		runAveragingMethod(inputData);
		
		runTempReplacement(inputData);
		runBackwardTempReplacement(inputData);
		
		runZeroInsertion(inputData);
		
		
		runCombinationMethod(inputData);

	}
	
	private static void runCombinationMethod(InputData inputData){
		
		Boundary avgBoundary=new Boundary(0,5);
		Boundary bezierCurveBoundary=new Boundary(6,20);
		Boundary tempReplacementBoundary=new Boundary(21,Integer.MAX_VALUE);
		
		CombinationMethod cm=new CombinationMethod(inputData,combinedRecoveredFilePath,
				true,avgBoundary,bezierCurveBoundary,tempReplacementBoundary);
		
		cm.doRecovery();
		
		RMSCalculator rmsC=new RMSCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=combinedRecoveredFilePath;
		
		System.out.print("RMS value for combined method: ");
		
		rmsC.doCaculation();
	}
	
	private static void runBezierCurve(InputData inputData){
		
		BezierCurve bc=new BezierCurve(inputData,bezierCurveRecoveredFilePath);
		
		bc.runRecovery();
		
		RMSCalculator rmsC=new RMSCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=bezierCurveRecoveredFilePath;
		
		System.out.print("RMS value for bezier curve with 4 control points: ");
		
		rmsC.doCaculation();
	}
	
	private static void runAveragingMethod(InputData inputData){
		
		AveragingMethod am=new AveragingMethod(inputData,averagingMethodRecoveredFilePath);
		
		am.runRecovery();
		
		RMSCalculator rmsC=new RMSCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=averagingMethodRecoveredFilePath;
		
		System.out.print("RMS value for averaging methods: ");
		rmsC.doCaculation();
	}
	
	private static void runTempReplacement(InputData inputData){
		
		TempReplacement tr=new TempReplacement(inputData,tempReplacementRecoveredFilePath);
		
		tr.runRecovery();
		
		RMSCalculator rmsC=new RMSCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=tempReplacementRecoveredFilePath;
		
		System.out.print("RMS value for temp replacement (previous): ");
		rmsC.doCaculation();
	}
	
private static void runBackwardTempReplacement(InputData inputData){
		
		TempReplacementBackward tr=new TempReplacementBackward(inputData,tempReplacementBackwardRecoveredFilePath);
		
		tr.runRecovery();
		
		RMSCalculator rmsC=new RMSCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=tempReplacementBackwardRecoveredFilePath;
		
		System.out.print("RMS value for backward temp replacement: ");
		rmsC.doCaculation();
	}
	
	private static void runZeroInsertion(InputData inputData){
		
		ZeroInsertion zi=new ZeroInsertion(inputData,zeroInsertionRecoveredFilePath);
		
		zi.runRecovery();
		
		RMSCalculator rmsC=new RMSCalculator();
		
		rmsC.originFilePath=originFilePath;
		rmsC.recoveredFilePath=zeroInsertionRecoveredFilePath;
		
		System.out.print("RMS value for zero insertion: ");
		rmsC.doCaculation();
	}

}
