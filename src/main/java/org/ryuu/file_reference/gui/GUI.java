package org.ryuu.file_reference.gui;

import org.ryuu.file_reference.core.Generator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.awt.FlowLayout.CENTER;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static org.ryuu.file_reference.gui.Persistence.*;

public class GUI {
    private static final Generator GENERATOR = new Generator();

    public static void main(String[] args) {
        JPanel rootFilePathPanel = new JPanel();
        rootFilePathPanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JLabel rootFilePathLabel = new JLabel("Root file path :");
        rootFilePathPanel.add(rootFilePathLabel);
        JTextField rootFilePathTextField = new JTextField(55);
        rootFilePathPanel.add(rootFilePathTextField);

        JPanel referencePathPanel = new JPanel();
        referencePathPanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JLabel referencePathLabel = new JLabel("Reference path :");
        referencePathPanel.add(referencePathLabel);
        JTextField referencePathTextField = new JTextField(55);
        referencePathPanel.add(referencePathTextField);

        JPanel packageNamePanel = new JPanel();
        packageNamePanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JLabel packageNameLabel = new JLabel("Package name :");
        packageNamePanel.add(packageNameLabel);
        JTextField packageNameTextField = new JTextField(55);
        packageNamePanel.add(packageNameTextField);

        JPanel scriptNamePanel = new JPanel();
        scriptNamePanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JLabel scriptNameLabel = new JLabel("Script name :");
        scriptNamePanel.add(scriptNameLabel);
        JTextField scriptNameTextField = new JTextField(55);
        scriptNamePanel.add(scriptNameTextField);

        JPanel generateButton = new JPanel();
        generateButton.setLayout(new FlowLayout(CENTER, 8, 4));
        JButton loginButton = new JButton("Generate");
        generateButton.add(loginButton);

        JPanel consolePanel = new JPanel();
        JTextArea consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(consoleTextArea, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        consolePanel.add(scrollPane);
        scrollPane.setMinimumSize(new Dimension(765, 200));
        scrollPane.setMaximumSize(new Dimension(765, 200));
        scrollPane.setPreferredSize(new Dimension(765, 200));

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

        GridBagConstraints scriptNamePanelConstraints = new GridBagConstraints();
        scriptNamePanelConstraints.gridx = 0;
        scriptNamePanelConstraints.gridy = 3;
        frame.add(scriptNamePanel, scriptNamePanelConstraints);

        GridBagConstraints loginPanelConstraints = new GridBagConstraints();
        loginPanelConstraints.gridx = 0;
        loginPanelConstraints.gridy = 4;
        frame.add(generateButton, loginPanelConstraints);

        GridBagConstraints consolePanelConstraints = new GridBagConstraints();
        consolePanelConstraints.gridx = 0;
        consolePanelConstraints.gridy = 5;
        consolePanelConstraints.insets = new Insets(4, 16, 16, 16);
        frame.add(consolePanel, consolePanelConstraints);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() {
            public synchronized void flush() {
                consoleTextArea.setText(toString());
                frame.pack();
            }
        };
        PrintStream printStream = new PrintStream(byteArrayOutputStream, true);
        System.setErr(printStream);
        System.setOut(printStream);

        rootFilePathTextField.setText(getRootFilePath());
        referencePathTextField.setText(getReferencePath());
        packageNameTextField.setText(getPackageName());
        scriptNameTextField.setText(getScriptName());
        loginButton.addActionListener(actionEvent -> {
            byteArrayOutputStream.reset();
            putRootFilePath(rootFilePathTextField.getText());
            putReferencePath(referencePathTextField.getText());
            putPackageName(packageNameTextField.getText());
            putScriptName(scriptNameTextField.getText());
            GENERATOR.generate(rootFilePathTextField.getText(), referencePathTextField.getText(), packageNameTextField.getText(), scriptNameTextField.getText());
        });

        GENERATOR.start.add(() -> System.out.println("generate start"));
        GENERATOR.over.add(() -> System.out.println("generate over"));
        referencePathTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                String packageName;
                packageName = tryGetPackageName(referencePathTextField.getText(), "com");
                if (packageName.equals("")) {
                    packageName = tryGetPackageName(referencePathTextField.getText(), "org");
                }

                packageNameTextField.setText(packageName);
            }
        });

        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static String tryGetPackageName(String referencePath, String packageNameStart) {
        if (referencePath.contains(packageNameStart)) {
            referencePath = referencePath.replace("\\", "/");
            referencePath = referencePath.replace("/", ".");

            int index = referencePath.lastIndexOf(packageNameStart);

            if (index != -1) {
                return referencePath.substring(index);
            }
        }
        return "";
    }
}