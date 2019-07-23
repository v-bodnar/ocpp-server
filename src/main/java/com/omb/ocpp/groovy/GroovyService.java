package com.omb.ocpp.groovy;

import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import org.apache.tools.ant.util.FileUtils;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.tools.GroovyClass;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.omb.ocpp.gui.Application.LITHOS_HOME;

@Service
public class GroovyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyService.class);
    private static final Path SCRIPTS_FOLDER = Paths.get(LITHOS_HOME, "ocpp", "groovy","src","main","groovy","com",
            "omb","ocpp", "groovy");
    private static final Path SUPPLIERS_FOLDER = Paths.get(LITHOS_HOME, "ocpp", "groovy","src","main","groovy","com",
            "omb","ocpp", "groovy", "supplier");
    private static final Path GROOVY_PROJECT_FOLDER = Paths.get(LITHOS_HOME, "ocpp", "groovy");
    private static final String BUILD_GRADLE = "build.gradle";
    private static final String SETTINGS_GRADLE = "settings.gradle";
    private static final String CONFIRMATION_SUPPLIER_GROOVY_SUFFIX = "ConfirmationSupplier.groovy";
    private final Map<Class<? extends Request>, ConfirmationSupplier<Request, Confirmation>> confirmationSuppliers =
            new HashMap<>();
    private Consumer<Void> groovyCacheChangedListener = aVoid -> LOGGER.debug("No listeners attached");

    private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    public void loadGroovyScripts() {
        try {
            if (!SCRIPTS_FOLDER.toFile().exists()) {
                Files.createDirectories(SCRIPTS_FOLDER);

            }
            createGroovyFilesFromResources();
            reloadGroovyFiles();
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Could not load groovy scripts", e);
        }
    }

    private void createGroovyFilesFromResources() throws IOException, URISyntaxException {
        URL innerResourceFolderUrl = ClassLoader.getSystemClassLoader().getResource("groovy");
        URI gradleBuild =
                Optional.ofNullable(ClassLoader.getSystemClassLoader().getResource("groovy/" + BUILD_GRADLE))
                        .orElseThrow().toURI();
        URI settingsBuild =
                Optional.ofNullable(ClassLoader.getSystemClassLoader().getResource("groovy/" + SETTINGS_GRADLE))
                        .orElseThrow().toURI();

        if (innerResourceFolderUrl == null) {
            LOGGER.error("Could not find groovy scripts in resources");
            return;
        }

        LOGGER.debug("build.gradle path: {}", gradleBuild.toString());
        LOGGER.debug("settings.gradle path: {}", gradleBuild.toString());

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
                stream.filter(path -> (path.toString().endsWith(CONFIRMATION_SUPPLIER_GROOVY_SUFFIX) ||
                        path.toString().endsWith(BUILD_GRADLE)) && Files.isRegularFile(path))
                        .forEach(path -> createGroovyFile(path,
                                Paths.get(SUPPLIERS_FOLDER.toString(), path.getFileName().toString())));
            }
        } else {
            try (Stream<Path> stream = Files.walk(Paths.get(uri))) {
                stream.filter(path -> path.toString().endsWith(CONFIRMATION_SUPPLIER_GROOVY_SUFFIX) && Files.isRegularFile(path) )
                        .forEach(path -> createGroovyFile(path,
                                Paths.get(SUPPLIERS_FOLDER.toString(), path.getFileName().toString())));
            }
            createGroovyFile(Paths.get(gradleBuild), GROOVY_PROJECT_FOLDER.resolve(BUILD_GRADLE));
            createGroovyFile(Paths.get(settingsBuild), GROOVY_PROJECT_FOLDER.resolve(SETTINGS_GRADLE));
        }
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    private void createGroovyFile(Path source, Path destination) {
        if (!Files.exists(destination) && Files.isRegularFile(source)) {
            LOGGER.debug("Creating groovy file: {}", destination);
            try (ReadableByteChannel src = Channels.newChannel(Files.newInputStream(source));
                 FileChannel dest = new FileOutputStream(destination.toFile()).getChannel()) {
                dest.transferFrom(src, 0, Integer.MAX_VALUE);
            } catch (IOException e) {
                LOGGER.error(String.format("Could not create scripts file %s", source.getFileName()), e);
            }
        }
    }

    public synchronized void reloadGroovyFiles() {
        confirmationSuppliers.clear();
        groovyClassLoader.clearCache();

        //this will compile all groovy files
        List<Class> classes = compileGroovyFiles();
        classes.stream()
                .filter(aClass -> aClass.getGenericInterfaces().length != 0
                        && aClass.getGenericInterfaces()[0] instanceof ParameterizedType
                        && ((ParameterizedType) aClass.getGenericInterfaces()[0]).getRawType().equals(ConfirmationSupplier.class))
                .forEach(this::putToCache);

        groovyCacheChangedListener.accept(null);
    }

    public void uploadGroovyScript(InputStream inputStream, String scriptName) throws Exception {
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
            groovyCacheChangedListener.accept(null);
        } else {
            throw new InvalidClassException(String.format("Could not load class from file %s, check that class implements " +
                    "ConfirmationSupplier<REQUEST extends Request, RESPONSE extends Confirmation>", destination));
        }
    }

    private List<Class> compileGroovyFiles() {
        List<Class> compiledClasses = new LinkedList<>();
        CompilationUnit compileUnit = new CompilationUnit(groovyClassLoader);
        try (Stream<Path> stream = Files.walk(SCRIPTS_FOLDER)) {
            stream.filter(path -> path.toString().endsWith(".groovy") && Files.isRegularFile(path))
                    .forEach(path -> {
                        LOGGER.debug("Adding file for compilation: {}", path);
                        compileUnit.addSource(path.toFile());
                    });
        } catch (IOException | GroovyRuntimeException e) {
            LOGGER.error(String.format("Can't walk path: %s", SCRIPTS_FOLDER), e);
        }
        compileUnit.compile();

        for (Object compileClass : compileUnit.getClasses()) {
            GroovyClass groovyClass = (GroovyClass) compileClass;
            byte[] compiledScriptBytes = groovyClass.getBytes();
            compiledClasses.add(compileUnit.getClassLoader().defineClass(groovyClass.getName(), compiledScriptBytes));
        }

        return compiledClasses;
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
}
