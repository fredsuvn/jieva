package xyz.srclab.common.pattern.provider;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.collection.list.ListHelper;
import xyz.srclab.common.reflect.classpath.ClassPathHelper;
import xyz.srclab.common.lang.tuple.Pair;
import xyz.srclab.common.reflect.instance.InstanceHelper;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author sunqian
 */
final class ClassDescriptorProviderLoader<T> extends AbstractProviderLoader<T> {

    private static final List<Condition> conditions = ListHelper.immutable(Arrays.asList(
            new ConditionOnClass(),
            new ConditionOnMissingClass()
    ));

    private static <T> Map<String, T> parseClassesDescriptor(String classesDescriptor) {
        if (!classesDescriptor.contains(",")) {
            Pair<String, T> provider = parseClassDescriptor(classesDescriptor.trim());
            return Collections.singletonMap(provider.get0(), provider.get1());
        }

        String[] classNameDescriptors = classesDescriptor.split(",");
        Map<String, T> providerMap = new LinkedHashMap<>();
        for (String classNameDescriptor : classNameDescriptors) {
            Pair<String, T> provider = parseClassDescriptor(classNameDescriptor.trim());
            providerMap.put(provider.get0(), provider.get1());
        }
        return providerMap;
    }

    private static <T> Pair<String, T> parseClassDescriptor(String classDescriptor) {
        if (classDescriptor.contains("|")) {
            String[] classDescriptors = classDescriptor.split("\\|");
            for (String descriptor : classDescriptors) {
                @Nullable Pair<String, T> pair = parseClassDescriptor0(descriptor.trim());
                if (pair != null) {
                    return pair;
                }
            }
            throw new NoSuchElementException(classDescriptor);
        }

        @Nullable Pair<String, T> pair = parseClassDescriptor0(classDescriptor);
        checkArguments(pair != null, classDescriptor);
        return pair;
    }

    @Nullable
    private static <T> Pair<String, T> parseClassDescriptor0(String classDescriptor) {
        String providerName;
        String providerClass;
        @Nullable String condition = null;
        int conditionSeparatorIndex = classDescriptor.indexOf("(");
        checkArguments(
                conditionSeparatorIndex != 0
                        && conditionSeparatorIndex < classDescriptor.length() - 1,
                classDescriptor
        );
        int nameSeparatorIndex = classDescriptor.indexOf(":");
        if (conditionSeparatorIndex > 0) {
            checkArguments(classDescriptor.endsWith(")"), classDescriptor);
            condition = classDescriptor.substring(conditionSeparatorIndex + 1, classDescriptor.length() - 1).trim();
            if (nameSeparatorIndex >= 0 && nameSeparatorIndex < conditionSeparatorIndex) {
                providerName = classDescriptor.substring(0, nameSeparatorIndex).trim();
                providerClass = classDescriptor.substring(nameSeparatorIndex + 1, conditionSeparatorIndex).trim();
            } else {
                providerClass = classDescriptor.substring(0, conditionSeparatorIndex).trim();
                providerName = providerClass;
            }
        } else {
            if (nameSeparatorIndex >= 0) {
                providerName = classDescriptor.substring(0, nameSeparatorIndex).trim();
                providerClass = classDescriptor.substring(nameSeparatorIndex + 1).trim();
            } else {
                providerClass = classDescriptor;
                providerName = providerClass;
            }
        }
        return parseClassDescriptor0(providerName, providerClass, condition);
    }

    @Nullable
    private static <T> Pair<String, T> parseClassDescriptor0(
            String providerName,
            String providerClass,
            @Nullable String conditionDescriptor
    ) {
        if (conditionDescriptor == null) {
            return Pair.of(providerName, InstanceHelper.newInstance(providerClass));
        }
        String[] conditionComponents = conditionDescriptor.split(":");
        checkArguments(conditionComponents.length == 2, conditionDescriptor);
        for (Condition condition : conditions) {
            if (!conditionComponents[0].equals(condition.getConditionName())) {
                continue;
            }
            if (condition.test(conditionComponents[1])) {
                return Pair.of(providerName, InstanceHelper.newInstance(providerClass));
            }
        }
        return null;
    }

    private static void checkArguments(boolean expression, String classDescriptor) {
        Checker.checkArguments(expression, "Parse class descriptor failed: " + classDescriptor);
    }

    ClassDescriptorProviderLoader(String classNamesDescriptor) {
        super(parseClassesDescriptor(classNamesDescriptor));
    }

    private interface Condition extends Predicate<String> {

        String getConditionName();
    }

    private static final class ConditionOnClass implements Condition {

        @Override
        public String getConditionName() {
            return "onClass";
        }

        @Override
        public boolean test(String className) {
            return ClassPathHelper.hasClass(className);
        }
    }

    private static final class ConditionOnMissingClass implements Condition {

        @Override
        public String getConditionName() {
            return "onMissing";
        }

        @Override
        public boolean test(String className) {
            return !ClassPathHelper.hasClass(className);
        }
    }
}
