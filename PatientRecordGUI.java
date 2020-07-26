/**
 * Question 2 
 * This class contains most of  the GUI implementation required to display all the requirement. The rest can be found 
 * in the PatientsApplicationRunner which contains the main method. Including a button option to
 * build an XML document 
 * 
 ***/
package patientsGUI;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/****
 * 
 * @author Patricia Virgen ID17999166
 */

public class PatientRecordGUI extends JPanel implements ListSelectionListener { // used interface for the JList

    //Necessary attributes
    private JPanel displayPanel;
    private JPanel listPanel;
    private JPanel buttonPanel;
    private JSplitPane splitPane;
    private JList nhiNumbersList;
    private PatientRecords records;
    private DefaultListModel listModel;
    private XmlNodeFactory nodeFactory;
    private JFileChooser fileChooser;

    //required buttons 
    private JButton addButton = new JButton("Add Patient");
    private JButton removeButton = new JButton("Remove Patient");
    private JButton loadImageButton = new JButton("Load Image");
    private JButton loadXMLButton = new JButton("Load XML");
    private JButton saveXMLButton = new JButton("Save XML");
    private JButton extractToXMl = new JButton("Extract to XML");
    
    /*****
     * Only constructor that initializes the class attributes. 
     * It calls some methods & throws the required exceptions 
     * @param panel
     * @throws ParserConfigurationException 
     */
    public PatientRecordGUI(JPanel panel) throws ParserConfigurationException { 
        this.displayPanel = panel;
        buttonPanel = new JPanel();
        this.displayPanel.setPreferredSize(new Dimension(600, 600)); //goes to the right side
        this.listModel = new DefaultListModel();
        this.records = new PatientRecords();
        listPanel = new JPanel();
        listPanel.setPreferredSize(new Dimension(300, 600));
        nhiNumbersList = new JList(listModel);
        nhiNumbersList.setPreferredSize(new Dimension(300, 600));
        listPanel.add(nhiNumbersList);
        splitPane = new JSplitPane(SwingConstants.VERTICAL, listPanel, displayPanel); //this is where the JList gets displayed 
        this.add(splitPane);
        nhiNumbersList.addListSelectionListener(this);
        nodeFactory = new XmlNodeFactory("yesterdaysPatients.xml");
        fileChooser = new JFileChooser();
        initializeButtonPanel();
    }

    /****
     * This method gets the buttonPanel which gets called in the PatientsApplicationRunner class
     * @return the panel containing the buttons 
     */
    public JPanel getButtonPanel() {
        return buttonPanel;
    }

    /****
     * This private method adds the buttons to its assigned panel while
     * handling their own actionListeners implemented below 
     * @throws ParserConfigurationException 
     */
    private void initializeButtonPanel() throws ParserConfigurationException {
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(loadImageButton);
        buttonPanel.add(loadXMLButton);
        buttonPanel.add(saveXMLButton);
        buttonPanel.add(extractToXMl);
        handleLoadXMLButton();
        handleAddButton();
        handleSaveButton();
        handleLoadImageButton();
        handleRemovePatientButton();
        handleExtractToXMLButton();
    }
    
    /****
     * The job of this method is check if the user has entered the correct file name
     * into the texField. It must begin with NHI and have at least one digit number after that.
     * @param fileName that is an xml file 
     * @return true if the file is valid 
     */
    private boolean checkIfValidXMLFileName(String fileName) {
        if (fileName.isEmpty() || fileName.length() <= 4) {
            return false;
        }
        String lastFourSymbols = fileName.substring(fileName.length() - 4, fileName.length());
        if (lastFourSymbols.equals(".xml")) {
            return true;
        }
        return false;
    }


