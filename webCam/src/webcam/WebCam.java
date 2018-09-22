package webcam;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Label;
import java.awt.Toolkit;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;

public class WebCam extends JFrame{


	private Logger logger=Logger.getLogger(this.getClass());

	private static int width = 640*3/2;
	private static int height = 400*3/2;
	
	JLabel imgLabel;
	Image image;
	
	public WebCam(){
		
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
        
        ImageIcon openIcon = new ImageIcon("pic/exportIcon.jpg");
        JMenuItem openFolder = new JMenuItem("打开目录", openIcon);
        openFolder.setMnemonic(KeyEvent.VK_O);
        openFolder.setMnemonic('O');// 设置快捷键
        openFolder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK));// 设置加速器
        
        
        openFolder.setToolTipText("打开目录");
        openFolder.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		try {
					String path = Utils.getPropertyValue(this.getClass(),"config.properties", "path");
					// 自动创建文件夹
	                File file=new File(path);
	        		if(!file.exists()){//如果文件夹不存在
	        			file.mkdir();//创建文件夹
	        		}
					
					java.awt.Desktop.getDesktop().open(new File(path));
				}catch(IOException e1){
					e1.printStackTrace();
				}
        	}
        });
        
        ImageIcon exportIcon = new ImageIcon("pic/exportIcon.jpg");
        JMenuItem fileExport = new JMenuItem("一键导出Excel", exportIcon);
        fileExport.setMnemonic(KeyEvent.VK_E);
        fileExport.setToolTipText("一键导出Excel");
        fileExport.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		exportExcel();
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

        // 右侧面板
        final JPanel phtotJp = new JPanel();
        
        // 控件
        final JTextArea textarea = new JTextArea(10,10);
        textarea.setLineWrap(true);
        final JScrollPane jsp = new JScrollPane(textarea);//滚动条方法一
        //jsp.setViewportView(textarea);//滚动条方法二
        //jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);// 水平滚动条
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);// 垂直滚动条
        
        final JButton photoButton = new JButton("拍照");
        final JButton nextButton = new JButton("清空");
        final JLabel successLabel = new JLabel();
        final JLabel pathText = new JLabel();
        final JButton openImg = new JButton("查看");
        openImg.setVisible(false); //隐藏按钮的方法

        // 排版
        fileMenu.add(openFolder);
        fileMenu.add(fileExport);
        fileMenu.add(fileClose);
        menuBar.add(fileMenu);
        
        
        // 创建一个垂直盒子容器, 把上面 3 个 JPanel 串起来作为内容面板添加到窗口
        Box hBox = Box.createHorizontalBox();
        Box vBox1 = Box.createVerticalBox();
        Box vBox1_hBox = Box.createHorizontalBox();
        Box vBox2 = Box.createVerticalBox();
        Box vBox2_hBox1 = Box.createHorizontalBox();
        Box vBox2_hBox2 = Box.createHorizontalBox();

        if(checkCamera){
        	vBox1.add(camPanel);
        }	
        
        // 两个按钮
        vBox1_hBox.add(Box.createHorizontalStrut(130));
        vBox1_hBox.add(photoButton);
        vBox1_hBox.add(Box.createHorizontalStrut(50));
        vBox1_hBox.add(nextButton);
        vBox1_hBox.add(Box.createHorizontalStrut(130));
        
        // 成功提示
        vBox2_hBox1.add(successLabel);
        
        // 文件路径
        vBox2_hBox2.add(pathText);
        vBox2_hBox2.add(Box.createHorizontalStrut(10));
        vBox2_hBox2.add(openImg);
        
        // 组装盒子
        vBox1.add(jsp);
        vBox1.add(vBox1_hBox);
        vBox2.add(vBox2_hBox1);
        vBox2.add(vBox2_hBox2);
        vBox2.add(phtotJp);
        
        // 左右移动splitPane分隔符
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane.setLeftComponent(vBox1);
        splitPane.setRightComponent(vBox2);
        
        hBox.add(splitPane);
        
        this.getContentPane().add(hBox);
        this.setJMenuBar(menuBar);
        
        // 设置窗口参数
        this.setTitle("图像采集");
        this.pack();
        this.setVisible(true);
        this.setSize(width, height);
        this.setLocation(Utils.getWindowCenterWidth(width), Utils.getWindowCenterHeight(height));


        // 拍照
        photoButton.setMnemonic(KeyEvent.VK_S);
        photoButton.setMnemonic(KeyEvent.VK_C);
        photoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {   
            	textarea.requestFocus();
            	photoButton.setEnabled(false);
            	
            	String textValue = textarea.getText();
            	if(textValue == null || "".equals(textValue)){
            		JOptionPane.showMessageDialog(null, "请先扫描商品条形码");
            		photoButton.setEnabled(true);
            		return;
            	}
            	
            	String path = Utils.getPropertyValue(this.getClass(),"config.properties", "path");
            	String imgWidth = Utils.getPropertyValue(this.getClass(),"config.properties", "imgWidth");
            	String imgHeight = Utils.getPropertyValue(this.getClass(),"config.properties", "imgHeight");
                String imgName = path + Utils.getBarcodeImgName(textarea.getText());
                String imgPath =  imgName + "." + ImageUtils.FORMAT_JPG.toLowerCase();
                
                // 自动创建文件夹
                File file=new File(path);
        		if(!file.exists()){//如果文件夹不存在
        			file.mkdir();//创建文件夹
        		}
        		
                // 生成图片
                WebcamUtils.capture(webcam, imgName, ImageUtils.FORMAT_JPG);
                
                // 裁剪图片
                Utils.corpImg(imgPath, Integer.parseInt(imgWidth), Integer.parseInt(imgHeight));
            	
                // 右侧展示成功提示和图片
                successLabel.setAlignmentX(Label.LEFT);
                successLabel.setText("成功： " + new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
                pathText.setText(imgPath);
                openImg.setVisible(true); // 展示按钮
                
                // 清空图片，重新加载
                phtotJp.removeAll();
                image = Toolkit.getDefaultToolkit().getImage(imgPath);
            	ImageIcon imageIcon = new ImageIcon(image.getScaledInstance(getWidth()/5*2, getHeight()/2-25, Image.SCALE_DEFAULT));
            	imgLabel = new JLabel();
    			imgLabel.setIcon(imageIcon);
    			imgLabel.setOpaque(false);
    			imgLabel.setBounds(0, 0, getWidth(), getHeight());
    			phtotJp.add(imgLabel, new Integer(-30001));
    			imgLabel.repaint();
        		
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run()
                    {
                    	String goodsTextPath = Utils.getPropertyValue(this.getClass(),"config.properties", "goodsTextPath");
                    	String goodsListTextPath = Utils.getPropertyValue(this.getClass(),"config.properties", "goodsListTextPath");
                    	
                    	// 保存barcode
                        Utils.saveBarcode(goodsTextPath, textarea.getText());
                        // 保存barcodeList
                        Utils.saveBarcodeList(goodsListTextPath, textarea.getText());
                        
                        photoButton.setEnabled(true);
                        return;
                    }
                });
            }
        });
        
        nextButton.setMnemonic(KeyEvent.VK_DELETE);
        nextButton.setMnemonic(KeyEvent.VK_D);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	textarea.setText("");
            	textarea.requestFocus();
            }
        });
        
        openImg.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String path = Utils.getPropertyValue(this.getClass(),"config.properties", "path");
					java.awt.Desktop.getDesktop().open(new File(path));
				}
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
        
        // 窗口关闭触发
        this.addWindowListener(new WindowAdapter() {
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
        	Utils.modifyComponentSize(this);
        }else{
        	JOptionPane.showMessageDialog(null, "请检查是否连接相机");
        	//System.exit(0);
        }

        // 监控窗口大小变化
        this.addComponentListener(new ComponentAdapter(){
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
	public static void main(String[] args) {
		new WebCam();
	}
}
