import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.io.IOException;
import java.util.ArrayList;
public class lampserver {

    private static int PIN_NUMBER_RED = 0;
    private static int PIN_NUMBER_GREEN = 1;
    private static int PIN_NUMBER_BLUE = 2;

    private static int colors[] = { 0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0x00FFFF, 0xFF00FF, 0xFFFFFF, 0x9400D3 };

    private int map(int x, int in_min, int in_max, int out_min, int out_max) {
    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    /**
     * set color, for example: 0xde3f47
     * 
     * @param color
     *            0xde3f47
     */
    private void ledColorSet(int color) {
    int r_val, g_val, b_val;

    r_val = (color & 0xFF0000) >> 16; // get red value
    g_val = (color & 0x00FF00) >> 8; // get green value
    b_val = (color & 0x0000FF) >> 0; // get blue value

    r_val = map(r_val, 0, 255, 0, 100); // change a num(0~255) to 0~100
    g_val = map(g_val, 0, 255, 0, 100);
    b_val = map(b_val, 0, 255, 0, 100);

    SoftPwm.softPwmWrite(PIN_NUMBER_RED, 100 - r_val); // change duty cycle
    SoftPwm.softPwmWrite(PIN_NUMBER_GREEN, 100 - g_val);
    SoftPwm.softPwmWrite(PIN_NUMBER_BLUE, 100 - b_val);
    }

    private void init() {
    /** initialize the wiringPi library, this is needed for PWM */
    Gpio.wiringPiSetup();

    SoftPwm.softPwmCreate(PIN_NUMBER_RED, 0, 100);
    SoftPwm.softPwmCreate(PIN_NUMBER_GREEN, 0, 100);
    SoftPwm.softPwmCreate(PIN_NUMBER_BLUE, 0, 100);
    }

    /**
     * Main method called to change the LED colors
     * 
     * @throws Exception
     *             when Thread.sleep is interupted
     */
    private void play() {
       while (true) {
	    try {
            ServerSocket socket = new ServerSocket(1337);
            Socket client = socket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String data;
            while ((data = in.readLine()) != null) {
                System.out.println(data);
                switch (data) {
                    case "red;":
                        ledColorSet(colors[0]);
                        break;
                    case "blue;":
                        ledColorSet(colors[2]);
                        break;
                    case "yellow;":
                        ledColorSet(colors[3]);
                        break;
		case "green;":
			ledColorSet(colors[1]);
			break;
		case "cyan;":
			ledColorSet(colors[4]);
			break;
		case "pink;":
			ledColorSet(colors[5]);
			break;
		case "violet;":
			ledColorSet(colors[7]);
			break;
		default:
			System.out.println(data);

			if (data.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
				ledColorSet(Integer.parseInt(data.replaceFirst("#", ""), 16));
			}
			
                }
            }
        } catch (IOException ex) {
            
        }
       }
    }

    public static void main(String[] args) {
    try {

        lampserver rgb = new lampserver();

        /** initialize the boad and pins */
        rgb.init();

        /** start the color change */
        rgb.play();

    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}
