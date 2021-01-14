/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import de.steup.engineering.ksm.process.PersMainEnt;
import de.steup.engineering.ksm.plc.retain.RetainMain;
import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author christian
 */
public class RetainPersitanceTest {

    private File file;

    @Before
    public void setUp() throws IOException {
        this.file = File.createTempFile("persistance", "test");
    }

    /**
     * Shows the created file in pluma and deletes the file afterwards.
     *
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    @After
    public void tearDown() throws InterruptedException, IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec("pluma " + this.file.getAbsoluteFile());
        Thread.sleep(250); // the editor needs time to load the file.
        this.file.delete();
    }

    @Test
    public void test() {
        RetainMain process = new RetainMain();
        try {
            JAXBContext context = JAXBContext.newInstance(RetainMain.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(process, file);
        } catch (JAXBException ex) {
            ex.printStackTrace();
            fail("unable to save");
        }
    }
}
