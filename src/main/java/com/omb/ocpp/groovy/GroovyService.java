package com.omb.ocpp.groovy;

import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.omb.ocpp.gui.Application.OCPP_SERVER_HOME;

@Service
public class GroovyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyService.class);
    private static final Path GROOVY_PROJECT_FOLDER = Paths.get(OCPP_SERVER_HOME, "groovy");
    private static final Path SUPPLIERS_FOLDER = Paths.get(GROOVY_PROJECT_FOLDER.toString(), "src", "main", "groovy", "com",
            "omb", "ocpp", "groovy", "supplier");

    private final Map<Class<? extends Request>, ConfirmationSupplier<Request, Confirmation>> confirmationSuppliers =
            new HashMap<>();
    private Consumer<Void> groovyCacheChangedListener = aVoid -> LOGGER.debug("No listeners attached");

    public void loadGroovyScripts() {
        try {
            if (!GROOVY_PROJECT_FOLDER.toFile().exists()) {
                throw new FileNotFoundException(String.format("Please execute \"git clone https://github" +
                        ".com/v-bodnar/GroovyOcppSupplier.git %s\"", GROOVY_PROJECT_FOLDER));
            }
            reloadGroovyFiles();
        } catch (IOException e) {
            LOGGER.error("Could not load groovy scripts", e);
        }
    }

    public synchronized void reloadGroovyFiles() {
        confirmationSuppliers.clear();

        List<Class> classes = new BatchGroovyClassLoader().parseClasses(getGroovyFiles());
        classes.stream()
                .filter(aClass -> aClass.getGenericInterfaces().length != 0
                        && aClass.getGenericInterfaces()[0] instanceof ParameterizedType
                        && ((ParameterizedType) aClass.getGenericInterfaces()[0]).getRawType().equals(ConfirmationSupplier.class))
                .forEach(this::putToCache);

        groovyCacheChangedListener.accept(null);
    }

    public void uploadGroovyScript(InputStream inputStream, String scriptName) throws Exception {
        Path destination = Paths.get(SUPPLIERS_FOLDER.toString(), scriptName);
        LOGGER.debug("Replacing file {}", destination);
        Files.deleteIfExists(destination);
        try (ReadableByteChannel src = Channels.newChannel(inputStream);
             FileChannel dest = new FileOutputStream(destination.toFile()).getChannel()) {
            dest.transferFrom(src, 0, Integer.MAX_VALUE);
        }
        reloadGroovyFiles();
    }

    private List<Path> getGroovyFiles() {
        try (Stream<Path> stream = Files.walk(GROOVY_PROJECT_FOLDER)) {
            return stream
                    .filter(path -> path.toString().endsWith(".groovy") && Files.isRegularFile(path))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error(String.format("Can't walk path: %s", GROOVY_PROJECT_FOLDER), e);
            return new LinkedList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private void putToCache(Class<ConfirmationSupplier> aClass) {
        try {
            confirmationSuppliers.put((Class<? extends Request>) ((ParameterizedType) aClass.getGenericInterfaces()[0]).getActualTypeArguments()[0],
                    aClass.getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.error(String.format("Could not instantiate Confirmation supplier: %s", aClass), e);
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Confirmation> T getConfirmation(UUID sessionUuid, Request request) {
        try {
            return (T) Optional.ofNullable(confirmationSuppliers.get(request.getClass()))
                    .orElse(new ConfirmationSupplier<>() {
                        @Override
                        public Confirmation getConfirmation(UUID sessionUuid, Request request) {
                            return null;
                        }

                        @Override
                        public Instant getClassLoadDate() {
                            return Instant.now();
                        }
                    })
                    .getConfirmation(sessionUuid, request);
        } catch (Exception e) {
            LOGGER.error("Error in groovy confirmation supplier", e);
            return null;
        }
    }

    public List<ConfirmationSupplier> getConfirmationSuppliers() {
        return new ArrayList<>(confirmationSuppliers.values());
    }

    public void setGroovyCacheChangedListener(Consumer<Void> groovyCacheChangedListener) {
        this.groovyCacheChangedListener = groovyCacheChangedListener;
    }

    class FeatureNotSupportedException extends Exception {
        public FeatureNotSupportedException(String message) {
            super(message);
        }

        public FeatureNotSupportedException(String message, Throwable cause) {
            super(message, cause);
        }

        public FeatureNotSupportedException(Throwable cause) {
            super(cause);
        }

        public FeatureNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
