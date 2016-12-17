import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {

	private JFrame frmMain; 
	
	private JTextArea text;
	
	private JTextField textMsg;
	
	private JScrollPane scrollPane;
	
	private OutputStream os;
	
	private String name;
	
	public Client() {

		ui();
		
		Socket client = null;
		try {
			
			client = new Socket("139.199.39.220", 5678);
			os = client.getOutputStream();
			this.name = client.getInetAddress().toString();
		} catch (IOException e) {
			print("µÇÂ¼Ê§°Ü£¡");
			return ;
		}
		
		
		
		textMsg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				messageHandle(textMsg.getText());
			}
			
		});
		
		try {
			new Thread(new Listen(client)).start();
		} catch (IOException e1) {
			print("¼àÌýÊ§°Ü");
		}
		
	}
	
	private void ui() {
		frmMain = new JFrame();
		text = new JTextArea();
		textMsg = new JTextField();
		scrollPane = new JScrollPane(text);
		
		text.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		text.setEnabled(false);
		text.setFont(new Font("default", Font.ROMAN_BASELINE, 14));
		text.setForeground(Color.BLACK);
		text.setBackground(Color.WHITE);
		textMsg.setBounds(10, 340, 380, 20);
		scrollPane.setBounds(10, 10, 380, 320);
		//scrollPane.add(text);
		
		frmMain.setResizable(false);
		frmMain.setDefaultCloseOperation(3);
		frmMain.setLayout(null);
		frmMain.add(scrollPane);
		frmMain.add(textMsg);	
		frmMain.setBounds(600, 300, 400, 400);
		frmMain.setVisible(true);
	}
	
	public class Listen implements Runnable {
		
		private InputStream is;
		
		public Listen(Socket client) throws IOException {
			this.is = client.getInputStream();
		}
		
		@Override
		public void run() {
			
			InputStreamReader isr = new InputStreamReader(is);
			
			while(true) {
				char[] chs = new char[1024];
				try {
					
					int r = isr.read(chs);
					String msg = new String(chs, 0, r);
					print(msg);
					
				} catch (IOException e) {
					
					print("Ö÷»úÍË³ö");
					
				}
			}
		}
	}
	
	private void messageHandle(String msg) {
		String[] strs = msg.split(" ");
		
		if(strs[0].equals("setName")) {
			if(strs.length == 2) {
				this.name = strs[1];
			} else {
				print("ÃüÁî¸ñÊ½´íÎó");
			}
			textMsg.setText("");
			
		} else {
			
			OutputStreamWriter osw = new OutputStreamWriter(os);
			
			try {
				osw.write(this.name + " : " + msg);
				osw.flush();
			} catch (IOException e1) {
				print("·¢ËÍÊ§°Ü");
			}
			
			textMsg.setText("");
		}
	}
	
	private void print(String msg) {
		text.append(msg + "\r\n");
		text.setCaretPosition(text.getText().length());
	}
	
	public static void main(String[] args) {
		new Client();
	}
	
}
