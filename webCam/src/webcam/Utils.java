package webcam;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.github.sarxos.webcam.util.ImageUtils;

public class Utils
{
	private Logger logger=Logger.getLogger(this.getClass());

	/**
	 * 获取当前屏幕宽
	 * @return
	 */
	public static int getScreenWidth() {
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    return screenSize.width;
	  }

	/**
	 * 获取当前屏幕高
	 * @return
	 */
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
	 * 获取国标码图片名
	 * @param path
	 * @param text
	 */
	public static String getBarcodeImgName(String text)
	{
		if(text.equals("")){
			return "";
		}
    	String barcode = text.replace("\n", "_").trim();
    	if(barcode.substring(barcode.length()-1).equals("_")){
    		barcode = barcode.substring(0, barcode.length()-1);
    	}
    	return barcode + "" + getCurrentDateStr("YYYYMMddHHmmssSSS");
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
	
    /**
     * 裁剪图片
     * @param path
     * @param targetWidth
     * @param targetHeight
     */
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
	 * 控件自适应分辨率
	 * @param window
	 */
	public static void modifyComponentSize(JFrame window) {
		int fraWidth = window.getWidth();//frame的宽
		int fraHeight = window.getHeight();//frame的高
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		float proportionW = screenWidth/fraWidth;
		float proportionH = screenHeight/fraHeight;
		
		try 
		{
			Component[] components = window.getRootPane().getContentPane().getComponents();
			for(Component co:components)
			{
				float locX = co.getX() * proportionW;
				float locY = co.getY() * proportionH;
				float width = co.getWidth() * proportionW;
				float height = co.getHeight() * proportionH;
				co.setLocation((int)locX, (int)locY);
				co.setSize((int)width, (int)height);
				int size = (int)(co.getFont().getSize() * proportionH);
				Font font = new Font(co.getFont().getFontName(), co.getFont().getStyle(), size);
				co.setFont(font);
			}
		} 
		catch (Exception e) 
		{
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

	/**
	 * 读取txt文件每一行，放入集合
	 * @param goodsListTextPath 
	 * @return
	 */
	public static List<String> getLineListFromFile(String path) {
		List<String> list = new ArrayList<String>();
		try {
	        FileInputStream fis = new FileInputStream(path);
	        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
	        BufferedReader br = new BufferedReader(isr);
	        String line = "";
	        while ((line = br.readLine()) != null) {
	            if ("".equals(line) == false) {
	                list.add(line);
	            }
	        }
	        br.close();
	        isr.close();
	        fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}

	/**
	 * 根据barcode提取商品信息
	 * @param barcodes
	 * @return
	 */
	public static HashMap<String, String> getGoodsInfoFromDB(String barcodes) {
		if(StringUtils.isBlank(barcodes)){
			return null;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		HashMap<String, String> map = new HashMap<String, String>();
		String newBarcodes = checkBarcode(barcodes);
		
		try {
			conn = DBUtils.getConnection();
			
			String[] barcodeArr = newBarcodes.split("_");
			HashMap<String, String> keyMap = new HashMap<String, String>();
			for(String key : barcodeArr){
				keyMap.put(key, "");
			}
			map.put("inputGoodCount", keyMap.size() + "");
			
			String query = "SELECT sBarcode,sGoodsName from posstores103.tposgoods where sBarcode in (";
			StringBuilder queryBuilder = new StringBuilder(query);
			for ( int i = 0; i < barcodeArr.length; i++) {
				queryBuilder.append( " '" + barcodeArr[i]);
				if (i != barcodeArr.length - 1){
					queryBuilder.append( "',");
				}
				if (i == barcodeArr.length - 1){
					queryBuilder.append( "'");
				}
			}
			queryBuilder.append( ")");
			ps = conn.prepareStatement(queryBuilder.toString());
			rs = ps.executeQuery();// executeQuery会返回结果的集合，否则返回空值
			int i = 1;
			while (rs.next()) {
				map.put("barcode" + i, rs.getString(1));
				map.put("goodsName" + i, rs.getString(2));
				i++;
				//System.out.println(rs.getString(1) + "\t" + rs.getString(2)+ "\t\t");// 入如果返回的是int类型可以用getInt()
			}
			map.put("outGoodCount", (i-1) + "");

			if(keyMap.size()!=i-1){
				System.out.println("DB中无此商品："+newBarcodes.replaceAll("_", ","));
			}
			
		} catch (SQLException e) {
			System.out.println("MySQL操作错误");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null && ps != null){
				try {
					DBUtils.closeResource(rs, ps);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	
	/**
	 * 读取照片地址
	 * @param path
	 * @return
	 */
	public static List<String> getPhotoPathListFromFile(String path) {
		List<File> fileList = getFileSort(path);
        List<String> pathList = new ArrayList<String>();
		int errCount = 0;
        for (File file : fileList) {
        	String fileAllName = file.getName();
        	String fileName = fileAllName.substring(0,fileAllName.lastIndexOf("."));
    		String fileType = fileAllName.substring(fileAllName.lastIndexOf("."));
    		if(".jpg".equals(fileType.toLowerCase()) == false && ".jpeg".equals(fileType.toLowerCase()) == false){
    			continue;
    		}
    		//String barcodes = fileAllName.substring(0,fileAllName.lastIndexOf("-"));
    		//String newFileAllName = barcodes + fileType;
    		
    		pathList.add(fileAllName);
    		//System.out.println(fileAllName);
        }
        System.out.println(errCount);
		return pathList;
	}
	
	private static String checkBarcode(String barcodes){
		String[] barcodeArr = barcodes.split("_");

		for(String barcode : barcodeArr){
			String newBarcode = "";
			if(barcode.length() != 13 && barcode.startsWith("6") == false){
				newBarcode = "6" + barcode ;
				//System.err.println(barcode + "===>>>>" + newBarcode);
				if(barcode.equals("6931958014143")){
					newBarcode = "6931958014099";
				}
				if(barcode.equals("066001114505")){
					newBarcode = "9066001114505";
				}
				if(barcode.equals("555104515291")){
					newBarcode = "9555104515291";
				}
			}else{
				if(barcode.length() != 13){
					if(barcode.equals("640110242032")){
						newBarcode = "7" + barcode ;
					}
					else 
					if(barcode.equals("66923644285036")){
						newBarcode = "6923644266066";
					}else{
						System.err.println(barcode);						
					}
				}
			}
			if(StringUtils.isNotBlank(newBarcode)){
				if(checkStandardBarcode(newBarcode)==false){
					System.err.println("补充条码，校验失败："+barcode + "===>>>>" + newBarcode);	
				}
				barcodes = barcodes.replace(barcode, newBarcode);
			}else{
				if(checkStandardBarcode(barcode) == false){
					System.err.println("原条码，校验失败："+barcode + "===>>>>" + newBarcode);	
				}
			}
			
		}
		return barcodes;
	}
	 /**
     * 获取目录下所有文件(按时间排序)
     * 
     * @param path
     * @return
     */
	public static List<File> getFileSort(String path) {
 
        List<File> list = getFiles(path, new ArrayList<File>());
 
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() > newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }
        return list;
    }
 
    /**
     * 
     * 获取目录下所有文件
     * 
     * @param realpath
     * @param files
     * @return
     */
    private static List<File> getFiles(String realpath, List<File> files) {
 
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    // 递归所有文件，但是目前不需要
                	//getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * 导出商品数据
     * @param photoPathList
     * @param goodsInfoList
     */
	public static boolean exportGoodsExcel(String filePath, List<HashMap<String, String>> goodsInfoList) {
		
		try {
			FileOutputStream fileOut = new FileOutputStream(filePath);//创建excel表格
			 
		 	Workbook wb = new HSSFWorkbook();//获取workbook
		    HSSFSheet sheet = (HSSFSheet) wb.createSheet("商品信息");// 生成一个表格
		     
		    sheet.setColumnWidth(0, 4000);
		    sheet.setColumnWidth(1, 4000);
		    sheet.setColumnWidth(2, 4000);
		    sheet.setColumnWidth(3, 4000);
		    sheet.setColumnWidth(4, 4000);
		    sheet.setColumnWidth(5, 4000);
		    sheet.setColumnWidth(6, 4000);
		    sheet.setColumnWidth(7, 4000);
		    sheet.setColumnWidth(8, 4000);
		    sheet.setColumnWidth(9, 4000);
		    sheet.setColumnWidth(10, 4000);
		    sheet.setColumnWidth(11, 4000);
		    sheet.setColumnWidth(12, 4000);
		    sheet.setColumnWidth(13, 4000);
		    sheet.setColumnWidth(14, 4000);
		    sheet.setColumnWidth(15, 4000);
		    sheet.setColumnWidth(16, 4000);
		    
		    HSSFRow row = sheet.createRow((short)0);//创建行并插入表头
		    row.setHeight((short)500);
		    row.createCell(0).setCellValue("图片地址");
		    row.createCell(1).setCellValue("code1");
		    row.createCell(2).setCellValue("商品名称1");
		    row.createCell(3).setCellValue("code2");
		    row.createCell(4).setCellValue("商品名称2");
		    row.createCell(5).setCellValue("code3");
		    row.createCell(6).setCellValue("商品名称3");
		    row.createCell(7).setCellValue("code4");
		    row.createCell(8).setCellValue("商品名称4");
		    row.createCell(9).setCellValue("code5");
		    row.createCell(10).setCellValue("商品名称5");
		    row.createCell(11).setCellValue("code6");
		    row.createCell(12).setCellValue("商品名称6");
		    row.createCell(13).setCellValue("code7");
		    row.createCell(14).setCellValue("商品名称7");
		    row.createCell(15).setCellValue("code8");
		    row.createCell(16).setCellValue("商品名称8");
		     
		    for(int i = 0; i < goodsInfoList.size(); i++){//循环插入数据
		    	HashMap<String, String> map = goodsInfoList.get(i); 
		        row = sheet.createRow(i+1);
		        row.createCell(0).setCellValue(map.get("path"));  
			    row.createCell(1).setCellValue(map.get("barcode1")== null ? "" : map.get("barcode1"));
			    row.createCell(2).setCellValue(map.get("goodsName1")== null ? "" : map.get("barcode1")== null ? "" : map.get("goodsName1") + "(" +  map.get("barcode1") + ")");
			    row.createCell(3).setCellValue(map.get("barcode2")== null ? "" : map.get("barcode2"));
			    row.createCell(4).setCellValue(map.get("goodsName2")== null ? "" : map.get("barcode2")== null ? "" : map.get("goodsName2") + "(" +  map.get("barcode2") + ")");
			    row.createCell(5).setCellValue(map.get("barcode3")== null ? "" : map.get("barcode3"));
			    row.createCell(6).setCellValue(map.get("goodsName3")== null ? "" : map.get("barcode3")== null ? "" : map.get("goodsName3") + "(" +  map.get("barcode3") + ")");
			    row.createCell(7).setCellValue(map.get("barcode4")== null ? "" : map.get("barcode4"));
			    row.createCell(8).setCellValue(map.get("goodsName4")== null ? "" : map.get("barcode4")== null ? "" : map.get("goodsName4") + "(" +  map.get("barcode4") + ")");
			    row.createCell(9).setCellValue(map.get("barcode5")== null ? "" : map.get("barcode5"));
			    row.createCell(10).setCellValue(map.get("goodsName5")== null ? "" : map.get("barcode5")== null ? "" : map.get("goodsName5") + "(" +  map.get("barcode5") + ")");
			    row.createCell(11).setCellValue(map.get("barcode6")== null ? "" : map.get("barcode6"));
			    row.createCell(12).setCellValue(map.get("goodsName6")== null ? "" : map.get("barcode6")== null ? "" : map.get("goodsName6") + "(" +  map.get("barcode6") + ")");
			    row.createCell(13).setCellValue(map.get("barcode7")== null ? "" : map.get("barcode7"));
			    row.createCell(14).setCellValue(map.get("goodsName7")== null ? "" : map.get("barcode7")== null ? "" : map.get("goodsName7") + "(" +  map.get("barcode7") + ")");
			    row.createCell(15).setCellValue(map.get("barcode8")== null ? "" : map.get("barcode8"));
			    row.createCell(16).setCellValue(map.get("goodsName8")== null ? "" : map.get("barcode8")== null ? "" : map.get("goodsName8") + "(" +  map.get("barcode8") + ")");
		    }
		    
		    wb.write(fileOut);
		    fileOut.close();
		    wb.close();
		    return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 校验条码
	 * @param barcode
	 * @return
	 */
	public static boolean checkStandardBarcode(String barcode){
		
		if(barcode.length()!=13 && barcode.length()!=18){
			return false;
		}
		int lastCharOfBarcode = Integer.parseInt(barcode.substring(barcode.length()-1));
	    
	    int codeJ=0; //奇位和
	    int codeO=0; //偶位和
	    int c=0;
	    
	    for(int i = 0; i<barcode.length()-1; i++){
	    	if(i==0){
	    		codeJ += (barcode.charAt(i)-'0');
	    	}else{
	    		if(i%2==0){
	    			codeJ += (barcode.charAt(i)-'0');
	    		}
	    		if(i%2==1){
	    			codeO += (barcode.charAt(i)-'0');
	    		}
	    	}
	    }
	    if(barcode.length()==13){
	    	c = (codeJ + codeO * 3) % 10;
	    }
	    if(barcode.length()==18){
	    	c = (codeO + codeJ * 3) % 10;
	    }
	    int cc = (10 - c) % 10;
	    return lastCharOfBarcode == cc;
	}

	/**
	 * 校验输入的条码
	 * @param text
	 */
	public static boolean validateInsertBarcode(String text)
	{
		String[] barcodeArr = text.split("\n");

		for(String barcode : barcodeArr) {
			if(!checkStandardBarcode(barcode)){
				return false;
			}
		}
		return true;
	}

}
