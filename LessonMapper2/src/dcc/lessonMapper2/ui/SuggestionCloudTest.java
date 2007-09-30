package dcc.lessonMapper2.ui;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;

import lessonMapper.lom.LOMAttribute;
import dcc.lessonMapper2.LMInitializer;

/**
 * make measure on the use of the suggestion cloud
 * 
 * 
 * 
 * @author omotelet
 * 
 */
public class SuggestionCloudTest {

	public static boolean ISACTIVE = false;

	static SuggestionCloudTest ITSInstance;

	static Hashtable<String, Integer> ITSAttributesNumbers = new Hashtable<String, Integer>();
	static {
		ITSAttributesNumbers.put("general/title", 0);
		ITSAttributesNumbers.put("general/keyword", 1);
		ITSAttributesNumbers.put("general/description", 2);
		ITSAttributesNumbers.put("educational/learningresourcetype", 3);
		ITSAttributesNumbers.put("educational/difficulty", 4);
		ITSAttributesNumbers.put("educational/description", 5);
	}

	public static int SUCCESSFULLDRAG = 0, ERRORDRAG = 1, NOACTION = 2;

	public static SuggestionCloudTest getInstance() {
		if (ITSInstance == null)
			ITSInstance = new SuggestionCloudTest();
		return ITSInstance;
	}

	Date itsDate = new Date(System.currentTimeMillis());

	@SuppressWarnings("deprecation")
	String itsDateString = "" + itsDate.getMonth() + "_" + itsDate.getDate()
			+ "_" + itsDate.getHours() + "_" + itsDate.getMinutes() + "_";
	/**
	 * has the information of new session
	 */
	PrintStream itsMainCloudTestLog;

	/**
	 * has the log in the form:
	 *  pid/session-id/isCloud/event_type/attribute_number/keyNb/time/startdateHour/startDateMinute
	 */
	PrintStream itsCloudTestLog;

	int itsSessionNb = 0;
	int eventPid = 0;

	long itsFocusStartTime;
	boolean isDrag;

	//the Number of element in the sug windows
	int itsKeyNb;
	
	String itsLogDirectory = LMInitializer.iniFilesDirectory + File.separator + "log";
	
	

	public SuggestionCloudTest() {
		try {
			new File(itsLogDirectory).mkdir();
			itsMainCloudTestLog = new PrintStream( itsLogDirectory + File.separator+
					itsDateString + "Main"
							+ ".log");
			itsCloudTestLog = new PrintStream( itsLogDirectory + File.separator+
					itsDateString + "CloudTest" + ".log");
			/*	itsMainCloudTestLog = new PrintStream(System.getProperty("user.home")+
					File.pathSeparator+ "LM2Config"+File.pathSeparator +itsDateString + "Main"
							+ ".log");
			itsCloudTestLog = new PrintStream(System.getProperty("user.home")+
					File.pathSeparator+ "LM2Config"+File.pathSeparator 
					+ itsDateString + "Test" + ".log");*/
		} catch (Exception e) {
			System.out.println("--------cloudTest file not created");
		}
		for (Entry<String, Integer> theEntry : ITSAttributesNumbers.entrySet()) {
			itsMainCloudTestLog.println(theEntry.getValue() + " - "
					+ theEntry.getKey());
		}
	
		itsMainCloudTestLog.println("	SUCCESSFULLDRAG = 0, ERRORDRAG = 1, NOACTION = 2");
		itsMainCloudTestLog.println("pid/session-id/isCloud/event_type/attribute_number/keyNb/time/startdateHour/startDateMinute");
	}

	

	/**
	 * init the log file for the new session
	 * 
	 * @param isCloud
	 */
		public int printToggleCloudSession() {
		itsSessionNb++;
		itsMainCloudTestLog.println("Start Session " + itsSessionNb
				+ " at " + new Date(System.currentTimeMillis()));
		return itsSessionNb;
	}

	/**
	 * log a drag error if call with isDrag true then record current time
	 * 
	 * @param aAttribute
	 */
	@SuppressWarnings("deprecation")
	public void takeFocus(LOMAttribute aAttribute,int keyNb) {
		// mnanage alll that stufdff

		if (isDrag) {
			Date theDate = new Date(itsFocusStartTime);
			itsCloudTestLog.println(eventPid++ + " " + itsSessionNb + " "
					+ (isDragCloud ? 1 : 0) + " " + ERRORDRAG + " "
					+ ITSAttributesNumbers.get(itsDragAttribute.getName())
					+ " " + itsKeyNb +" "+(itsDragStartTime - itsFocusStartTime)+ " "+ theDate.getHours()+ " "+theDate.getSeconds());
			isDrag = false;
		}

		itsFocusStartTime = System.currentTimeMillis();
		itsKeyNb = keyNb;
		// itsCurrentAttribute=aAttribute
	}

	/**
	 * log a loose focus event if an element is not dragged idf isDrag register
	 * a drag error
	 * 
	 * @param aAttribute
	 */
	@SuppressWarnings("deprecation")
	public void looseFocus(LOMAttribute aAttribute,boolean isCloud) {
		Date theDate = new Date(itsFocusStartTime);
		if (isDrag) {
			itsCloudTestLog.println(eventPid++ + " " + itsSessionNb + " "
					+ (isDragCloud ? 1 : 0) + " " + ERRORDRAG + " "
					+ ITSAttributesNumbers.get(itsDragAttribute.getName())
					+ " " +itsKeyNb+ " "+(itsDragStartTime - itsFocusStartTime)+ " "+ theDate.getHours()+ " "+theDate.getSeconds());
			isDrag = false;
		} else {
			long theLostTime = System.currentTimeMillis() - itsFocusStartTime;
			itsCloudTestLog.println(eventPid++ + " " + itsSessionNb + " "
					+ (isCloud ? 1 : 0) + " " + NOACTION + " "
					+ ITSAttributesNumbers.get(aAttribute.getName()) + " "+
					itsKeyNb +" "+ (theLostTime)+ " "+ theDate.getHours()+ " "+theDate.getSeconds());
		}
	}

	long itsDragStartTime = 0;
	LOMAttribute itsDragAttribute;
	boolean isDragCloud;

	/**
	 * register drag start time and set drag mode
	 * 
	 * @param aAttribute
	 */
	public void beginDragEvent(LOMAttribute aAttribute,boolean isCloud) {
		isDrag = true;
		itsDragStartTime = System.currentTimeMillis();
		itsDragAttribute = aAttribute;
		isDragCloud=isCloud;
	}

	/**
	 * log the time of the drag event
	 */
	@SuppressWarnings("deprecation")
	public void endDragEvent(LOMAttribute aAttribute) {
		Date theDate = new Date(itsFocusStartTime);
		if (isDrag) {
			isDrag = false;
			// if the drag action fill an attribute
			if (aAttribute != null) {
				itsCloudTestLog.println(eventPid++ + " " + itsSessionNb + " "
						+ (isDragCloud ? 1 : 0) + " " + SUCCESSFULLDRAG + " "
						+ ITSAttributesNumbers.get(itsDragAttribute.getName())
						+ " " + itsKeyNb+ " "+(itsDragStartTime - itsFocusStartTime)+ " "+ theDate.getHours()+ " "+theDate.getSeconds());
			}
		}
	}
}
