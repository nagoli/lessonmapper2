package dcc.lessonMapper2;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

public class LMApplicationHandlerForMac extends ApplicationAdapter{

	public LMApplicationHandlerForMac(){
		//Application.getApplication().removeApplicationListener(null);
		//Application.getApplication().addAboutMenuItem();
		Application.getApplication().addApplicationListener(this);
		
	}
	
	public void handleQuit(ApplicationEvent aArg0) {
		LessonMapper2.getInstance().close();
		
	}

	public void handleAbout(ApplicationEvent aArg0) {
		LessonMapper2.getInstance().itsPresentation.setVisible(true);
		
	}
	
	
	
}
