package xyz.srclab.common.pattern.provider.load;

import xyz.srclab.common.lang.tuple.Pair;
import xyz.srclab.common.reflect.instance.InstanceHelper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author sunqian
 */
final class ClassNamesProviderLoader<T> extends AbstractProviderLoader<T> {

    private static <T> Map<String, T> parseClassNamesDescriptor(String classNamesDescriptor) {
        if (!classNamesDescriptor.contains(",")) {
            Pair<String, T> provider = parseClassNameDescriptor(classNamesDescriptor);
            return Collections.singletonMap(provider.get0(), provider.get1());
        }

        String[] classNameDescriptors = classNamesDescriptor.split(",");
        Map<String, T> providerMap = new LinkedHashMap<>();
        for (String classNameDescriptor : classNameDescriptors) {
            Pair<String, T> provider = parseClassNameDescriptor(classNameDescriptor.trim());
            providerMap.put(provider.get0(), provider.get1());
        }
        return providerMap;
    }

    private static <T> Pair<String, T> parseClassNameDescriptor(String classNameDescriptor) {
        if (!classNameDescriptor.contains(":")) {
            return Pair.of(classNameDescriptor, InstanceHelper.newInstance(classNameDescriptor));
        }
        String[] kv = classNameDescriptor.split(":");
        if (kv.length != 2) {
            throw new IllegalArgumentException("Parse class name failed: " + classNameDescriptor);
        }
        return Pair.of(kv[0].trim(), InstanceHelper.newInstance(kv[1].trim()));
    }

    ClassNamesProviderLoader(String classNamesDescriptor) {
        super(parseClassNamesDescriptor(classNamesDescriptor));
    }
}
