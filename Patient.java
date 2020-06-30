package patientsGUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author DSA class
 */
public class Patient {

    private String name;
    private String nhiNumber;
    private char gender;
    private int age;
    private String address;
    private ImageIcon image;
    private String symptoms;
    private String treatment;
    private JPanel displayPanel;
    private String picURL;

    public Patient(String patientID) {
        this(patientID, null, 'X', 0, "");
    }

    public Patient(String nhiNumber, String name, char gender, int age, String address) {
        this.nhiNumber = nhiNumber;
        this.name = name;
        this.address = address;
        this.gender = Character.toUpperCase(gender);
        this.age = age;
        image = null;
        symptoms = "";
        treatment = "";
        picURL = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = Character.toUpperCase(gender);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNHI() {
        return nhiNumber;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public void loadImage(String picURL) {
        this.picURL = picURL;
        image = new ImageIcon(picURL);
        if (displayPanel != null)
            displayPanel.repaint();
    }

    public String getPicURL() {
        return picURL;
    }

    @Override
    public String toString() {

        return nhiNumber + "> " + "address: " + name + ", gender: " + gender
                + ", age: " + age + ", address: " + address + ", PIC: " + picURL+
                ", symptoms: ["+symptoms+"], treatment: ["+treatment+"]";
    }

    public JPanel getDisplayPanel() {
        if (displayPanel == null)
            displayPanel = new DisplayPanel();
        return displayPanel;
    }

    private class DisplayPanel extends JPanel implements ActionListener, DocumentListener {
        private JTextArea symptomsArea, treatmentArea;
        private JTextField nameField, addressField;
        private JTextField ageField;
        private JLabel patientIDLabel;
        private JRadioButton maleButton;
        private JRadioButton femaleButton;
        private JRadioButton otherButton;


        // Check with Seth if that's what he requires
        private boolean containsSymbols(String number) {
            String regex = "^[A-Za-z]+$";
            return number.matches(regex);
        }

        public DisplayPanel() {
            super(new GridLayout(2, 2));
            super.setPreferredSize(new Dimension(600, 600));

            JPanel infoPanel = new JPanel();

            //infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
            infoPanel.setLayout(new GridLayout(0, 1));
            patientIDLabel = new JLabel("NHI Number: " + nhiNumber, SwingConstants.CENTER);
            patientIDLabel.setFont(new Font("Comic Sans", Font.BOLD, 14));

            nameField = new JTextField(name, 20);
            nameField.getDocument().addDocumentListener(this);
            nameField.setBorder(BorderFactory.createTitledBorder("Patient Name"));

            ageField = new JTextField("" + age, 3);
            ageField.getDocument().addDocumentListener(this);
            maleButton = new JRadioButton("male");
            femaleButton = new JRadioButton("female");
            otherButton = new JRadioButton("other");

            if (gender == 'M')
                maleButton.setSelected(true);
            else if (gender == 'F')
                femaleButton.setSelected(true);
            else
                otherButton.setSelected(true);

            ButtonGroup group = new ButtonGroup();
            group.add(maleButton);
            group.add(femaleButton);
            group.add(otherButton);
            maleButton.addActionListener(this);
            femaleButton.addActionListener(this);
            otherButton.addActionListener(this);

            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createTitledBorder("gender/age"));
            panel.add(maleButton);
            panel.add(femaleButton);
            panel.add(otherButton);
            panel.add(ageField);


            addressField = new JTextField(address, 20);
            addressField.getDocument().addDocumentListener(this);
            addressField.setBorder(BorderFactory.createTitledBorder("Patient Address"));

            infoPanel.add(patientIDLabel);
            infoPanel.add(nameField);
            infoPanel.add(panel);
            infoPanel.add(addressField);
            infoPanel.add(Box.createVerticalGlue());

            symptomsArea = new JTextArea(symptoms, 10, 5);
            symptomsArea.getDocument().addDocumentListener(this);
            symptomsArea.setBorder(BorderFactory.createTitledBorder("Symptoms"));
            treatmentArea = new JTextArea(treatment, 10, 5);
            treatmentArea.getDocument().addDocumentListener(this);
            treatmentArea.setBorder(BorderFactory.createTitledBorder("Treatment"));

            JPanel southPanel = new JPanel();
            southPanel.setLayout(new GridLayout(1, 2));

            super.add(infoPanel, BorderLayout.EAST);
            super.add(new DrawPanel(), BorderLayout.CENTER);
            super.add(new JScrollPane(symptomsArea));
            super.add(new JScrollPane(treatmentArea));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (maleButton.isSelected())
                gender = 'M';
            else if (femaleButton.isSelected())
                gender = 'F';
            else
                gender = 'X';
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            if (e.getDocument() == symptomsArea.getDocument()) {
                symptoms = symptomsArea.getText();
            }
            if (e.getDocument() == treatmentArea.getDocument()) {
                treatment = treatmentArea.getText();
            }
            if (e.getDocument() == ageField.getDocument()) {
                try {
                    age = Integer.parseInt(ageField.getText());
                } catch (NumberFormatException nfe) {
                }
            }
            if (e.getDocument() == nameField.getDocument()) {
                name = nameField.getText();
//                if(containsSymbols(name)){
//                    name = nameField.getText();
//                }
//                else{
//                    JOptionPane.showMessageDialog(new JFrame(),
//                            "Not a valid name format!");
//                }
            }
            if (e.getDocument() == addressField.getDocument()) {
                address = addressField.getText();
            }
        }

        private class DrawPanel extends JPanel {
            public DrawPanel() {
                super();
                //super.setPreferredSize(new Dimension(200,200));
                super.setBackground(Color.WHITE);
            }

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    Image i = image.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
                    image.setImage(i);
                    image.paintIcon(this, g, 0, 0);
                } else
                    g.drawString("No Picture for " + name, getWidth() / 2 - 50, getHeight() / 2 - 50);
            }
        }
    }


}
