package de.steup.engineering.ksm.touchscreen.retain;

import de.steup.engineering.ksm.plc.retain.RetainMain;
import java.awt.Window;
import java.io.File;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author christian
 */
public class PersUtil {

    private final Log logger = LogFactory.getLog(PersUtil.class.toString());

    public void saveRetain(Window owner, RetainMain retainData, File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(RetainMain.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(retainData, file);
        } catch (Exception ex) {
            logger.error("Fehler beim speichern der Retaindaten", ex);
            JOptionPane.showMessageDialog(owner,
                    String.format("Datei %s kann nicht geschrieben werden.", file.getName()), "Fehler beim Speichern",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public RetainMain loadRetain(Window owner, File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(RetainMain.class);
            Unmarshaller u = context.createUnmarshaller();
            return (RetainMain) u.unmarshal(file);
        } catch (Exception ex) {
            logger.error("Fehler beim laden der Retaindaten", ex);
            JOptionPane.showMessageDialog(owner,
                    String.format("Datei %s kann nicht gelesen werden.", file.getName()),
                    "Fehler beim Laden",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
