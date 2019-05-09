package com.omb.ocpp.groovy;

import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import groovy.lang.GroovyClassLoader;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class GroovyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyService.class);
    private static final String LITHOS_HOME = Optional.ofNullable(System.getenv("LITHOS_HOME")).orElse("/home/bmterra/lithos");
    private static final Path SCRIPTS_FOLDER = Paths.get(LITHOS_HOME, "ocpp", "groovy");
    private static final boolean USE_SCRIPTS_FOLDER = true; //set to false to debug scripts from resources
    private final Map<Class<? extends Request>, ConfirmationSupplier<Request, Confirmation>> confirmationSuppliers =
            new HashMap<>();

    private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    public void loadGroovyScripts() {
        try {
            if (!SCRIPTS_FOLDER.toFile().exists()) {
                Files.createDirectories(SCRIPTS_FOLDER);
            }
            createGroovyFilesFromResources();
            loadConfirmationSuppliers();
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Could not load groovy scripts", e);
        }
    }

    private void createGroovyFilesFromResources() throws IOException, URISyntaxException {
        URL innerResourceFolderUrl = ClassLoader.getSystemClassLoader().getResource("groovy");
        if (innerResourceFolderUrl == null) {
            LOGGER.error("Could not find groovy scripts in resources");
            return;
        }

        // -------- __@      __@       __@       __@      __~@
        // ----- _`\<,_    _`\<,_    _`\<,_     _`\<,_    _`\<,_
        // ---- (*)/ (*)  (*)/ (*)  (*)/ (*)  (*)/ (*)  (*)/ (*)
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //FIXME ugly warkaround for problem with getting resources from jar
        final URI uri = Optional.of(innerResourceFolderUrl.toURI())
                .orElseThrow(() -> new IOException(String.format("Could not create URI to file %s", innerResourceFolderUrl)));
        if (uri.toString().contains("!")) {
            final String[] array = uri.toString().split("!");
            try (final FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), new HashMap<>());
                 Stream<Path> stream = Files.walk(fs.getPath(array[1]))) {
                stream.filter(path -> path.toString().endsWith("ConfirmationSupplier.groovy") && Files.isRegularFile(path))
                        .forEach(path -> createGroovyFile(path,
                                Paths.get(SCRIPTS_FOLDER.toString(), path.getFileName().toString())));
            }
        } else {
            try (Stream<Path> stream = Files.walk(Paths.get(uri))) {
                stream.filter(path -> path.toString().endsWith("ConfirmationSupplier.groovy") && Files.isRegularFile(path))
                        .forEach(path -> createGroovyFile(path,
                                Paths.get(SCRIPTS_FOLDER.toString(), path.getFileName().toString())));
            }
        }
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    private void createGroovyFile(Path source, Path destination) {
        if (!Files.exists(destination) && Files.isRegularFile(source)) {
            LOGGER.debug("Creating groovy file: {}", destination);
            try (ReadableByteChannel src = Channels.newChannel(Files.newInputStream(source));
                 FileChannel dest = new FileOutputStream(destination.toFile()).getChannel()) {
                dest.transferFrom(src, 0, Integer.MAX_VALUE);
                loadGroovyClass(source, destination);
            } catch (IOException e) {
                LOGGER.error(String.format("Could not create scripts file %s", source.getFileName()), e);
            }
        } else if (Files.isRegularFile(source)) {
            LOGGER.debug("File already exists {}", destination);
            loadGroovyClass(source, destination);
        }
    }

    private void loadGroovyClass(Path source, Path destination) {
        try {
            if (USE_SCRIPTS_FOLDER) {
                groovyClassLoader.parseClass(destination.toFile());
            } else {
                groovyClassLoader.parseClass(source.toFile());
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Could not load script %s", source.getFileName()), e);
        }
    }

    public synchronized void reloadGroovyFiles() {
        confirmationSuppliers.clear();
        groovyClassLoader.clearCache();

        if (!USE_SCRIPTS_FOLDER) {
            LOGGER.error("Reloading of groovy classes works only with USE_SCRIPTS_FOLDER = true, change it in " +
                    "GroovyService class");
            return;
        }

        try (Stream<Path> stream = Files.walk(SCRIPTS_FOLDER)) {
            stream.filter(path -> path.toString().endsWith("ConfirmationSupplier.groovy") && Files.isRegularFile(path))
                    .forEach(path -> {
                        try {
                            Class clazz = groovyClassLoader.parseClass(path.toFile());
                            LOGGER.info("{} - reloaded", clazz.getSimpleName());
                        } catch (IOException e) {
                            LOGGER.error(String.format("Could not load script %s", path.getFileName()), e);
                        }
                    });
        } catch (IOException e) {
            LOGGER.error(String.format("Can't walk path: %s", SCRIPTS_FOLDER), e);
        }

        loadConfirmationSuppliers();
    }

    private void loadConfirmationSuppliers() {
        Arrays.stream(groovyClassLoader.getLoadedClasses())
                .filter(aClass -> aClass.getGenericInterfaces().length != 0
                        && aClass.getGenericInterfaces()[0] instanceof ParameterizedType
                        && ((ParameterizedType) aClass.getGenericInterfaces()[0]).getRawType().equals(ConfirmationSupplier.class)
                )
                .forEach(this::putToCache);
    }

    public void uploadGroovyScript(InputStream inputStream, String scriptName) throws Exception {
        if (USE_SCRIPTS_FOLDER) {
            Path destination = Paths.get(SCRIPTS_FOLDER.toString(), scriptName);
            LOGGER.debug("Replacing file {}", destination);
            Files.deleteIfExists(destination);
            try (ReadableByteChannel src = Channels.newChannel(inputStream);
                 FileChannel dest = new FileOutputStream(destination.toFile()).getChannel()) {
                dest.transferFrom(src, 0, Integer.MAX_VALUE);
            }

            Class uploadedClass = groovyClassLoader.parseClass(destination.toFile());
            if (uploadedClass.getGenericInterfaces().length != 0 &&
                    uploadedClass.getGenericInterfaces()[0] instanceof ParameterizedType
                    && ((ParameterizedType) uploadedClass.getGenericInterfaces()[0]).getRawType().equals(ConfirmationSupplier.class)) {
                putToCache(uploadedClass);
            } else {
                throw new InvalidClassException(String.format("Could not load class from file %s, check that class implements " +
                        "ConfirmationSupplier<REQUEST extends Request, RESPONSE extends Confirmation>", destination));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void putToCache(Class aClass) {
        try {
            confirmationSuppliers.put((Class<? extends Request>) ((ParameterizedType) aClass.getGenericInterfaces()[0]).getActualTypeArguments()[0],
                    (ConfirmationSupplier) aClass.getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.error(String.format("Could not instantiate Confirmation supplier: %s", aClass), e);
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Confirmation> T getConfirmation(UUID sessionUuid, Request request) {
        try {
            return (T) Optional.ofNullable(confirmationSuppliers.get(request.getClass()))
                    .orElse((sessionUuid1, request1) -> null)
                    .getConfirmation(sessionUuid, request);
        } catch (Exception e) {
            LOGGER.error("Error in groovy confirmation supplier", e);
            return null;
        }
    }

}
