import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.MouseInfo;

class MyRobot {
	private Robot robot;
	private int screenHeight;
	private int screenWidth;
	private double controller_height = 400.0;
	private double controller_width = 680.0;

	public MyRobot() throws Exception{
		try {
			robot = new Robot();
			Dimension screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
			screenHeight = screenResolution.height;
			screenWidth = screenResolution.width;
		} catch (AWTException e) {
			System.out.println(""+e);
			throw new Exception("Cannot Create Robot Object");
		}
	}

	public void leftClick() {
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	public void rightClick() {
		robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
	}

	public void scroll(int amount) {
		robot.mouseWheel(amount);
	}

	public void moveMouse(double controller_x, double controller_y) {
		int monitor_x = (int) ((controller_x * ((double)screenWidth)) / controller_width);
		int monitor_y = (int) ((controller_y * ((double)screenHeight)) / controller_height);
		Point mousePosition = MouseInfo.getPointerInfo().getLocation();
		robot.mouseMove((int)mousePosition.getX() + monitor_x, (int)mousePosition.getY() + monitor_y);
	}
}
