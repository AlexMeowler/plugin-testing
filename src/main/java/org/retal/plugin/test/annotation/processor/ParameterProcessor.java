package org.retal.plugin.test.annotation.processor;

import org.apache.maven.plugins.annotations.Parameter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("org.apache.maven.plugins.annotations.Mojo")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ParameterProcessor extends AbstractProcessor {

    public static final String FILE_NAME_PATTERN = "%s-default-values.xml";
    public static final String ROOT_ELEMENT_NAME = "values";
    public static final String DEFAULT_VALUES_ELEMENT_NAME = "defaultValues";
    public static final String REQUIRED_VALUES_ELEMENT_NAME = "requiredValues";

    //TODO refactoring??
    //TODO separate project for testing plugins???

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for(TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                Map<String, String> defaultValues = element.getEnclosedElements().stream()
                        .filter(e -> Objects.nonNull(e.getAnnotation(Parameter.class)))
                        .filter(e -> !e.getAnnotation(Parameter.class).defaultValue().isEmpty())
                        .collect(Collectors.toMap(e -> e.getSimpleName().toString(),
                                e -> e.getAnnotation(Parameter.class).defaultValue()));
                List<String> requiredValues = element.getEnclosedElements().stream()
                        .filter(e -> Objects.nonNull(e.getAnnotation(Parameter.class)))
                        .filter(e -> e.getAnnotation(Parameter.class).required())
                        .map(Element::getSimpleName)
                        .map(Name::toString)
                        .collect(Collectors.toList());
                try {
                    String fileName = String.format(FILE_NAME_PATTERN, element.getSimpleName().toString());
                    FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", fileName);
                    PrintWriter writer = new PrintWriter(fileObject.openWriter());
                    writer.println(String.format("<%s>", ROOT_ELEMENT_NAME));
                    writer.println(String.format("\t<%s>", DEFAULT_VALUES_ELEMENT_NAME));
                    for(Map.Entry<String, String> entry : defaultValues.entrySet()) {
                        writer.println(String.format("\t\t<%s>%s</%s>", entry.getKey(), entry.getValue(), entry.getKey()));
                    }
                    writer.println(String.format("\t</%s>", DEFAULT_VALUES_ELEMENT_NAME));
                    writer.println(String.format("\t<%s>", REQUIRED_VALUES_ELEMENT_NAME));
                    for(String valueName : requiredValues) {
                        writer.println(String.format("\t\t<%s></%s>", valueName, valueName));
                    }
                    writer.println(String.format("\t</%s>", REQUIRED_VALUES_ELEMENT_NAME));
                    writer.println(String.format("</%s>", ROOT_ELEMENT_NAME));
                    //TODO possible recursion?
                    writer.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }
}
