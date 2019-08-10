package ml.jordie.vatsimnotify.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Properties;

public class ConfigFile {

    public static ConfigFile instance;

    public ConfigFile() {
        instance = this;
        try {
            File file = new File("config.properties");
            if (!file.exists()) {
                file.createNewFile();

                Properties p = new Properties();
                p.setProperty("DISCORD_TOKEN", "SET-TOKEN-HERE");
                p.setProperty("TEXT_CHANNEL", "CHANNEL-ID-HERE");
                p.setProperty("FILTER_POSITIONS", "yes");
                p.setProperty("POSITIONS", "MEM,BNA,MQY,LIT,LRF");
                p.setProperty("POSITION_OPEN_NOTIFICATION", "%name% is opening %position.");
                p.setProperty("POSITION_CLOSED_NOTIFICATION", "%name% is closed %position.");
                p.setProperty("CLOSING_NOTIFICATIONS", "no");
                p.store(new FileOutputStream("config.properties"), "github.com/JordannDev/VatsimNotify");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    public static ConfigFile getInstance() {
        return instance;
    }

    /**
     * @description Get property from the config.properties file.
     * @param key The key of the property you want to get.
     * @return String
     */
    public String getProperty(String key) {
        try {
            Properties config = new Properties();
            config.load(new FileReader("config.properties"));
            if (doesPropertyExist(key))
                return config.getProperty(key);
            else
                return null;
        } catch (Exception ex) {
            System.err.println("Most of the time, you can manually add the new property to the file using: <PROPERTY NAME>=<VALUE>");
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * @description Checks if a property exists in the config.properties file.
     * @param key The key of the property you want to get.
     * @return true/false
     */
    public boolean doesPropertyExist(String key) {
        try {
            Properties config = new Properties();
            config.load(new FileReader("config.properties"));
            return config.containsKey(key);
        } catch (Exception ex) {
            System.err.println("Most of the time, you can manually add the new property to the file using: <PROPERTY NAME>=<VALUE>");
            ex.printStackTrace();
        }
        return false;
    }

}