    /****
     * The job of this method is to respond to the user's input so that a new XML document can 
     * be created. It handles the correct input by checking a the conditions for an xml file, and calls
     * helper methods. 
     */
    private void handleExtractToXMLButton() {
        extractToXMl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = JOptionPane.showInputDialog(new JFrame(), "Enter the XML file name: ");
                if (!fileName.isEmpty() && checkIfValidXMLFileName(fileName)) {
                    XmlNodeFactory factory = new XmlNodeFactory(fileName); // just adding if everything is correct

                    try {
                        factory.createNewXMLDocument(); // creating a new XML document

                        for (Patient patient : records.getPatientRecords().values()) { //getting the values from the map (records)
                            factory.addXMLNode(patient);
                        }
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(new JFrame(), "ERROR: " + exception.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "ERROR: invalid file name!"); // just tell the user
                }
            }
        });
    }

    
    /*****
     * This method responds to the remove button been selected. 
     * It does this by removing a patient form both the JList and the records. When 
     * this happens an empty panel gets displayed by default.
     */
    public void handleRemovePatientButton() {
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

               if(records.getPatientRecords().size()!=0){ //checking that there is something to actually be removed 
                   String number = nhiNumbersList.getSelectedValue().toString();
                   Patient patient = records.getPatient(number); //getting the patient with that number (NHI)
                   
                
                   listModel.remove(nhiNumbersList.getSelectedIndex()); //removing from the displayed list
                   records.removePatient(number);//removing from the records

                   JPanel defaultPanel = new JPanel();
                   defaultPanel.setSize(900, 600);
                   defaultPanel.add(new JLabel("Nothing is selected!")); 

                   splitPane.remove(displayPanel);
                   displayPanel = defaultPanel; 
                   splitPane.add(displayPanel);//after the patient has been removed - display the empty panel 
               }
               else{
                   JOptionPane.showMessageDialog(new JFrame(), "No patient selected!"); //tell the user he/she hasn't selected anything
               }


            }
        });
    }

    
    /****
     * This method handles the SaveButton performance. .
     * It catches the appropriated exceptions and prints them accordingly.
     * 
     */
    private void handleSaveButton() {
        saveXMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    if (listModel.size() == 0) {// in case there is nothing in the list yet
                        JOptionPane.showMessageDialog(new JFrame(), "Records are empty, nothing to save!"); // tell the user 
                    } else {
                        nodeFactory.removeChildNodes();
                        for (Patient patient : records.getPatientRecords().values()) {
                            nodeFactory.addXMLNode(patient);
                        }
                    }
                 // Catching the possible exceptions
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ParserConfigurationException e1) {
                    e1.printStackTrace();
                } catch (TransformerException e1) {
                    e1.printStackTrace();
                } catch (org.xml.sax.SAXException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    
    /***
     * This method allows the user to add an image corresponding to a particular
     * patient by allowing it to choose it from a file. 
     */
    private void handleLoadImageButton() {
        loadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (nhiNumbersList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(new JFrame(), "No patient selected!");// just in case the button is clicked and nothing is there yet
                } else {
                    int returnVal = fileChooser.showOpenDialog(PatientRecordGUI.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        String number = nhiNumbersList.getSelectedValue().toString();
                        Patient patient = records.getPatient(number); //liking the image to the corresponding paitent on records
                        patient.loadImage(file.getAbsolutePath());//getting the path of the image
                        //This is where a real application would open the file.

                    }
                }

            }
        });
    }

    
    /****
     * The job of this method is to handle the addButton button. It does so by 
     * prompting a window that allows the user to enter the NHI number, or to cancel the 
     * action if needed. It also calls other methods to assist with the NHI validation. 
     */
    private void handleAddButton() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame addUserFrame = new JFrame("Add new Patient");
                addUserFrame.setLocation(500, 500);
                addUserFrame.setSize(600, 200);
                JPanel content = new JPanel(new GridBagLayout());

                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;//set the x location of the grid for the next component
                c.gridy = 0;//set the y location of the grid for the next component

                JLabel label = new JLabel("Enter the NHI Number: ");
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);
                content.add(label, c);
                JTextField nhiNumberField = new JTextField();
                nhiNumberField.setPreferredSize(new Dimension(200, 24));
                nhiNumberField.setHorizontalAlignment(JLabel.CENTER);
                c.gridy = 3;
                content.add(nhiNumberField, c);
                JButton buttonAdd = new JButton("Add");
                JButton buttonCancel = new JButton("Cancel");
                JPanel butonPanel = new JPanel(new FlowLayout());
                butonPanel.add(buttonAdd);
                butonPanel.add(buttonCancel);
                c.gridy = 5;
                content.add(butonPanel, c);
                addUserFrame.getContentPane().add(content);
                addUserFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                addUserFrame.setVisible(true);
                handleCancelButtonAction(buttonCancel, addUserFrame);
                handleAddButtonAction(nhiNumberField, buttonAdd, addUserFrame);
            }
        });
    }

    /****
     * When the cancel button is selected this method is called and the window is 
     * disposed. 
     * @param button
     * @param frame 
     */
    private void handleCancelButtonAction(JButton button, JFrame frame) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
    }

    
    /****
     * This method gets the text entered by the user, checks if this is a valid NHI number, when this is true it 
     * creates a new Patient object and adds it onto the records, as well as in the listModel given it isn't there yet.
     * @param textField
     * @param addButton
     * @param frame 
     */
    private void handleAddButtonAction(JTextField textField, JButton addButton, JFrame frame) {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String number = textField.getText();
                
                if (checkIfValidNHINumber(number)) { // only added if correct
                    Patient newPatient = new Patient(number);
                    records.addPatient(newPatient);
                    if (!listModel.contains(number)) {
                        listModel.addElement(number);
                    }
                    frame.dispose(); // if the patient that is been added is already there , just dispose the window that adds the patient 
                } else { //anything else just mention that is not valid 
                    JOptionPane.showMessageDialog(new JFrame(),
                            "Not a valid NHI Number");
                }
            }
        });
    }


    /****
     * This method checks whether the passed string into its argument is on 
     * digits value only. It is used to validate the NHI number been added. 
     * @param word - a String  
     * @return true if that is the case. 
     */
    private boolean containsOnlyDigits(String word) {
        String regex = "[0-9]+";
        return word.matches(regex);
    }
    
    
    /*****
     * The job is this method is to check that the passed number is 
     * a valid NHI number. It first breaks the number into two parts: 
     * the first 3 letters must be NHI and there must be another 6 letters
     * of a digit value only
     * @param number
     * @return true if the number is valid and false otherwise. 
     */
    private boolean checkIfValidNHINumber(String number) {
        
        if (number.isEmpty()) { // in case of the passed number having nothing 
            return false;
        }
        else{
        String firstThreeLetters = number.substring(0, 3);
        String lastSixDigits = number.substring(3, number.length());
        if (firstThreeLetters.equalsIgnoreCase("NHI")
                && lastSixDigits.length() == 6
                && containsOnlyDigits(lastSixDigits)) {
            return true;
        }}
        return true;
    }
    
    
    /****
     * The job of this method is to load a given XML file, and to parse the document nodes
     * as well as adding the NHI numbers to the list. 
     * @throws ParserConfigurationException 
     */
    private void handleLoadXMLButton() throws ParserConfigurationException {
        loadXMLButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Document document = FileToDocumentConverter.getDocumentFromXML("yesterdaysPatients.xml");
                    records.parseDocumentNodes(document);
                    for (String number : records.getPatientNHIs()) {
                        if (!listModel.contains(number)) {// if is not already there
                            listModel.addElement(number);// then add it 
                        }
                    }

                    System.out.println(records.getPatientRecords().toString());
                }
                catch(Exception error){
                    JOptionPane.showMessageDialog(new JFrame(), error.getMessage());
                }
