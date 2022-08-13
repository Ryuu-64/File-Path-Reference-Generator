package pers.ryuu;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.awt.FlowLayout.CENTER;
import static pers.ryuu.FileReferenceGenerator.generate;

public class FileReferenceGeneratorGUI {
    public static void main(String[] args) {
        JPanel rootFilePathPanel = new JPanel();
        rootFilePathPanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JLabel rootFilePathLabel = new JLabel("Root file path :");
        rootFilePathPanel.add(rootFilePathLabel);
        JTextField rootFilePathTextField = new JTextField(30);
        rootFilePathPanel.add(rootFilePathTextField);

        JPanel referencePathPanel = new JPanel();
        referencePathPanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JLabel referencePathLabel = new JLabel("Reference path :");
        referencePathPanel.add(referencePathLabel);
        JTextField referencePathTextField = new JTextField(30);
        referencePathPanel.add(referencePathTextField);

        JPanel packageNamePanel = new JPanel();
        packageNamePanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JLabel packageNameLabel = new JLabel("Package name :");
        packageNamePanel.add(packageNameLabel);
        JTextField packageNameTextField = new JTextField(30);
        packageNamePanel.add(packageNameTextField);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JButton loginButton = new JButton("Generate");
        loginPanel.add(loginButton);

        JTextArea consoleTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(consoleTextArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        consoleTextArea.setLayout(new FlowLayout(CENTER, 8, 4));
        consoleTextArea.setEditable(false);

        JFrame frame = new JFrame("File Reference Generator");
        frame.setLayout(new GridBagLayout());
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagConstraints rootFilePathPanelConstraints = new GridBagConstraints();
        rootFilePathPanelConstraints.gridx = 0;
        rootFilePathPanelConstraints.gridy = 0;
        frame.add(rootFilePathPanel, rootFilePathPanelConstraints);
        GridBagConstraints referencePathPanelConstraints = new GridBagConstraints();
        referencePathPanelConstraints.gridx = 0;
        referencePathPanelConstraints.gridy = 1;
        frame.add(referencePathPanel, referencePathPanelConstraints);
        GridBagConstraints packageNamePanelConstraints = new GridBagConstraints();
        packageNamePanelConstraints.gridx = 0;
        packageNamePanelConstraints.gridy = 2;
        frame.add(packageNamePanel, packageNamePanelConstraints);
        GridBagConstraints loginPanelConstraints = new GridBagConstraints();
        loginPanelConstraints.gridx = 0;
        loginPanelConstraints.gridy = 3;
        frame.add(loginPanel, loginPanelConstraints);
        GridBagConstraints consoleTextAreaConstraints = new GridBagConstraints();
        consoleTextAreaConstraints.gridx = 0;
        consoleTextAreaConstraints.gridy = 4;
        consoleTextAreaConstraints.insets = new Insets(8, 16, 16, 16);
        frame.add(consoleTextArea, consoleTextAreaConstraints);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() {
            public synchronized void flush() {
                consoleTextArea.setText(toString());
                frame.pack();
            }
        };
        PrintStream printStream = new PrintStream(byteArrayOutputStream, true);

        loginButton.addActionListener(actionEvent -> {
            byteArrayOutputStream.reset();
            generate(rootFilePathTextField.getText(), referencePathTextField.getText(), packageNameTextField.getText());
        });

        System.setErr(printStream);
        System.setOut(printStream);
        frame.pack();
        frame.setVisible(true);
    }
}
