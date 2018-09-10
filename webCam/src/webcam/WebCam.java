package webcam;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;

public class WebCam {
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
        
        // 面板
        final JFrame window = new JFrame("图像采集");
        JPanel jp1 = new JPanel(new GridLayout(1, 1));
        JPanel jp2 = new JPanel(new GridLayout(1, 2));

        // 导航栏
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        ImageIcon exportIcon = new ImageIcon("pic/exportIcon.jpg");
        JMenuItem fileExport = new JMenuItem("导出Excel", exportIcon);
        fileExport.setMnemonic(KeyEvent.VK_E);
        fileExport.setToolTipText("导出Excel");
        fileExport.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		
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

        // 控件
        final JTextArea textarea = new JTextArea();
        final JScrollPane jsp = new JScrollPane(textarea);
        final JButton photoButton = new JButton("拍照");
        final JButton nextButton = new JButton("下一组");
        
        // 排版
        fileMenu.add(fileExport);
        fileMenu.add(fileClose);
        menuBar.add(fileMenu);
        
        jp1.add(textarea);
        jp2.add(photoButton);
        jp2.add(nextButton);
        JSplitPane jsplit = new JSplitPane(0, jp1, jsp);
        window.getContentPane().add(jsplit);
        
        
        // 创建一个垂直盒子容器, 把上面 3 个 JPanel 串起来作为内容面板添加到窗口
        Box vBox = Box.createVerticalBox();
        if(checkCamera){
        	vBox.add(camPanel);
        }	
        vBox.add(jp1);
        vBox.add(jp2);
        window.setContentPane(vBox);
        window.setJMenuBar(menuBar);
        
        // 设置窗口参数
        window.pack();
        window.setVisible(true);
        window.setLocation(Utils.getWindowCenterWidth(width), Utils.getWindowCenterHeight(height));
        
        // 拍照
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
                        num++;
                        return;
                    }
                });
            }
        });
        
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
            }
        });
        if(checkCamera){
        	System.out.println("相机打开成功");        	
        	modifyComponentSize(window);
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
	 * 控件自适应分辨率
	 * @param window
	 */
	private static void modifyComponentSize(JFrame window) {
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
			// TODO: handle exception
		}

	}


	public static void main(String[] args) throws InterruptedException {
		start();
		
	}
}
