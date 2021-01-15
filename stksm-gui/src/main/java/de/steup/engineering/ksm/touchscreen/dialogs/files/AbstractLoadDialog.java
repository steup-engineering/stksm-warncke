package de.steup.engineering.ksm.touchscreen.dialogs.files;

import java.awt.BorderLayout;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 *
 * @author christian
 */
public abstract class AbstractLoadDialog extends JDialog {

    private final PathConfig pathConfig;
    private final JLabel pathLabel;
    private final JPanel filesPanel;
    private final String acceptedEnding = ".xml";

    public AbstractLoadDialog(Window owner, PathConfig pathConfig) {
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

        GridLayout buttonLayout = new GridLayout(0, 1);
        buttonLayout.setHgap(10);
        buttonPanel.setLayout(buttonLayout);

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
        }

        for (final File dir : dirs) {

            JButton dirButton = new JButton("> " + dir.getName());
            dirButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (dir.canRead()) {
                        loadDirectory(dir);
                    } else {
                        // not allowed to read directory
                    }
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
            fileButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    loadFile(file);
                    dispose();
                }
            });
            filesPanel.add(fileButton);
        }

        filesPanel.validate();
        filesPanel.repaint();
    }

    protected abstract void loadFile(File file);
}
