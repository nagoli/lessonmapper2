package util.system.thumb;

import java.io.File;

public class MacThumbMaker extends ThumbMaker {

	
	
	Object itsMutex = new Object();
	
	
	
	
	
	/**
	 * try to generate a thumb automatically for this file using thumbnailMaker
	 * package
	 */
	public synchronized void makeAutomaticThumb(String aFileName,
			String aThumbName) throws ThumbNotBuildException {
		int theTimeOut = 30;
		int theTime = 0;
		String[] command = new String[3];
		command[0]= "/USERDISK/develop/thumbnailMaker/printThumb.sh";
		command[1] = aFileName;
		command[2] = aThumbName;
		synchronized (itsMutex) {
			try {
				File theLock = new File("/USERDISK/develop/thumbnailMaker/lock");
				if (theLock.exists())
					Runtime.getRuntime().exec(
							"/USERDISK/develop/thumbnailMaker/reset.sh");
				Runtime.getRuntime().exec(command);
				Thread.sleep(1000); // let the time to the other
				// process to take the lock
				while (theLock.exists() && theTime < theTimeOut) {
					System.out.println("wait for lock");
					theTime += 1;
					Thread.sleep(3000); // wait for the lock to
														// be
					// released
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					System.out.println("reset thumbMaker");
					Runtime.getRuntime().exec(
							"/USERDISK/develop/thumbnailMaker/reset.sh");
				} catch (Exception e2) {
					System.out.println("reset has failed");
					e2.printStackTrace();
				}
				throw new ThumbNotBuildException();
			}
			try {
				System.out.println("reset thumbMaker");
				Runtime.getRuntime().exec(
						"/USERDISK/develop/thumbnailMaker/reset.sh");
			} catch (Exception e) {
				System.out.println("reset has failed");
				e.printStackTrace();
			}
		}
		if (!new File(aThumbName).exists())
			throw new ThumbNotBuildException();
	}

	/**
	 * try to generate a thumb automatically for this file using thumbnailMaker
	 * package
	 */
	/*public void makeInteractiveThumb(String aFileName, String aThumbName)
			throws ThumbNotBuildException {
			
		
		
		int theTimeOut = 30;
		int theTime = 0;
		String[] command = new String[3];
		command[0]= "/USERDISK/develop/thumbnailMaker/takeThumb.sh";
		command[1] = aFileName;
		command[2] = aThumbName;
		synchronized (itsMutex) {
			try {
				File theLock = new File("/USERDISK/develop/thumbnailMaker/lock");
				if (theLock.exists())
					Runtime.getRuntime().exec(
							"/USERDISK/develop/thumbnailMaker/reset.sh");
				Runtime.getRuntime().exec(command);
				Thread.currentThread().sleep(1000); // let the time to the other
				// process to take the lock
				while (theLock.exists() && theTime < theTimeOut) {
					System.out.println("wait for lock");
					theTime += 1;
					Thread.currentThread().sleep(3000); // wait for the lock to
					// be
					// released
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					System.out.println("reset thumbMaker");
					Runtime.getRuntime().exec(
							"/USERDISK/develop/thumbnailMaker/reset.sh");
				} catch (Exception e2) {
					System.out.println("reset has failed");
					e2.printStackTrace();
				}
				throw new ThumbNotBuildException();
			}
			try {
				System.out.println("reset thumbMaker");
				Runtime.getRuntime().exec(
						"/USERDISK/develop/thumbnailMaker/reset.sh");
			} catch (Exception e) {
				System.out.println("reset has failed");
				e.printStackTrace();
			}
		}
		if (!new File(aThumbName).exists())
			throw new ThumbNotBuildException();
			
	}*/

	
	
}
