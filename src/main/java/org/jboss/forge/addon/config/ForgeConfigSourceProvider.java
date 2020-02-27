package org.jboss.forge.addon.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.smallrye.config.PropertiesConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

public class ForgeConfigSourceProvider implements ConfigSourceProvider {

    private static final Logger log = Logger.getLogger(ForgeConfigSourceProvider.class.getName());

    private static final String USER_CONFIG_PATH = "org.jboss.forge.addon.configuration.USER_CONFIG_PATH";
    private static final String CONFIGSOURCE_PROPERTIES_URL_KEY = "configsource.properties.url";

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
        List<ConfigSource> sources = new ArrayList<>(2);
        Path configProperties = getConfigPropertiesPath();
        if (Files.exists(configProperties)) {
            Properties props = new Properties();
            try (InputStream stream = Files.newInputStream(configProperties)) {
                props.load(stream);
            } catch (IOException e) {
                log.log(Level.WARNING, "Could not read " + configProperties, e);
            }
            sources.add(new PropertiesConfigSource(props, configProperties.toString()));
            String configSourcePropertiesUrl = props.getProperty(CONFIGSOURCE_PROPERTIES_URL_KEY);
            if (configSourcePropertiesUrl != null) {
                try {
                    sources.add(new PropertiesConfigSource(new URL(configSourcePropertiesUrl)));
                } catch (IOException e) {
                    log.log(Level.WARNING, "Could not read " + configSourcePropertiesUrl, e);
                }
            }
        }
        return sources;
    }


    private Path getConfigPropertiesPath() {
        String property = System.getProperty(USER_CONFIG_PATH);
        final File userConfigurationFile;
        if (property == null || property.isEmpty()) {
            userConfigurationFile = new File(OperatingSystemUtils.getUserForgeDir(), "config.properties");
        } else {
            userConfigurationFile = new File(property);
        }
        return userConfigurationFile.toPath();
    }
}