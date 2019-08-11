package com.omb.ocpp.groovy;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.tools.GroovyClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BatchGroovyClassLoader extends GroovyClassLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchGroovyClassLoader.class);
    private final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
    private final ErrorCollector errorCollector = new ErrorCollector(compilerConfiguration);

    public List<Class> parseClasses(List<Path> groovyFiles) {
        if (groovyFiles == null || groovyFiles.isEmpty()) {
            return new LinkedList<>();
        } else {
            return compileGroovyFiles(groovyFiles);
        }
    }

    private List<Class> compileGroovyFiles(List<Path> groovyFiles) {
        List<SourceUnit> sourceUnits = getGroovySources(groovyFiles);
        if (sourceUnits.isEmpty()) {
            LOGGER.error("No groovy sources found for compilation");
            return new LinkedList<>();
        }

        InnerLoader innerLoader = new InnerLoader(this);
        CompilationUnit compilationUnit = new CompilationUnit(innerLoader);
        compilationUnit.setConfiguration(compilerConfiguration);
        sourceUnits.forEach(compilationUnit::addSource);

        try {
            compilationUnit.compile(Phases.CLASS_GENERATION);
        } catch (CompilationFailedException e) {
            LOGGER.error("Failed to compile groovy files", e);
            return new LinkedList<>();
        }

        List<Class> classes = (List<Class>) compilationUnit.getClasses().stream()
                .map(groovyClass -> defineClass((GroovyClass) groovyClass))
                .collect(Collectors.toList());
        return classes;
    }

    private Class defineClass(GroovyClass groovyClass) {
        Class clazz = this.defineClass(groovyClass.getName(), groovyClass.getBytes());
        setClassCacheEntry(clazz);
        return clazz;
    }

    private List<SourceUnit> getGroovySources(List<Path> groovyFiles) {
        return groovyFiles.stream()
                .map(path -> {
                    LOGGER.debug("Added file for compilation {} ", path);
                    return new SourceUnit(path.toFile(), compilerConfiguration, this, errorCollector);
                })
                .collect(Collectors.toList());
    }
}
