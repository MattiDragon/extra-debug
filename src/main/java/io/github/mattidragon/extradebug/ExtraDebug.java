package io.github.mattidragon.extradebug;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class ExtraDebug implements ModInitializer {
    public static final String MOD_ID = "extra-debug";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final PrintStream ERROR_LOG;
    
    @Override
    public void onInitialize() {
    
    }
    
    static {
        PrintStream log;
    
        try {
            File file = FabricLoader.getInstance().getGameDir().resolve("logs/extra-debug.log").toFile();
            file.getParentFile().mkdirs();
            file.delete();
            file.createNewFile();
            log = new PrintStream(file);
        } catch (IOException e) {
            LOGGER.error("Can't log to file! Logging disabled.", e);
            log = new PrintStream(PrintStream.nullOutputStream());
        }
        
        ERROR_LOG = log;
    }
}
