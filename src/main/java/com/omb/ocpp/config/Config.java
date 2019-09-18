package com.omb.ocpp.config;

import com.omb.ocpp.gui.Application;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static com.omb.ocpp.gui.Application.OCPP_SERVER_HOME;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final Path PROPERTIES_PATH = Paths.get(OCPP_SERVER_HOME, "ocpp-server.properties").normalize();
    private static final String DEFAULT_VALUE_MESSAGE = "Could not get value for key %s, default value will be " +
            "returned %s";

    private Configuration config;
    private PropertiesConfigurationLayout layout;

    @PostConstruct
    private void init() {
        try {
            if (Files.notExists(PROPERTIES_PATH)) {
                LOGGER.debug("Creating configuration file: {}", PROPERTIES_PATH);
                Files.createFile(PROPERTIES_PATH);
            }
        } catch (IOException e) {
            LOGGER.error("Critical error during creation/loading of configuration file, application will be stopped", e);
        }

        layout = new PropertiesConfigurationLayout();

        Parameters params = new Parameters();
        PropertiesBuilderParameters propBuilder = params.properties();
        propBuilder.setLayout(layout);
        propBuilder.setFile(PROPERTIES_PATH.toFile());
        propBuilder.setEncoding("UTF-8");
        propBuilder.setListDelimiterHandler(new DefaultListDelimiterHandler(','));

        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(propBuilder);
        builder.setAutoSave(true);
        try {
            config = builder.getConfiguration();
            populateConfig();
        } catch (ConfigurationException cex) {
            LOGGER.error("Loading of the configuration file failed", cex);
        }
    }

    private void populateConfig() {
        Arrays.stream(ConfigKey.values())
                .forEach(configKey -> {
                    if (!config.containsKey(configKey.getKey())) {
                        setValue(configKey, configKey.getDefaultValue());
                    }
                });
    }

    public void setValue(ConfigKey configKey, Object value) {
        layout.setComment(configKey.getKey(), configKey.getComment());
        config.setProperty(configKey.getKey(), value);
    }

    public String getString(ConfigKey configKey) {
        try {
            return config.getString(configKey.getKey(), configKey.getDefaultValue().toString());
        } catch (ConversionException e) {
            LOGGER.error(String.format(DEFAULT_VALUE_MESSAGE, configKey.getKey(), configKey.getDefaultValue()), e);
            return configKey.getDefaultValue().toString();
        }
    }

    public int getInt(ConfigKey configKey) {
        try {
            return config.getInt(configKey.getKey(), (int) configKey.getDefaultValue());
        } catch (ConversionException e) {
            LOGGER.error(String.format(DEFAULT_VALUE_MESSAGE,
                    configKey.getKey(), configKey.getDefaultValue()), e);
            return (int) configKey.getDefaultValue();
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getStringCollection(ConfigKey configKey) {
        try {
            return config.getCollection(String.class, configKey.getKey(), new ArrayList<>(),
                    (ArrayList<String>) configKey.getDefaultValue());
        } catch (ConversionException e) {
            LOGGER.error(String.format(DEFAULT_VALUE_MESSAGE,
                    configKey.getKey(), configKey.getDefaultValue()), e);
            return (LinkedList<String>) configKey.getDefaultValue();
        }
    }

    public boolean getBoolean(ConfigKey configKey) {
        try {
            return config.getBoolean(configKey.getKey(), (boolean) configKey.getDefaultValue());
        } catch (ConversionException e) {
            LOGGER.error(String.format(DEFAULT_VALUE_MESSAGE,
                    configKey.getKey(), configKey.getDefaultValue()), e);
            return (boolean) configKey.getDefaultValue();
        }
    }
}