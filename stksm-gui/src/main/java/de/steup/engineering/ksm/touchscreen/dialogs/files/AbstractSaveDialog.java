/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.steup.engineering.ksm.touchscreen.dialogs.files;

import de.steup.engineering.ksm.touchscreen.dialogs.AlphaDialog;
import de.steup.engineering.ksm.touchscreen.dialogs.StringSetter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 *
 * @author christian
 */
public abstract class AbstractSaveDialog extends JDialog {

    private static final long serialVersionUID = 6140459098008820795L;

    private final PathConfig pathConfig;
    private final JLabel pathLabel;
    private final JPanel filesPanel;
    private final JButton overwButton;
    private final JButton deleteButton;
    private File currentPath = null;
    private File selectedFile = null;
    private final JButton delDirButton;
    private final String acceptedEnding = ".xml";

    public AbstractSaveDialog(Window owner, PathConfig pathConfig) {
        super(owner, pathConfig.getDesc() + " laden", ModalityType.APPLICATION_MODAL);
        this.pathConfig = pathConfig;

        super.setResizable(false);
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Container pane = super.getContentPane();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel pathPanel = new JPanel();
        pathLabel = new JLabel();
        pathPanel.add(pathLabel);

        mainPanel.add(pathPanel, BorderLayout.PAGE_START);

        filesPanel = new JPanel();

        BoxLayout layout = new BoxLayout(filesPanel, BoxLayout.Y_AXIS);
        filesPanel.setLayout(layout);

        mainPanel.add(new JScrollPane(filesPanel), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        GridLayout buttonLayout = new GridLayout(0, 6);
        buttonLayout.setHgap(10);
        buttonPanel.setLayout(buttonLayout);

        JButton newButton = new JButton("Neu");
        newButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final StringSetter setter = new StringSetter() {

                    @Override
                    public void setValue(String value) {
                        newFile(value);
                    }
                };
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        AlphaDialog.showDialog(AbstractSaveDialog.this, "Neue Datei anlegen", null, 1, 40, setter);
                    }
                });
            }
        });
        buttonPanel.add(newButton);

        overwButton = new JButton("Ersetzen");
        overwButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                overwriteFile();
            }
        });
        buttonPanel.add(overwButton);

        deleteButton = new JButton("Löschen");
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFile();
            }
        });
        buttonPanel.add(deleteButton);

        JButton newDirButton = new JButton("Verz Neu");
        newDirButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final StringSetter setter = new StringSetter() {

                    @Override
                    public void setValue(String value) {
                        newDir(value);
                    }
                };
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        AlphaDialog.showDialog(AbstractSaveDialog.this, "Neues Verzeichnis anlegen", null, 1, 40, setter);
                    }
                });
            }
        });
        buttonPanel.add(newDirButton);

        delDirButton = new JButton("Verz lösch");
        delDirButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                deleteDir();
            }
        });
        buttonPanel.add(delDirButton);

        JButton cancelButton = new JButton("Abbruch");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        pane.add(mainPanel);

        super.setSize(700, 700);

        super.setLocationRelativeTo(owner);

        loadDirectory(pathConfig.getLastPath());
    }

    private void loadDirectory(File path) {
        File root = pathConfig.getRoot();
        currentPath = path;
        selectedFile = null;
        overwButton.setEnabled(false);
        deleteButton.setEnabled(false);
        pathConfig.setLastPath(path);

        String rootPathStr = root.getAbsolutePath();
        String currentPathStr = path.getAbsolutePath();
        String relativePathStr = currentPathStr.substring(rootPathStr.length());

        if (relativePathStr.isEmpty()) {
            relativePathStr = File.separator;
        }

        pathLabel.setText(relativePathStr);

        filesPanel.removeAll();

        File dirs[] = path.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.isDirectory() && !file.isHidden();
            }
        });

        if (!path.equals(root)) {
            JButton dirButton = new JButton("< Verzeichnis höher");
            final File parent = path.getParentFile();
            dirButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    loadDirectory(parent);
                }
            });
            filesPanel.add(dirButton);
            delDirButton.setEnabled(true);
        } else {
            delDirButton.setEnabled(false);
        }

        for (final File dir : dirs) {

            JButton dirButton = new JButton("> " + dir.getName());
            dirButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    loadDirectory(dir);
                }
            });
            filesPanel.add(dirButton);
        }

        File files[] = path.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return !file.isDirectory() && !file.isHidden() && file.getName().endsWith(acceptedEnding);
            }
        });

        for (final File file : files) {
            String fn = file.getName();
            final JButton fileButton = new JButton(fn.substring(0, fn.length() - 4));
            final Color defaultColor = fileButton.getBackground();
            fileButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    for (Component comp : filesPanel.getComponents()) {
                        if (comp instanceof JButton) {
                            ((JButton) comp).setBackground(defaultColor);
                        }
                    }
                    fileButton.setBackground(Color.YELLOW);
                    selectedFile = file;
                    overwButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            });
            filesPanel.add(fileButton);
        }

        filesPanel.validate();
        filesPanel.repaint();
    }

    private void newFile(String name) {
        if (currentPath == null) {
            return;
        }
        saveFile(new File(currentPath, name + acceptedEnding));
        dispose();
    }

    private void overwriteFile() {
        if (selectedFile == null) {
            return;
        }
        if (selectedFile.exists()) {
            if (JOptionPane.showConfirmDialog(
                    this,
                    String.format("Soll die Datai %s wirklich überschrieben werden?", selectedFile.getName()),
                    "Sicherheitsabfrage",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }
        saveFile(selectedFile);
        dispose();
    }

    protected abstract void saveFile(File file);

    private void deleteFile() {
        if (selectedFile == null) {
            return;
        }

        if (JOptionPane.showConfirmDialog(
                this,
                String.format("Soll die Datai %s wirklich gelöscht werden?", selectedFile.getName()),
                "Sicherheitsabfrage",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        if (!selectedFile.delete()) {
            JOptionPane.showMessageDialog(
                    this,
                    String.format("Datei %s kann nicht gelöscht werden.", selectedFile.getName()),
                    "Faler bei Dateioperation",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadDirectory(currentPath);
    }

    private void newDir(String name) {
        if (currentPath == null) {
            return;
        }

        File newDir = new File(currentPath, name);
        if (!newDir.mkdir()) {
            JOptionPane.showMessageDialog(
                    this,
                    String.format("Verzeichnis %s kann nicht angelegt werden.", newDir.getName()),
                    "Faler bei Dateioperation",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadDirectory(currentPath);
    }

    private void deleteDir() {
        if (currentPath == null || currentPath.equals(pathConfig.getRoot())) {
            return;
        }

        if (JOptionPane.showConfirmDialog(
                this,
                String.format("Soll das Verzeichnis %s samt Inhalt wirklich gelöscht werden?", currentPath.getName()),
                "Sicherheitsabfrage",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        if (!deleteRecursive(currentPath)) {
            JOptionPane.showMessageDialog(
                    this,
                    String.format("Verzeichnis %s kann nicht gelöscht werden.", currentPath.getName()),
                    "Faler bei Dateioperation",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadDirectory(currentPath.getParentFile());
    }

    private boolean deleteRecursive(File path) {
        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                if (!deleteRecursive(file)) {
                    return false;
                }
            }
        }
        return path.delete();
    }
}
