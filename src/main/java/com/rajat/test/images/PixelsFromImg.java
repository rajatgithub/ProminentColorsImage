package com.rajat.test.images;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.activation.MimetypesFileTypeMap;

/**
 * Hello world!
 *
 */
public class PixelsFromImg 
{
	
	
    public static void main( String[] args ) throws Exception
    {
    
    	File folder =  null;
    	if( args.length ==  1) 
    	{
    		folder = new File(args[0]);
    	} else {
    		folder = new File(System.getProperty("user.dir"));
    	}
    	
    	int totalColors = 24;
    	int colorsToPrinted = 10;
    	
        String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        File logFile = new File(folder.getAbsolutePath()+ File.separatorChar+ timeLog + ".txt");

        // This will output the full path where the file will be written to...
        //System.out.println(logFile.getCanonicalPath());

        BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
    	
		File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	        //System.out.println("File " + listOfFiles[i].getName());
	    	  String mimeType = new MimetypesFileTypeMap().getContentType(listOfFiles[i]);
	    			  //new MimetypesFileTypeMap().getContentType( listOfFiles[i]));
	    	   // mimeType should now be something like "image/png"
	    	   if(mimeType.substring(0,5).equalsIgnoreCase("image")){
	    		   getMaxUsedColors(listOfFiles[i], totalColors, colorsToPrinted, writer);
	    		   writer.newLine();
	    	   }
	    	  
	      } else if (listOfFiles[i].isDirectory()) {
	        //System.out.println("Directory " + listOfFiles[i].getName());
	      }
	    }
	   
	    writer.close();
	    
	    System.out.println("Done!");
	    System.out.println("The output is written to file " +  logFile.getCanonicalPath());
    }
    
	public static void getMaxUsedColors(File imageFile, int totalColors, int colorsToPrinted, BufferedWriter writer ) throws IOException
	{   
    	//File imageFile = new File("C:\\Users\\rajat.mahajan\\Downloads\\Together-2015-04-02\\Together\\Image13.jpg"); 
    	
    	ImageInputStream is = ImageIO.createImageInputStream(imageFile);
         Iterator<?> iter = ImageIO.getImageReaders(is);
         if(iter == null)
        	 return;
         
         int delta = totalColors/2;
         
         ImageReader imageReader = (ImageReader)iter.next();
         imageReader.setInput(is);

         BufferedImage image = imageReader.read(0);

         int height = image.getHeight();
         int width = image.getWidth();
         int totalPixelCount = height * width;
         
         Map<String, Integer> colorsWithCount = new HashMap<String, Integer>();
         for(int i=0; i < width ; i++)
         {
             for(int j=0; j < height ; j++)
             {
                 int rgb = image.getRGB(i, j);
                 int[] rgbArr = getRGBArr(rgb);  
                 
                 for (int k = 0; k<rgbArr.length; k++) {
                	 rgbArr[k] = ( ( rgbArr[k] + delta ) / totalColors ) * totalColors;
                	 if (rgbArr[k] > 255)
                		 rgbArr[k] = 255;
                 }
                 
				String hexCode = RGBToHexCode(rgbArr);
				Integer counter = colorsWithCount.get(hexCode);
				if (counter == null)
					counter = 0;
				counter++;
				colorsWithCount.put(hexCode, counter);               
                                
             }
         }
    
         List<String> colorHexs = getNMostCommonColour(colorsWithCount, colorsToPrinted);
         //System.out.println(imageFile.getName()); 
         //System.out.println("HexCode, Percentage");
         
         writer.write(imageFile.getName());
         writer.newLine();
         writer.write("HexCode, Percentage");
         writer.newLine();
         
         for( String colorHex : colorHexs) {
        	 //System.out.println(colorHex + "," + (float)colorsWithCount.get(colorHex)/totalPixelCount );
             writer.write(colorHex + "," + (float)colorsWithCount.get(colorHex)/totalPixelCount );
             writer.newLine();
         }
    }
    
    public static List<String> getNMostCommonColour(Map<String, Integer> map, int n) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
              public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                  .compareTo(((Map.Entry) (o2)).getValue());
              }
        });    
        
        List<String> colorPalette = new ArrayList<String>();
        for ( int i = 1; i <= n;  i++ ) {
	        Map.Entry me = (Map.Entry )list.get(list.size()-i);
	        String  hexCode = (String)me.getKey();
	        colorPalette.add(hexCode);    
	    }
        return colorPalette;
    }    

    public static int[] getRGBArr(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        return new int[]{red,green,blue};

    }
    
    private static String RGBToHexCode(int[] rgb) {
    	String hexCode = null;
    	if (rgb.length == 3) {
            hexCode = Integer.toHexString(rgb[0])+Integer.toHexString(rgb[1])+Integer.toHexString(rgb[2]);
    	}
    	return hexCode;
    }
    
    public static boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        // Filter out black, white and grays...... (tolerance within 10 pixels)
        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance) 
            if (rbDiff > tolerance || rbDiff < -tolerance) { 
                return false;
            }                 
        return true;
    }
}
