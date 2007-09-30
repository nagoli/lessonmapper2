package dcc.lessonMapper2;



/**
 * All Undoable command should be executed by the undo manager:
 * UndoManager.proceed(Command)
 * @author omotelet
 *
 */
public interface UndoableCommand {

	public void proceed();
	
	public void undo();
	
	public void redo();
	
	
}
