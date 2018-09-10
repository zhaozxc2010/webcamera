package webcam;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class Utils
{
	public static int getScreenWidth() {
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    return screenSize.width;
	  }

	  public static int getScreenHeight() {
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    return screenSize.height;
	  }

	  public static int getWindowCenterWidth(int windowWidth) {
	    int screenWidth = getScreenWidth();
	    int width = screenWidth / 2 - windowWidth / 2;
	    return width;
	  }

	  public static int getWindowCenterHeight(int windowHeight) {
	    int screenHeight = getScreenHeight();
	    int height = screenHeight / 2 - windowHeight / 2;
	    return height;
	  }
	  
	  /**
		 * 获取配置文件中的值
		 * @param key
		 * @return
		 */
		public static String getPropertyValue(Class clazz,String propertyName,String key){
			String value = null;
			try {
				if(propertyName.indexOf(".properties")==-1){
					propertyName = propertyName + ".properties";
				}
				Properties prop = new Properties();
				InputStream in = null;
				
				if  (clazz !=  null ) {        
					in = clazz.getResourceAsStream(propertyName);        
				}  else {        
					//in = ClassLoader.getSystemResourceAsStream(propertyName);        
				}
				if(in!=null){
					prop.load(in);
					value = prop.getProperty(key)==null?"":prop.getProperty(key).trim();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return value;
		}
		/**
	 	 * 获取当前日期格式化的时间
	 	 * @param d
	 	 * @param num
	 	 * @return
	 	 */
	 	public static String getCurrentDateStr(String format){
	 		SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(new Date());
	 	}
	/**
	 * 图片重命名为国标码
	 * @param path
	 * @param text
	 */
	public static String updateImgName(String path, String text)
	{
		if(text.equals("")){
			return "";
		}
    	String barcode = text.replace("\n", "_").trim();
    	if(barcode.substring(barcode.length()-1).equals("_")){
    		barcode = barcode.substring(0, barcode.length()-1);
    	}
    	
    	File file = new File(path);//File类型可以是文件也可以是文件夹
    	File[] fileList = file.listFiles();//将该目录下的所有文件放置在一个File类型的数组中
    	for(File fileObj : fileList){
    		if(fileObj.isFile()){
    			String fileAllName = fileObj.getName();
        		String fileName = fileAllName.substring(0,fileAllName.lastIndexOf("."));
        		String fileType = fileAllName.substring(fileAllName.lastIndexOf("."));
        		if(fileAllName.indexOf("-") == -1){
        			File newFile = new File(path + barcode + "-" + getCurrentDateStr("YYYYMMddHHmmssSSS") + fileType);
        			fileObj.renameTo(newFile);
        			return newFile.getAbsolutePath();
        		}
    		}
    	}
    	return "";
	} 
	
    
    public static void corpImg(String path, int targetWidth, int targetHeight){
        try{
        	//String path="C:/1.jpg";    //输入图片  测试要在C盘放一张图片1.jpg
	    	ImgUtils.scale(path,path, targetWidth, targetHeight, true);//等比例缩放  输出缩放图片
	
	        /*File newfile = new File(path);   
	        
	        BufferedImage bufferedimage=ImageIO.read(newfile);
	        int width = bufferedimage.getWidth();
	        int height = bufferedimage.getHeight();
	        // 目标将图片裁剪成 目标宽高
	        if (width > targetWidth) {
	            bufferedimage=ImgUtils.cropImage(bufferedimage,(int) ((width - targetWidth) / 2),0,(int) (width - (width-targetWidth) / 2),(int) (height));
	            if (height > targetHeight) {
	                bufferedimage=ImgUtils.cropImage(bufferedimage,0,(int) ((height - targetHeight) / 2),targetWidth,(int) (height - (height - targetHeight) / 2) );
	            }
	        }else{
	            if (height > targetHeight) {
	                bufferedimage=ImgUtils.cropImage(bufferedimage,0,(int) ((height - targetHeight) / 2),(int) (width),(int) (height - (height - targetHeight) / 2) );
	            }
	        }
	        
	        // 输出裁剪图片
	        ImageIO.write(bufferedimage, "jpg", new File(path));  */  
	        
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
    public static void main(String[] args)
	{
    	corpImg("E://photo/1-20180909175355527.jpg", 960, 645);
	}

    /**
     * 保存barcode
     * @param text
     */
	public static void saveBarcode(String path, String text)
	{
		try
		{
			FileWriter writer = new FileWriter(path, true);
			writer.write(text+"\n\n");
	        writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	 /**
     * 保存barcodeList
     * @param text
     */
	public static void saveBarcodeList(String path, String text)
	{
		try
		{
			String barcode = text.replace("\n", ",").trim();
	    	if(barcode.substring(barcode.length()-1).equals(",")){
	    		barcode = barcode.substring(0, barcode.length()-1);
	    	}
	    	
			FileWriter writer = new FileWriter(path, true);
			writer.write(barcode+"\n\n");
	        writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