//                catch (ParserConfigurationException e1) {
//                    e1.printStackTrace();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                } catch (SAXException e1) {
//                    e1.printStackTrace();
//                }
            }
        });
    }

    
    /****
     * This private method sets a passed panel to the panel to be displayed.
     * @param displayPanel 
     */
    private void setDisplayPanel(JPanel displayPanel) {
        this.displayPanel = displayPanel;
    }

    
    /****
     * This method initializes the JList with the NHI's given a list of values contained on an array 
     * @param listValues 
     */
    private void initializeJList(String[] listValues) {
        nhiNumbersList = new JList(listValues);
    }

    /*****
     * The job of this method it to respond to the the change in the list when different 
     * patients are selected 
     * @param e 
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (nhiNumbersList.getSelectedIndex() == -1) { // in case if no nhi number was selected
               

            } else {//Selection - this is executed if something was selected
                String number = nhiNumbersList.getSelectedValue().toString();//getting a number 
                Patient patient = records.getPatient(number); //look up a patient from hashmap "records" based on the number selected
               
                // then we get the panel that is associated with that patient - with all info based on the
                // getDisplayPanel() method written by Seth
                JPanel rightPanelWithInfo = patient.getDisplayPanel();
                splitPane.remove(displayPanel); // removing the old panel
                displayPanel = rightPanelWithInfo; // then setting the display planel and redeclaring it to be equal to the one we got higher
                this.setDisplayPanel(displayPanel);//setting the panel back in
                splitPane.add(displayPanel, 1);  // and we add it to the pane
            }
        }
    }

}
