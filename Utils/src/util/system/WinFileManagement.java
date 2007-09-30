package util.system;



public class WinFileManagement extends FileManagement {

	String itsCMD;
	
	public String getCMD(){
		if (itsCMD!=null) return itsCMD;
		if (System.getProperty("os.name").equals("Windows 95")){
			itsCMD="command.com";
		}else itsCMD = "cmd.exe";
		return itsCMD;
	}

	/**
	 * open the 
	 */
	
	@Override
	public void openFile(String aFile, boolean isPresentationMode) {
		
				String[] command = new String[3];
		
	        String fileProtocolHandler;
	      
	        fileProtocolHandler ="rundll32 url.dll,FileProtocolHandler ";
	       
	   /*     if (aFile.endsWith("ppt") ) {
	            // make the powerpoint files showed directlty in slide show mode
	            command[0] = "\"c:\\Program Files\\Microsoft Office\\Office\\POWERPNT.EXE\" /s \"" + aFile + "\"";
	        } else*/
	        command[0] = getCMD();
			command[1]= "/C";     
	        command[2] = fileProtocolHandler+ aFile;

	      
	        try {
				Runtime.getRuntime().exec(command);

			} catch (Exception e) {
				e.printStackTrace();
			}
	    

		
	}

	
	
	
	
}
