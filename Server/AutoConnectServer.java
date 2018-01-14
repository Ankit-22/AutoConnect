import java.io.*;
import java.net.*;

public class AutoConnectServer {
	public static void main(String[] args) {
		String str = "";
		ServerSocket ss = null;
		Socket s = null;
		DataInputStream  dis = null;
		try {
			ss=new ServerSocket(6000);
			s=ss.accept();
			System.out.println("accepted a socket");
			dis=new DataInputStream(s.getInputStream());
		} catch (Exception e) {
			System.out.println(e);
		}
		while(!str.equals("close")) {
			try {
				if(dis.available() != 0) {
					System.out.println("Data Available");
					str=(String)dis.readUTF();
					System.out.println("message= "+str);
				}
				if(!s.getInetAddress().isReachable(1000)) {
					throw new EOFException("Done");
				}
			} catch(EOFException e) {
				System.out.println(e);
				try {
					s=ss.accept();
					System.out.println("accepted a socket");
					dis=new DataInputStream(s.getInputStream());
				} catch(Exception ex) {
					System.out.println(ex);
					break;
				}
			} catch(IOException e) {
				System.out.println(e);
			}
		}
	}
}
