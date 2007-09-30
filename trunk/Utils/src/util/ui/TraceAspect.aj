package util.ui;
/**
 * make trace of the call to the class defined in the poincut theclass
 */
public aspect TraceAspect {
/*
	pointcut theCalls(): execution(* dcc.lessonMapper2.ui.eventHandler.SelectionHandler.*(..));

	before():theCalls(){
		System.out.println("enter method"+thisJoinPoint.getSignature());
	}
	
	after():theCalls(){
		System.out.println("exit method"+thisJoinPoint.getSignature());
	}
	*/
	
}
