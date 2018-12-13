import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class OOPFinal extends JFrame implements ActionListener{
    private static final int FRAME_WIDTH = 285;
    private static final int FRAME_HEIGHT = 250;
    private static final int FRAME_X_ORIGIN = 150;
    private static final int FRAME_Y_ORIGIN = 250;

    private JButton calcButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton prevButton;
    private JButton nextButton;

    private JTextField inputName;
    private JTextField inputID;
    private JTextField inputMid;
    private JTextField inputFinal;
    private JTextField inputGrade;

    private JLabel labelName;
    private JLabel labelID;
    private JLabel labelMid;
    private JLabel labelFinal;
    private JLabel labelGrade;

    private JLabel noData;

    private JMenu fileMenu;
    private JMenu editMenu;

    private int data=0;
    private Student student;
    private ArrayList<Student> students;

    public static void main(String[] args) {
        OOPFinal oopFinal = new OOPFinal();
        oopFinal.setVisible(true);
    }

    public class Student implements Serializable {
        private String id;
        private String name;
        private int midScore;
        private int finalScore;

        public Student() {

        }

        public String getId() { return id; }
        public String getName() { return name; }
        public int getMidScore() { return midScore; }
        public int getFinalScore() { return finalScore; }

        public void setId(String id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setMidScore(int midScore) { this.midScore = midScore; }
        public void setFinalScore(int finalScore) { this.finalScore = finalScore; }
    }

    public OOPFinal() {
        Container contentPane;
        //set the frame properties
        setSize (FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setTitle ("Student Data");
        setLocation (FRAME_X_ORIGIN, FRAME_Y_ORIGIN);
        contentPane = getContentPane();
        contentPane.setLayout( null);

        labelName = new JLabel("Student Name");
        labelID =   new JLabel("Student ID");
        labelMid =  new JLabel("Mid Exam Score");
        labelFinal =  new JLabel("Final Exam Score");
        labelGrade =  new JLabel("Grade Score");

        labelName.setBounds(10, 30, 150, 20);
        labelID.setBounds(10, 10, 150, 20);
        labelMid.setBounds(10, 50, 150, 20);
        labelFinal.setBounds(10, 70, 150, 20);
        labelGrade.setBounds(10, 90, 150, 20);

        //set default value
        inputName = new JTextField("Default");
        inputID = new JTextField("000000");
        inputMid = new JTextField("0");
        inputFinal = new JTextField("0");
        inputGrade = new JTextField("F");
        inputGrade.setEnabled(false);

        inputName.setBounds(110, 30, 150, 20);
        inputID.setBounds(110, 10, 150, 20);
        inputMid.setBounds(110, 50, 150, 20);
        inputFinal.setBounds(110, 70, 150, 20);
        inputGrade.setBounds(110, 90, 75, 20);

        contentPane.add(labelName);
        contentPane.add(labelID);
        contentPane.add(labelMid);
        contentPane.add(labelFinal);
        contentPane.add(labelGrade);

        contentPane.add(inputName);
        contentPane.add(inputID);
        contentPane.add(inputMid);
        contentPane.add(inputFinal);
        contentPane.add(inputGrade);

        //create and place buttons on the frame
        calcButton = new JButton ("Calc");
        calcButton.setBounds(188, 90, 70, 20);

        addButton = new JButton ("Add Data");
        addButton.setBounds(10, 120, 123, 20);
        removeButton = new JButton ("Remove Data");
        removeButton.setBounds(135, 120, 123, 20);

        prevButton = new JButton ("Prev.");
        prevButton.setBounds(10, 160, 70, 20);
        nextButton = new JButton ("Next");
        nextButton.setBounds(188, 160, 70, 20);
        noData = new JLabel("000/000");
        noData.setBounds(110,160,70,20);

        contentPane.add(calcButton);
        contentPane.add(addButton);
        contentPane.add(removeButton);
        contentPane.add(prevButton);
        contentPane.add(nextButton);
        contentPane.add(noData);

        //register this frame as an action listener of the two buttons
        calcButton.addActionListener(this);
        addButton.addActionListener(this);
        removeButton.addActionListener(this);
        prevButton.addActionListener(this);
        nextButton.addActionListener(this);

        //create two menus and their menu items
        createFileMenu();
        createEditMenu();
        //and add them to the menu bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setColor(Color.WHITE, Color.BLACK);
        students = new ArrayList<>();

        //register 'Exit upon closing' as a default close operation
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createFileMenu( ) {
        JMenuItem item;
        fileMenu = new JMenu("File");
        item = new JMenuItem("New"); //New
        item.addActionListener(this);
        fileMenu.add(item);
        fileMenu.addSeparator(); //add a horizontal separator line
        item = new JMenuItem("Open"); //Open...
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Save"); //Save
        item.addActionListener(this);
        fileMenu.add(item);
        fileMenu.addSeparator(); //add a horizontal separator line
        item = new JMenuItem("Quit"); //Quit
        item.addActionListener(this);
        fileMenu.add(item);
    }

    private void createEditMenu() {
        JMenuItem item;
        editMenu = new JMenu("Edit");
        item = new JMenuItem("Black"); //Cut
        item.addActionListener(this);
        editMenu.add(item);
        item = new JMenuItem("White"); //Copy
        item.addActionListener(this);
        editMenu.add(item);
    }

    public void newFile() {
        int choice = JOptionPane.showConfirmDialog(null, "New page?",
                "New", JOptionPane.YES_NO_CANCEL_OPTION);

        if(choice == JOptionPane.OK_OPTION) {
            students.clear();

            data = 0;

            inputID.setText("");
            inputName.setText("");
            inputMid.setText("");
            inputFinal.setText("");
            inputGrade.setText("F");

            noData.setText("00" + data + "/00" + (students.size()));
        }
    }

    private void openFile(){
        JFileChooser fileChooser = new JFileChooser();
        int status = fileChooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            System.out.println("Open is clicked");
            try {
                File selectedFile = fileChooser.getSelectedFile();
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                students = (ArrayList<Student>)objectInputStream.readObject();

                data = students.size();

                inputID.setText(students.get(data-1).getId());
                inputName.setText(students.get(data-1).getName());
                inputMid.setText("" + students.get(data-1).getMidScore());
                inputFinal.setText("" + students.get(data-1).getFinalScore());

                noData.setText("00" + data + "/00" + (students.size()));

                objectInputStream.close();
            } catch (IOException e) {

            } catch (ClassNotFoundException e) {

            }
        } else { //== JFileChooser.CANCEL_OPTION
            System.out.println("Cancel is clicked");
        }
    }

    private void saveFile(){
        JFileChooser fileChooser = new JFileChooser();
        int status = fileChooser.showSaveDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            System.out.println("Save is clicked");
            try {
                File selectedFile = fileChooser.getSelectedFile();
                selectedFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(selectedFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(students);
                objectOutputStream.close();
            } catch (IOException e) {

            }
        } else { //== JFileChooser.CANCEL_OPTION
            System.out.println("Cancel is clicked");
        }
    }

    public void buttonAction(JButton clickedButton) {
        if(clickedButton.getText().equals("Add Data")) {
            student = new Student();
            student.setId(inputID.getText());
            student.setName(inputName.getText());
            student.setMidScore(Integer.parseInt(inputMid.getText()));
            student.setFinalScore(Integer.parseInt(inputFinal.getText()));

            if(students.size() == 0) {
                data = 1;
            }

            students.add(student);

            JOptionPane.showMessageDialog(null,
                    "ID: " + student.getId() + "\n" +
                            "Name: " + student.getName() + "\n" +
                            "Mid Score: " + student.getMidScore() + "\n"
                            + "Final Score: " + student.getFinalScore() + "\nAdded Success");

            noData.setText("00" + data + "/00" + (students.size()));

        } else if(clickedButton.getText().equals("Remove Data")) {
            int choice = JOptionPane.showConfirmDialog(null, "Delete this page?",
                    "Delete", JOptionPane.YES_NO_CANCEL_OPTION);
            if(choice == JOptionPane.OK_OPTION) {
                if(students.size() > 0) {
                    students.remove(data-1);
                    noData.setText("00" + data + "/00" + (students.size()));
                }
            }

        } else if(clickedButton.getText().equals("Prev.")) {
            if(data > 1) {
                student = new Student();
                student = students.get((--data)-1);

                System.out.println(data);

                inputID.setText(student.getId());
                inputName.setText(student.getName());
                inputMid.setText("" + student.getMidScore());
                inputFinal.setText("" + student.getFinalScore());

                noData.setText("00" + data + "/00" + (students.size()));
            }

        } else if(clickedButton.getText().equals("Next")) {
            if(students.size() > data) {
                student = new Student();
                student = students.get(data++);

                System.out.println(data);

                inputID.setText(student.getId());
                inputName.setText(student.getName());
                inputMid.setText("" + student.getMidScore());
                inputFinal.setText("" + student.getFinalScore());

                noData.setText("00" + data + "/00" + (students.size()));
            }
        } else if(clickedButton.getText().equals("Calc")) {
            if(students.size() > 0) {
                double totalScore = students.get(data-1).getMidScore()*0.4 +
                        students.get(data-1).getFinalScore()*0.6;
                if(totalScore > 90) {
                    inputGrade.setText("A");
                } else if(totalScore >= 80) {
                    inputGrade.setText("B");
                } else if(totalScore >= 70) {
                    inputGrade.setText("C");
                } else if(totalScore >= 60) {
                    inputGrade.setText("D");
                } else {
                    inputGrade.setText("F");
                }
            }
        } else {

        }
    }

    public void setColor(Color backGroundColor, Color foreGroundColor) {
        this.getContentPane().setBackground(backGroundColor);
        labelID.setForeground(foreGroundColor);
        labelName.setForeground(foreGroundColor);
        labelMid.setForeground(foreGroundColor);
        labelFinal.setForeground(foreGroundColor);
        labelGrade.setForeground(foreGroundColor);

        addButton.setBackground(backGroundColor);
        removeButton.setBackground(backGroundColor);
        prevButton.setBackground(backGroundColor);
        nextButton.setBackground(backGroundColor);
        calcButton.setBackground(backGroundColor);

        addButton.setForeground(foreGroundColor);
        removeButton.setForeground(foreGroundColor);
        prevButton.setForeground(foreGroundColor);
        nextButton.setForeground(foreGroundColor);
        calcButton.setForeground(foreGroundColor);

        inputID.setForeground(foreGroundColor);
        inputName.setForeground(foreGroundColor);
        inputMid.setForeground(foreGroundColor);
        inputFinal.setForeground(foreGroundColor);
        inputGrade.setForeground(foreGroundColor);

        inputID.setBackground(backGroundColor);
        inputName.setBackground(backGroundColor);
        inputMid.setBackground(backGroundColor);
        inputFinal.setBackground(backGroundColor);
        inputGrade.setBackground(backGroundColor);
    }

    public void actionPerformed(ActionEvent event) {
        if(event.getSource() instanceof JButton) {
            JButton clickedButton = (JButton)event.getSource();
            buttonAction(clickedButton);
        } else if(event.getActionCommand().equals("New")) {
            newFile();
        } else if(event.getActionCommand().equals("Open")) {
            openFile();
        } else if(event.getActionCommand().equals("Save")) {
            saveFile();
        } else if(event.getActionCommand().equals("Quit")) {
            System.exit(0);
        } else if(event.getActionCommand().equals("Black")) {
            setColor(Color.BLACK, Color.GREEN);
        } else if(event.getActionCommand().equals("White")) {
            setColor(Color.WHITE, Color.BLACK);
        }
    }
}
