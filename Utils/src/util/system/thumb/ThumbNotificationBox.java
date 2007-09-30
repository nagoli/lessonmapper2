package util.system.thumb;

import java.awt.image.BufferedImage;

public class ThumbNotificationBox {

	
	  private boolean full = false;
	  private BufferedImage itsImage ;

	  synchronized void push(BufferedImage aImage)
	    {
	      try
		{
		  while (full) wait();
		}
	      catch(InterruptedException exc) {}
	      full=true;
	      itsImage = aImage;
	      notify();
	    }

	  synchronized BufferedImage pop()
	    {
	      try
		{
		  while (!full) wait();
		}
	      catch(InterruptedException exc) {
	    	  return null;
	      }
	      full = false;
	      notify();
	      return itsImage;
	   }
	
}
