package org.jboss.forge.addon.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.smallrye.config.PropertiesConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

public class ForgeConfigSourceProvider implements ConfigSourceProvider {

    private static final Logger log = Logger.getLogger(ForgeConfigSourceProvider.class.getName());

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
        List<ConfigSource> sources = new ArrayList<>(1);
        Path configProperties = OperatingSystemUtils.getUserForgeDir().toPath().resolve("config.properties");
        if (Files.exists(configProperties)) {
            try {
                sources.add(new PropertiesConfigSource(configProperties.toUri().toURL()));
            } catch (IOException e) {
                log.log(Level.WARNING, "Could not read "+configProperties);
            }
        }
        return sources;
    }
}
