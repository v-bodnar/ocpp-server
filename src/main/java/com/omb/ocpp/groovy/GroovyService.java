package com.omb.ocpp.groovy;

import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.tools.GroovyClass;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
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
import java.util.stream.Stream;

import static com.omb.ocpp.gui.Application.LITHOS_HOME;

@Service
public class GroovyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyService.class);
    private static final Path GROOVY_PROJECT_FOLDER = Paths.get(LITHOS_HOME, "ocpp", "groovy");
    private static final Path SUPPLIERS_FOLDER = Paths.get(GROOVY_PROJECT_FOLDER.toString(), "src", "main", "groovy", "com",
            "omb", "ocpp", "groovy", "supplier");

    private final Map<Class<? extends Request>, ConfirmationSupplier<Request, Confirmation>> confirmationSuppliers =
            new HashMap<>();
    private Consumer<Void> groovyCacheChangedListener = aVoid -> LOGGER.debug("No listeners attached");

    private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    public void loadGroovyScripts() {
        try {
            if (!GROOVY_PROJECT_FOLDER.toFile().exists()) {
                throw new FileNotFoundException(String.format("Please execute \"git clone https://github" +
                        ".com/v-bodnar/GroovyOcppSupplier.git %s \"", GROOVY_PROJECT_FOLDER));
            }
            reloadGroovyFiles();
        } catch (IOException e) {
            LOGGER.error("Could not load groovy scripts", e);
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
        Path destination = Paths.get(SUPPLIERS_FOLDER.toString(), scriptName);
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
        List<SourceUnit> sourceUnits = new LinkedList<>();
        CompilationUnit compileUnit = new CompilationUnit(groovyClassLoader);
        try (Stream<Path> stream = Files.walk(GROOVY_PROJECT_FOLDER)) {
            stream.filter(path -> path.toString().endsWith(".groovy") && Files.isRegularFile(path))
                    .forEach(path -> {
                        LOGGER.debug("Adding file for compilation: {}", path);
                        sourceUnits.add(compileUnit.addSource(path.toFile()));
                    });
        } catch (IOException | GroovyRuntimeException e) {
            LOGGER.error(String.format("Can't walk path: %s", GROOVY_PROJECT_FOLDER), e);
        }
        compileUnit.compile();

        for (Object compileClass : compileUnit.getClasses()) {
            GroovyClass groovyClass = (GroovyClass) compileClass;
            byte[] compiledScriptBytes = groovyClass.getBytes();
            try {
                Class clazz = compileUnit.getClassLoader().loadClass(groovyClass.getName());
                throw new FeatureNotSupportedException("Dynamic groovy reload is not supported yet"); //todo
//                sourceUnits.stream()
//                        .filter(sourceUnit1 -> sourceUnit1.getName().endsWith(String.format("%s.groovy",
//                                clazz.getSimpleName())))
//                        .findFirst().ifPresentOrElse(sourceUnit1 -> {
//                            try {
//                                compiledClasses.add(compileUnit.getClassLoader().parseClass(sourceUnit1.getSource().getReader(),
//                                        sourceUnit1.getSource().getURI().toString()));
//                            } catch (IOException e) {
//                                LOGGER.error(String.format("Can't reload class %s", groovyClass.getName()), e);
//                            }
//                        },
//                        () -> LOGGER.error("Can't reload class {}", groovyClass.getName()));

            } catch (ClassNotFoundException e) {
                compiledClasses.add(compileUnit.getClassLoader().defineClass(groovyClass.getName(), compiledScriptBytes));
            } catch (FeatureNotSupportedException e) {
                LOGGER.error(e.getMessage());
            }
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

    class FeatureNotSupportedException extends Exception{
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
