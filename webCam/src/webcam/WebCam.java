package webcam;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import webcam.DBUtils;
import webcam.Utils;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;

public class WebCam {
	private Logger logger=Logger.getLogger(this.getClass());

	private static int num = 0;
	private static int width = 640;
	private static int height = 480;
	
	public static void start(){
		
		System.out.println("相机初始化中....");
		boolean checkCamera = true;
		
		// 声明相机
        final Webcam webcam = Webcam.getDefault();
        WebcamPanel camPanel = null;
        try{
	        // 设置分辨率
	        Dimension photoSize = WebcamResolution.FHD.getSize();
	        webcam.setCustomViewSizes(photoSize);
	        webcam.setViewSize(photoSize);
	
	        // 定义窗口
	        Dimension windowSize = WebcamResolution.VGA.getSize();// 窗口尺寸
	        camPanel = new WebcamPanel(webcam,windowSize,true);
	        camPanel.setFPSDisplayed(true);
	        camPanel.setDisplayDebugInfo(false);
	        camPanel.setImageSizeDisplayed(true);
	        camPanel.setMirrored(true);
        }catch(Exception e){
        	System.err.println("error：没有找到相机，请连接....");
        	checkCamera = false;
        }
        

        // 导航栏
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        ImageIcon exportIcon = new ImageIcon("pic/exportIcon.jpg");
        JMenuItem fileExport = new JMenuItem("一键导出Excel", exportIcon);
        fileExport.setMnemonic(KeyEvent.VK_E);
        fileExport.setToolTipText("一键导出Excel");
        fileExport.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		new WebCam().exportExcel();
        	}
        });
        
        ImageIcon closeIcon = new ImageIcon("pic/close.jpg");
        JMenuItem fileClose = new JMenuItem("退出", closeIcon);
        fileClose.setMnemonic(KeyEvent.VK_C);
        fileClose.setToolTipText("退出");
        fileClose.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		System.exit(0);
        	}
        });

        // 面板
        final JFrame window = new JFrame("图像采集");
        JPanel jp1 = new JPanel(new GridLayout(1, 1));
        JPanel jp2 = new JPanel(new GridLayout(1, 2));
        JPanel jp3 = new JPanel();
        JPanel jp4 = new JPanel(new GridLayout(1, 1));
        
        // 控件
        final JTextArea textarea = new JTextArea();
        final JScrollPane jsp = new JScrollPane(textarea);//滚动条方法一
        //jsp.setViewportView(textarea);//滚动条方法二
        
        final JButton photoButton = new JButton("拍照");
        final JButton nextButton = new JButton("清空");
        JLabel successLabel = new JLabel();
        JLabel imgLabel = new JLabel();
        
        // 排版
        fileMenu.add(fileExport);
        fileMenu.add(fileClose);
        menuBar.add(fileMenu);
        
        jp1.add(jsp);
        jp2.add(photoButton);
        jp2.add(nextButton);
        jp3.add(successLabel);
        jp4.add(imgLabel);
        
       
        // 创建一个垂直盒子容器, 把上面 3 个 JPanel 串起来作为内容面板添加到窗口
        Box vBox = Box.createVerticalBox();
        if(checkCamera){
        	vBox.add(camPanel);
        }	
        //vBox.add(jp3);
        vBox.add(jp1);
        vBox.add(jp2);
        window.setContentPane(vBox);
        window.setJMenuBar(menuBar);
        // 设置窗口参数
        window.pack();
        window.setVisible(true);
        window.setLocation(Utils.getWindowCenterWidth(width), Utils.getWindowCenterHeight(height));



        // 拍照
        photoButton.setMnemonic(KeyEvent.VK_S);
        photoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	photoButton.setEnabled(false);
            	
            	String textValue = textarea.getText();
            	if(textValue == null || "".equals(textValue)){
            		JOptionPane.showMessageDialog(null, "请先扫描商品条形码");
            		photoButton.setEnabled(true);
            		return;
            	}
            	
            	String goodsTextPath = Utils.getPropertyValue(this.getClass(),"config.properties", "goodsTextPath");
            	String goodsListTextPath = Utils.getPropertyValue(this.getClass(),"config.properties", "goodsListTextPath");
            	String path = Utils.getPropertyValue(this.getClass(),"config.properties", "path");
            	String imgWidth = Utils.getPropertyValue(this.getClass(),"config.properties", "imgWidth");
            	String imgHeight = Utils.getPropertyValue(this.getClass(),"config.properties", "imgHeight");
                String fileName = path + num;
                
                // 生成图片
                WebcamUtils.capture(webcam, fileName, ImageUtils.FORMAT_JPG);
                
                // 图片重命名为国标码
                String newPath = Utils.updateImgName(path, textarea.getText());
                
                // 裁剪图片
                Utils.corpImg(newPath, Integer.parseInt(imgWidth), Integer.parseInt(imgHeight));
            	
                // 保存barcode
                Utils.saveBarcode(goodsTextPath, textarea.getText());
                
                // 保存barcodeList
                Utils.saveBarcodeList(goodsListTextPath, textarea.getText());
                
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run()
                    {
                        //JOptionPane.showMessageDialog(null, "截图成功");
                        photoButton.setEnabled(true);
                    	successLabel.setText("成功： " + new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
                        num++;
                        return;
                    }
                });
            }
        });
        
        nextButton.setMnemonic(KeyEvent.VK_DELETE);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	textarea.setText("");
            	textarea.requestFocus();
            	num = 0;
            }

        });
        
        // 窗口关闭触发
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
            	System.out.println("相机关闭");
            	if(webcam != null){
            		webcam.close();
            	}
                System.exit(0);
                try {
					DBUtils.closeResource(DBUtils.getConnection());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
            }
        });
        if(checkCamera){
        	System.out.println("相机打开成功");        	
        	Utils.modifyComponentSize(window);
        }else{
        	JOptionPane.showMessageDialog(null, "请检查是否连接相机");
        	//System.exit(0);
        }

        // 监控窗口大小变化
        window.addComponentListener(new ComponentAdapter(){
        	@Override public void componentResized(ComponentEvent e){
        		//modifyComponentSize(window);
        	}});
	}
	
	
	/**
	 * 导出excel
	 */
	protected void exportExcel() {
		try {
			// 1. 读取照片地址
	    	String path = Utils.getPropertyValue(this.getClass(),"config.properties", "path");
	    	List<String>  photoPathList = Utils.getPhotoPathListFromFile(path);
	    	
			// 2. 读取goodsList.txt，循环每一行，获取barcodes
	    	String goodsListTextPath = Utils.getPropertyValue(this.getClass(),"config.properties", "goodsListTextPath");
	    	List<String>  barcodesList = Utils.getLineListFromFile(goodsListTextPath);
	    	
	    	// 3. 根据barcode提取商品信息
	    	List<HashMap<String, String>> goodsInfoList = new ArrayList<HashMap<String, String>>();  
	    	for(String barcodes : barcodesList){
	    		HashMap<String, String> map = Utils.getGoodsInfoFromDB(barcodes);
	    		if(map!=null && map.isEmpty() == false){
	    			goodsInfoList.add(map);
	    		}
	    	}
	    	//DBUtils.closeResource(DBUtils.getConnection());
	    	
	    	// 4. 照片地址补充进商品信息
	    	String excelPhotoPath = Utils.getPropertyValue(this.getClass(),"config.properties", "excelPhotoPath");
	    	if(photoPathList.size() != goodsInfoList.size()){
				System.out.println("error:照片数量和文本记录条数不一致，导出失败！");
				return;
			}
			for(int i = 0;i<photoPathList.size();i++){
				HashMap<String, String> map  = goodsInfoList.get(i);
				map.put("path", excelPhotoPath + "\\" + photoPathList.get(i));
			}
			
			// 5. 组装数据生成excel
	    	String exportPath = Utils.getPropertyValue(this.getClass(), "config.properties", "exportPath");
	    	boolean exportFlag = Utils.exportGoodsExcel(exportPath, goodsInfoList);
		
	    	if(exportFlag){
	    		int result = JOptionPane.showConfirmDialog(null, "导出成功，是否打开文件所在目录？");
		    	if(result == 0){
					try {
						java.awt.Desktop.getDesktop().open(new File(path));
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
	    	}else{
	    		JOptionPane.showMessageDialog(null, "导出失败！");	    		
	    	}
	    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public static void main(String[] args) throws InterruptedException {
		start();
		
	}
}
