package dcc.lessonMapper2;

import java.util.Stack;

public class UndoManager {

	static UndoManager ITSInstance;
	
	public static UndoManager getInstance(){
		if (ITSInstance == null)
			ITSInstance = new UndoManager();
			return ITSInstance;
		
	}
	
	
	public static void execute(UndoableCommand aCommand){
		getInstance().proceed(aCommand);
	}
	
	public static void undo(){
		getInstance().proceedUndo();
	}
	
	public static void redo(){
		getInstance().proceedRedo();
	}
	
	
	
	public Stack<UndoableCommand> itsUndoStack, itsRedoStack;
	
	
	public UndoManager() {
		itsUndoStack = new Stack<UndoableCommand>();
		itsRedoStack = new Stack<UndoableCommand>();
	}
	
	
	
	public void proceed(UndoableCommand aCommand){
		aCommand.proceed();
		itsUndoStack.push(aCommand);
		itsRedoStack.clear();
	}
	
	public void proceedUndo(){
		UndoableCommand theCommand = itsUndoStack.pop();
		theCommand.undo();
		itsRedoStack.push(theCommand);
	}
	
	public void proceedRedo(){
		UndoableCommand theCommand = itsRedoStack.pop();
		theCommand.redo();
		itsUndoStack.push(theCommand);
	}
	
	
}
