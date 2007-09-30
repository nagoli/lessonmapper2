package util.system;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MacFileManagement extends FileManagement {

	URL ITSKeynotePresentationPlug = getClass().getResource("keynoteOpen.app");
	
	/*
	 * @Override public boolean copy(String aFile, String anotherFile) { if
	 * (!aFile.startsWith("http://") && !aFile.startsWith("ftp://")) { if
	 * (aFile.startsWith("file://localhost")) aFile =
	 * aFile.substring(aFile.indexOf("t") + 1); File file = new
	 * File(anotherFile); file.getParentFile().mkdirs(); try {
	 * System.out.println(file.createNewFile()); } catch (IOException e1) {
	 * e1.printStackTrace(); } String[] command = new String[3]; command[0] =
	 * "/bin/cp"; command[1]= aFile; command[2]= anotherFile;
	 * //System.out.println(command); try { Runtime.getRuntime().exec(command);
	 *  } catch (Exception e) { e.printStackTrace(); return false; } return
	 * true; } else { String[] command = new String[3]; command[0] =
	 * "/sw/bin/wget"; command[1] = "aFile"; command[3] = "-O " + anotherFile;
	 * System.out.println(command); try { Runtime.getRuntime().exec(command);
	 *  } catch (Exception e) { e.printStackTrace(); return false; } return
	 * true; } }
	 */
	@Override
	public void openFile(String aFile, boolean isPresentationMode) {
		//super.openFile(aFile);
		directOpen(aFile, isPresentationMode);
	}

	private void directOpen(String aFile, boolean isPresentationMode) {
		//aFile = "\""+aFile+"\"";
		
		if (isPresentationMode){
			//not working powerpoint based versioin
			if (false && aFile.endsWith(".ppt")){
				String[] command = new String[3];
				command[0] = "/bin/cp";
				command[1] = aFile;
				command[2] = aFile.substring(0, aFile.length()-4)+ ".pps";
				try {
					Process theProcess = Runtime.getRuntime().exec(command);
					System.out.println(command[0]+" "+ command[1]+" "+command[2]);
					System.out.println();
					InputStream in = theProcess.getErrorStream();
				        int c;
				        while ((c = in.read()) != -1) {
				            System.out.print((char)c);
				        }
				        in.close();
					
					theProcess.waitFor();
					String[] command2 = new String[4];
					command2[0] = "/usr/bin/open";
					command2[1] = "-b";
					command2[2] = "microsoft.powerpoint";
					command2[3] = command[2];
					
					String[] command3 = new String[2];
					command3[0] = "open";
					command3[1] = command[2];
						 theProcess = Runtime.getRuntime().exec(command2);
						// theProcess = Runtime.getRuntime().exec();
						System.out.println(command2[0]+" "+ command2[1]+" "+command2[2]+" " +command2[3]);
						System.out.println();
						 // Get the input stream and read from it
				         in = theProcess.getErrorStream();
				         while ((c = in.read()) != -1) {
				            System.out.print((char)c);
				        }
				        in.close();
						return;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("error copying/opening File as .pps");
				}
			}
			if (aFile.endsWith(".key") || aFile.endsWith(".ppt" )){
				String[] command = new String[4];
				command[0] = "/usr/bin/open";
				command[1] = "-b";
				command[2] = "com.apple.iWork.Keynote";
			
				command[3] = aFile;
				try {
					Process theProcess = Runtime.getRuntime().exec(command);
					theProcess.waitFor();
					command[2] = "automator.keynoteOpen";
					theProcess = Runtime.getRuntime().exec(command);
					
					System.out.println(command[0]+" "+ command[1]+" "+command[2]+" "+command[3]);
					System.out.println();
					InputStream in = theProcess.getErrorStream();
				        int c;
				        while ((c = in.read()) != -1) {
				            System.out.print((char)c);
				        }
				        in.close();	
					return;
				} catch (Exception e) {
					System.out.println("Error opening presentation plug " + aFile);
				}
			
			}
		}
		//else super.openFile(aFile, isPresentationMode);
		String[] command = new String[2];
		command[0] = "/usr/bin/open";
		command[1] = aFile;
		try {
			Process theProcess = Runtime.getRuntime().exec(command);
			System.out.println(command[0]+" "+ command[1]);
			System.out.println();
			InputStream in = theProcess.getErrorStream();
		        int c;
		        while ((c = in.read()) != -1) {
		            System.out.print((char)c);
		        }
		        in.close();	
		} catch (IOException e) {
			System.out.println("Error opening file " + aFile);
		}
	}

}
