package cz.wake.corgibot.utils.config;

import cz.wake.corgibot.utils.AnsiColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ConfigUtils {

    private final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);
    private Config config;

    /**
     * This will try to load the bot config and kill the program if it fails
     */
    public ConfigUtils() {
        try {
            logger.info(AnsiColor.BLUE.applyTo("ℹ info") + "     " + "Loading config.json");
            this.config = ConfigLoader.getConfig(new File("config.json"));
            logger.info(AnsiColor.GREEN.applyTo("✔ success") + "  " + "Configuration from config.json has been successfully loaded.");
        } catch (Exception e) {
            logger.info(AnsiColor.RED.applyTo("✖ fatal") + "    " + "Error during loading config.json");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * This will return the config that we have
     *
     * @return the config for the bot
     */
    public Config loadConfig() {
        return config;
    }
}
