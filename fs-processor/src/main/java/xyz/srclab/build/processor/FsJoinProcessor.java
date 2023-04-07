package xyz.srclab.build.processor;

import org.apache.commons.io.IOUtils;
import xyz.srclab.build.annotations.JoinFs;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * To join static methods into Fs.
 *
 * @author fredsuvn
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"xyz.srclab.build.annotations.JoinFs", "xyz.srclab.build.annotations.JoinFsAs"})
public class FsJoinProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private PrintStream printStream;
    private String fsTemp;
    private String fsMethodTemp;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();

        String rootProjectPath = System.getProperty("rootProjectPath");
        System.out.println(">>>>*******************" + rootProjectPath);
        Path fsPath = Paths.get(rootProjectPath, "fs-core/src/main/java/xyz/srclab/common/Fs.java");
        try {
            //printStream = new PrintStream(
            //    new BufferedOutputStream(new FileOutputStream(fsPath.toFile(), false)));
            fsTemp = getFsTemp();
            fsMethodTemp = getFsMethodTemp();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(JoinFs.class)) {
            if (!(element instanceof TypeElement)) {
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            for (Element enclosedElement : typeElement.getEnclosedElements()) {
                if (!(enclosedElement instanceof ExecutableElement)) {
                    continue;
                }
                ExecutableElement executableElement = (ExecutableElement) enclosedElement;
                buildMethodSource(executableElement, null);
                //处理注解
                //String comment = elementUtils.getDocComment(executableElement);
                //System.out.println(executableElement.getSimpleName() + " >>> " + comment);
            }
        }
        return true;
    }

    private String buildMethodSource(ExecutableElement element, List<String> outImport) {
        String comment = elementUtils.getDocComment(element);
        String returnTypeStr = element.getReturnType().toString();
        System.out.println("returnType: " + getShortName(returnTypeStr));
        return "";
    }

    private String getShortName(String name) {
        System.out.println("short---name: " + name);
        String[] words = name.split("\\.");
        if (words.length <= 1) {
            return name;
        }
        int i =  words.length - 1;
        for (; i >= 0; i--) {
            if (Character.isLowerCase(words[i].charAt(0))) {
                break;
            }
        }
        if (i >= words.length - 2) {
            return words[i + 1];
        }
        StringBuilder result = new StringBuilder();
        for (int j = i + 1; j < words.length; j++) {
            result.append(words[j]).append(".");
        }



        result.delete(result.length() - 1, result.length());
        return result.toString();
    }

    private String getFsTemp() {
        try {
            return IOUtils.resourceToString("/templates/Fs.temp", StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFsMethodTemp() {
        try {
            return IOUtils.resourceToString("/templates/Fs-method.temp", StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
