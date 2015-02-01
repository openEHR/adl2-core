package org.openehr.adl.serializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bna on 01.02.2015.
 */
public abstract class CSerializer {


    private boolean debug = false;

    public CSerializer()
    {
        debug = false;
    }
    public CSerializer(boolean debug){
        this.debug = debug;
    }

    /**
     * Write the ADL to target/{file) to inspect manually and try open with Archetype Editor
     * @param adl the string representation of Archetype
     * @param filename the filename
     */
    protected void write(String adl, String filename) {
        if (debug) {
            try {
                System.out.println("adlResult");
                System.out.println(adl);
                FileWriter writer = new FileWriter(new File("target/" + filename));

                writer.write(adl);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
