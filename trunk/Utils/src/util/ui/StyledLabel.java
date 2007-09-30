package util.ui;

import java.awt.Color;

import javax.swing.JLabel;


/**
 * extends the constructors of JLabel to accept color and style paramenters
 * @author omotelet
 *
 */

public class StyledLabel extends JLabel {


	public StyledLabel(String text){
		super(text);
		setBackground(null);
	}
	
	
	public StyledLabel(String text, Color aColor){
		this(text);
		setForeground(aColor);
	}
	
	/**
	 * aFontStyle is attribute of class Font (ex Font.ITALIC)
	 * @param aText
	 * @param aFontStyle
	 */
	public StyledLabel(String text, int aFontStyle ){
		this(text);
		setFont(getFont().deriveFont(aFontStyle));
	}
	/**
	 * aFontStyle is attribute of class Font (ex Font.ITALIC)
	 * @param aText
	 * @param the
	 */
	public StyledLabel(String text, Color aColor, int aFontStyle){
		this(text);
		setFont(getFont().deriveFont(aFontStyle));
		setForeground(aColor);
	}
	
	/**
	 * aFontStyle is attribute of class Font (ex Font.ITALIC)
	 * @param aText
	 * @param the
	 */
	public StyledLabel(String text, Color aColor, int aFontStyle, Color aBackgroundColor ){
		this(text,aColor,aFontStyle);
		setBackground(aBackgroundColor);
	}
	
	
	
	/**
	 * aFontStyle is attribute of class Font (ex Font.ITALIC)
	 * @param aText
	 * @param the
	 */
	public StyledLabel(String text, Color aColor, float aFontSize ){
		this(text);
		setFont(getFont().deriveFont(aFontSize));
		setForeground(aColor);
	}
	
	
}
