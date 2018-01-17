import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

public class AutoConnectServer {

	private static ServerSocket ss = null;
	private static Socket s = null;
	private static DataInputStream  dis = null;
	private static DataOutputStream dos = null;
	private static MyRobot myRobot = null;
	private static Date prevDate = new Date();
	private static Scanner sc = null;

	static {
		try {
			ss = new ServerSocket(6000);
			s = ss.accept();
			System.out.println("accepted a socket");
			myRobot = new MyRobot();
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		String str = "";
		String type = "";

		while(true) {
			try {
				if(dis.available() != 0) {
					System.out.println("Data Available");
					str=(String)dis.readUTF();
					System.out.println("message= "+str);
					sc = new Scanner(str);
					type = sc.next();
					if(type.equals("Click:")) {
						myRobot.leftClick();
					}
					if(type.equals("Move:")) {
						myRobot.moveMouse(sc.nextDouble(), sc.nextDouble());
					}
				}
				Date now = new Date();
				if(now.getTime() - prevDate.getTime() >= 3000) {
					dos.writeUTF("HeartBeat");
					prevDate = now;
				}
			} catch(EOFException | SocketException e) {
				System.out.println(e);
				try {
					s.close();
					s=ss.accept();
					System.out.println("accepted a socket");
					dis = new DataInputStream(s.getInputStream());
					dos = new DataOutputStream(s.getOutputStream());
					System.out.println("Got InputStream");
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
