package xyz.srclab.build.processor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import xyz.srclab.build.annotations.FsMethods;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * To join static methods into Fs.
 *
 * @author fredsuvn
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
    "xyz.srclab.build.annotations.FsMethods",
    "xyz.srclab.build.annotations.FsMethod"
})
public class FsBuildingProcossor extends AbstractProcessor {

    private Elements elementUtils;
    private Path fsPath;
    private String fsTemplate;
    private final List<MethodInfo> methodInfos = new LinkedList<>();
    private final boolean writeFs = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        String rootProjectPath = System.getProperty("rootProjectPath");
        fsPath = Paths.get(rootProjectPath, "fs-core/src/main/java/xyz/srclab/common/base/Fs.java");
        fsTemplate = getFsVm();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (Element element : roundEnv.getElementsAnnotatedWith(FsMethods.class)) {
                if (!(element instanceof TypeElement)) {
                    continue;
                }
                TypeElement typeElement = (TypeElement) element;
                for (Element enclosedElement : typeElement.getEnclosedElements()) {
                    if (!(enclosedElement instanceof ExecutableElement)) {
                        continue;
                    }
                    ExecutableElement executableElement = (ExecutableElement) enclosedElement;
                    Set<Modifier> modifiers = executableElement.getModifiers();
                    if (!(modifiers.contains(Modifier.PUBLIC) && modifiers.contains(Modifier.STATIC))) {
                        continue;
                    }
                    MethodInfo methodInfo = buildMethodSource(executableElement);
                    if (!methodInfos.contains(methodInfo)) {
                        methodInfos.add(methodInfo);
                    }
                }
            }
            if (roundEnv.processingOver()) {
                writeFsJavaFile(fsPath.toString(), fsTemplate, methodInfos);
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MethodInfo buildMethodSource(ExecutableElement element) {
        String comment = elementUtils.getDocComment(element);
        String returnType = element.getReturnType().toString();
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setComment(comment);
        methodInfo.setReturnType(getShortName(returnType));
        methodInfo.setMethodName(element.getSimpleName().toString());

        List<? extends TypeParameterElement> typeParameterElements = element.getTypeParameters();
        if (typeParameterElements != null && !typeParameterElements.isEmpty()) {
            methodInfo.setTypeParams(typeParameterElements.stream().map(it->it.toString()).collect(Collectors.toList()));
        }

        List<String> ims = new LinkedList<>();
        ims.add(returnType);
        methodInfo.setIms(ims);
        return methodInfo;
    }

    private String getShortName(String name) {
        int fromIndex = name.length() - 1;
        int dotIndex = name.lastIndexOf(".", fromIndex);
        int wordCount = 0;
        while (true) {
            if (dotIndex > 0) {
                wordCount++;
                if (wordCount > 1) {
                    if (Character.isLowerCase(name.charAt(dotIndex + 1))) {
                        return name.substring(fromIndex + 2);
                    }
                }
            } else {
                if (fromIndex == name.length() - 1) {
                    return name;
                }
                if (Character.isLowerCase(name.charAt(0))) {
                    return name.substring(fromIndex + 2);
                }
            }
            fromIndex = dotIndex - 1;
            dotIndex = name.lastIndexOf(".", fromIndex);
        }
    }

    private String getFsVm() {
        try {
            InputStream in = FsBuildingProcossor.class.getResourceAsStream("/templates/Fs.vm");
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFsJavaFile(String destPath, String template, List<MethodInfo> methodInfos) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        VelocityContext context = new VelocityContext();
        context.put("methods", methodInfos);
        List<String> ims = new LinkedList<>();
        for (MethodInfo methodInfo : methodInfos) {
            for (String im : methodInfo.getIms()) {
                if (!ims.contains(im) && im.indexOf(".") > 0) {
                    ims.add(im);
                }
            }
        }
        context.put("ims", ims);
        StringWriter writer = new StringWriter();
        engine.evaluate(context, writer, "vm", template);

        if (writeFs) {
            try {
                PrintStream printStream = new PrintStream(new FileOutputStream(destPath, false));
                printStream.println(writer);
                printStream.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("writer: " + writer);
        }
    }

    @ToString
    @EqualsAndHashCode
    @Data
    public static class MethodInfo {
        private String comment;
        private String returnType;
        private String methodName;
        private List<String> typeParams;
        private List<String> ims;
    }
}
