import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
import java.util.Scanner;
import java.util.InputMismatchException;

public class AutoConnectServer {

	private static ServerSocket ss = null;
	private static Socket s = null;
	private static DataInputStream  dis = null;
	private static DataOutputStream dos = null;
	private static MyRobot myRobot = null;
	private static Scanner sc = null;
	private static Date prevDate = new Date();;

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
					prevDate = new Date();
					System.out.println("Data Available");
					ExecutorService executor = Executors.newCachedThreadPool();
					Callable<Object> task = new Callable<Object>() {
						public Object call() {
							try {
								return (String)dis.readUTF();
							} catch (Exception e) {
								return null;
							}
						}
					};
					Future<Object> future = executor.submit(task);
					try{
						str = (String)future.get(1, TimeUnit.SECONDS);
					} catch (TimeoutException | InterruptedException | ExecutionException ex) {
						throw new SocketException();
					}
					if(str == null) throw new SocketException();
					System.out.println("message= "+str);
					sc = new Scanner(str);
					type = sc.next();
					if(type.equals("Click:")) {
						myRobot.leftClick();
					}
					else if(type.equals("Move:")) {
						myRobot.moveMouse(sc.nextDouble(), sc.nextDouble());
					}
					else if(type.equals("LeftClick:")) {
						if(sc.next().equals("Down"))
							myRobot.leftClickDown();
						else
							myRobot.leftClickUp();
					}
					else if(type.equals("RightClick:")) {
						if(sc.next().equals("Down"))
							myRobot.rightClickDown();
						else
							myRobot.rightClickUp();
					}
				} else {
					Date now = new Date();
					if(now.getTime() - prevDate.getTime() >= 3000) {
						prevDate = now;
						throw new SocketException();
					}
				}
			} catch(EOFException | SocketException | InputMismatchException  e) {
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
