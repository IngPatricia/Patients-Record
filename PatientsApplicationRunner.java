/****
 * As part of Question 2 this class contains a main method which puts all the implementation together in order to display 
 * a graphical representation used to view multiple patients enrolled at general practitioners office
 */

package patientsGUI;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;

/****
 * 
 * @author Patricia 
 */
public class PatientsApplicationRunner {

    public static void main(String[] args) throws ParserConfigurationException {
        JFrame frame = new JFrame("Doctors Office");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel defaultPanel = new JPanel();
        defaultPanel.add(new JLabel("Nothing selected!")); // the default display panel when the application is first runned and nothing has been selected yet 
        defaultPanel.setSize(900, 600); //default size 

        PatientRecordGUI gui = new PatientRecordGUI(defaultPanel); 


        JPanel pa = new JPanel(new BorderLayout());
        pa.add(gui, BorderLayout.NORTH); //placing the records at the top of the panel 
        pa.add(gui.getButtonPanel(), BorderLayout.SOUTH); //adding the panel that contains the buttons at the lower part of the main panel 
        frame.getContentPane().add(pa); //adding everything to the frame

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        int screenHeight = dimension.height;
        int screenWidth = dimension.width;
        frame.pack(); //resize frame apropriately for its content
        
        //positions frame in center of screen
        frame.setLocation(new Point((screenWidth / 2) - (frame.getWidth() / 2), (screenHeight / 2) - (frame.getHeight() / 2)));       
        frame.setVisible(true); //making it visible 
    }


}
