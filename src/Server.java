import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

public class Server {

	private HashMap<String, OutputStream> oss;
	
	public Server() {
		
		oss = new HashMap<String, OutputStream>();
		
		ServerSocket server = null;
		try {
			server = new ServerSocket(5678);
		} catch (IOException e) {
			print("端口被占用");
			return ;
		}
		
		accept(server);
	}
	
	private void accept(ServerSocket _server) {
		
		ServerSocket server = _server;
		
		while(true) {
			
			Socket client = null;
			
			try {
				
				client = server.accept();
				
				synchronized(oss) {
					oss.put(client.getInetAddress().toString(), client.getOutputStream());
				}
				
				String msg = "Server : " + client.getInetAddress() + "已登录";
				print(msg);
				sendMessage(msg);				
				
			} catch (IOException e) {
				print("发生位置错误!客户端连接失败");
				continue;
			}
			
			try {
				new Thread(new Listen(client)).start();
			} catch (IOException e) {
				print("客户端监听失败");
			}
			
			
		}
	}
	
	
	public class Listen implements Runnable {
		
		private Socket client;
		private InputStream is;
		private String clientName;
		
		public Listen(Socket client) throws IOException {
			this.client = client;
			this.clientName = client.getInetAddress().toString();
			this.is = this.client.getInputStream();
		}
		
		@Override
		public void run() {
			InputStreamReader isr = new InputStreamReader(is);
			while(true) {
				try {
					char[] chs = new char[1024];
					int r = isr.read(chs);
					String msg = new String(chs, 0, r);
					sendMessage(msg);
					
				} catch (IOException e) {
					print(clientName + " 已退出");
					oss.remove(this.clientName);
					return ;
				}
			}
		}
	}

	public void sendMessage(String str) throws IOException {
		synchronized(oss) {
			Iterator<String> it = oss.keySet().iterator();
			OutputStreamWriter osw = null;
			while(it.hasNext()) {
				osw = new OutputStreamWriter(oss.get(it.next()));
				osw.write(str);
				osw.flush();
			}
		}
	}
	
	public void print(String str) {
		System.out.println(str);
	}
	
	public static void main(String[] args) {
		new Server();
	}
	
}
