package org.ryuu.file_path_reference_generator.gui;

import org.ryuu.file_path_reference_generator.core.Generator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.awt.FlowLayout.CENTER;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static org.ryuu.file_path_reference_generator.gui.Persistence.*;

public class GUI {
    public static void main(String[] args) {
        JPanel rootDirectoryPathPanel = new JPanel();
        rootDirectoryPathPanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JLabel rootDirectoryPathLabel = new JLabel("Root directory path :");
        rootDirectoryPathPanel.add(rootDirectoryPathLabel);
        JTextField rootDirectoryPathTextField = new JTextField(55);
        rootDirectoryPathPanel.add(rootDirectoryPathTextField);

        JPanel referenceScriptPathPanel = new JPanel();
        referenceScriptPathPanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JLabel referenceScriptPathLabel = new JLabel("Reference Script Path :");
        referenceScriptPathPanel.add(referenceScriptPathLabel);
        JTextField referenceScriptPathTextField = new JTextField(55);
        referenceScriptPathPanel.add(referenceScriptPathTextField);

        JPanel packageNamePanel = new JPanel();
        packageNamePanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JLabel packageNameLabel = new JLabel("Package name :");
        packageNamePanel.add(packageNameLabel);
        JTextField packageNameTextField = new JTextField(55);
        packageNamePanel.add(packageNameTextField);

        JPanel referenceScriptNamePanel = new JPanel();
        referenceScriptNamePanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JLabel referenceScriptNameLabel = new JLabel("Reference script name :");
        referenceScriptNamePanel.add(referenceScriptNameLabel);
        JTextField referenceScriptNameTextField = new JTextField(55);
        referenceScriptNamePanel.add(referenceScriptNameTextField);

        JPanel generatePanel = new JPanel();
        generatePanel.setLayout(new FlowLayout(CENTER, 8, 4));
        JButton generateButton = new JButton("Generate");
        generatePanel.add(generateButton);

        JPanel consolePanel = new JPanel();
        JTextArea consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(consoleTextArea, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        consolePanel.add(scrollPane);
        scrollPane.setMinimumSize(new Dimension(765, 200));
        scrollPane.setMaximumSize(new Dimension(765, 200));
        scrollPane.setPreferredSize(new Dimension(765, 200));

        JFrame frame = new JFrame("File Path Reference Generator");
        frame.setLayout(new GridBagLayout());
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagConstraints rootFilePathPanelConstraints = new GridBagConstraints();
        rootFilePathPanelConstraints.gridx = 0;
        rootFilePathPanelConstraints.gridy = 0;
        frame.add(rootDirectoryPathPanel, rootFilePathPanelConstraints);

        GridBagConstraints referencePathPanelConstraints = new GridBagConstraints();
        referencePathPanelConstraints.gridx = 0;
        referencePathPanelConstraints.gridy = 1;
        frame.add(referenceScriptPathPanel, referencePathPanelConstraints);

        GridBagConstraints packageNamePanelConstraints = new GridBagConstraints();
        packageNamePanelConstraints.gridx = 0;
        packageNamePanelConstraints.gridy = 2;
        frame.add(packageNamePanel, packageNamePanelConstraints);

        GridBagConstraints scriptNamePanelConstraints = new GridBagConstraints();
        scriptNamePanelConstraints.gridx = 0;
        scriptNamePanelConstraints.gridy = 3;
        frame.add(referenceScriptNamePanel, scriptNamePanelConstraints);

        GridBagConstraints loginPanelConstraints = new GridBagConstraints();
        loginPanelConstraints.gridx = 0;
        loginPanelConstraints.gridy = 4;
        frame.add(generatePanel, loginPanelConstraints);

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

        rootDirectoryPathTextField.setText(getRootFilePath());
        referenceScriptPathTextField.setText(getReferencePath());
        packageNameTextField.setText(getPackageName());
        referenceScriptNameTextField.setText(getScriptName());

        generateButton.addActionListener(actionEvent -> {
            byteArrayOutputStream.reset();

            putRootFilePath(rootDirectoryPathTextField.getText());
            putReferencePath(referenceScriptPathTextField.getText());
            putPackageName(packageNameTextField.getText());
            putScriptName(referenceScriptNameTextField.getText());

            Generator.generate(rootDirectoryPathTextField.getText(), referenceScriptPathTextField.getText(), packageNameTextField.getText(), referenceScriptNameTextField.getText());
        });

        Generator.start.add(() -> System.out.println("generate start"));
        Generator.over.add(() -> System.out.println("generate over"));

        referenceScriptPathTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                String packageName;
                packageName = tryGetPackageName(referenceScriptPathTextField.getText(), "com");
                if (packageName.equals("")) {
                    packageName = tryGetPackageName(referenceScriptPathTextField.getText(), "org");
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