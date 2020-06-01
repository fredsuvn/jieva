package xyz.srclab.common.pattern.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Loader;
import xyz.srclab.common.collection.ListHelper;
import xyz.srclab.common.collection.MapHelper;
import xyz.srclab.common.string.CharsRef;
import xyz.srclab.common.lang.tuple.Pair;
import xyz.srclab.common.reflect.ClassHelper;
import xyz.srclab.common.string.StringHelper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author sunqian
 */
final class StringDescriptorProviderLoader<T> implements ProviderLoader<T> {

    private static final String PARSE_ERROR = "Failed to parse provider descriptor: ";

    private static final List<Condition> conditions = ListHelper.immutable(Arrays.asList(
            new ConditionOnClass(),
            new ConditionOnMissingClass()
    ));

    private final String stringDescriptor;
    private final ClassLoader classLoader;

    @Immutable
    private @Nullable Map<String, T> providers;

    StringDescriptorProviderLoader(String stringDescriptor) {
        this(stringDescriptor, Loader.currentClassLoader());
    }

    StringDescriptorProviderLoader(String stringDescriptor, ClassLoader classLoader) {
        this.stringDescriptor = stringDescriptor;
        this.classLoader = classLoader;
    }

    @Override
    public @Immutable Map<String, T> load() {
        if (providers == null) {
            providers = load0();
        }
        return providers;
    }

    @Immutable
    private Map<String, T> load0() {
        List<ProviderCandidate> candidates = parseStringDescriptor(stringDescriptor);
        return MapHelper.immutable(candidates.stream().collect(Collectors.toMap(
                ProviderCandidate::getProviderName,
                c -> newProviderInstance(c.getProviderClassName())
        )));
    }

    private List<ProviderCandidate> parseStringDescriptor(String stringDescriptor) {
        List<ProviderCandidate> result = new LinkedList<>();
        int index = -1;
        do {
            int separator = stringDescriptor.indexOf(',', index + 1);
            @Nullable ProviderCandidate candidate;
            if (separator < 0) {
                candidate = parseCandidateGroup(CharsRef.of(stringDescriptor, 0).trim());
                index = stringDescriptor.length();
            } else {
                candidate = parseCandidateGroup(CharsRef.of(stringDescriptor, index + 1, separator).trim());
                index = separator;
            }
            if (candidate != null) {
                result.add(candidate);
            }
        } while (index < stringDescriptor.length());
        return result;
    }

    @Nullable
    private ProviderCandidate parseCandidateGroup(CharsRef groupRef) {
        int index = -1;
        do {
            int separator = StringHelper.indexOf(groupRef, index + 1, groupRef.length(), '|');
            ProviderCandidate candidate;
            if (separator < 0) {
                candidate = parseCandidate(groupRef.subSequence(index + 1).trim());
                index = groupRef.length();
            } else {
                candidate = parseCandidate(groupRef.subSequence(index + 1, separator).trim());
                index = separator;
            }
            if (candidate.getCondition() == null) {
                return candidate;
            }
            if (candidate.getCondition().test(candidate.getConditionValue())) {
                return candidate;
            }
        } while (index < groupRef.length());
        return null;
    }

    private ProviderCandidate parseCandidate(CharsRef candidateRef) {
        int conditionBegin = candidateRef.indexOf('(');
        Checker.checkArguments(conditionBegin < 0 ||
                        (conditionBegin > 0
                                && conditionBegin < candidateRef.length() - 2
                                && candidateRef.charAt(candidateRef.length() - 1) == ')'),
                () -> PARSE_ERROR + candidateRef
        );
        String providerName;
        String providerClassName;
        @Nullable Condition condition;
        @Nullable String conditionValue;
        if (conditionBegin < 0) {
            condition = null;
            conditionValue = null;
        } else {
            Pair<Condition, String> pair =
                    parseConditionPair(candidateRef.subSequence(conditionBegin));
            condition = pair.first();
            conditionValue = pair.second();
        }
        CharsRef nonConditionPart;
        if (condition == null) {
            nonConditionPart = candidateRef;
        } else {
            nonConditionPart = candidateRef.subSequence(0, conditionBegin).trim();
        }
        int classNameIndicator = nonConditionPart.indexOf(':');
        if (classNameIndicator < 0) {
            providerClassName = nonConditionPart.toString();
            providerName = providerClassName;
        } else {
            providerName = nonConditionPart.subSequence(0, classNameIndicator).trim().toString();
            providerClassName = nonConditionPart.subSequence(classNameIndicator + 1).trim().toString();
        }
        return new ProviderCandidate(providerName, providerClassName, condition, conditionValue);
    }

    private Pair<@Nullable Condition, @Nullable String> parseConditionPair(CharsRef conditionRef) {
        int conditionValueIndicator = conditionRef.indexOf(':');
        Checker.checkArguments(
                conditionValueIndicator > 1 && conditionValueIndicator < conditionRef.length() - 1,
                () -> PARSE_ERROR + conditionRef
        );
        String conditionType = conditionRef.subSequence(1, conditionValueIndicator).trim().toString();
        String conditionValue = conditionRef.subSequence(
                conditionValueIndicator + 1, conditionRef.length() - 1).trim().toString();
        for (Condition supportedCondition : conditions) {
            if (conditionType.equals(supportedCondition.getType())) {
                return Pair.of(supportedCondition, conditionValue);
            }
        }
        throw new IllegalArgumentException("Unknown condition type: " + conditionType);
    }

    private T newProviderInstance(String className) {
        return ClassHelper.newInstance(className, classLoader);
    }

    private static final class ProviderCandidate {

        private final String providerName;
        private final String providerClassName;
        private final @Nullable Condition condition;
        private final @Nullable String conditionValue;

        private ProviderCandidate(
                String providerName,
                String providerClassName,
                @Nullable Condition condition,
                @Nullable String conditionValue
        ) {
            this.providerName = providerName;
            this.providerClassName = providerClassName;
            this.condition = condition;
            this.conditionValue = conditionValue;
        }

        public String getProviderName() {
            return providerName;
        }

        public String getProviderClassName() {
            return providerClassName;
        }

        @Nullable
        public Condition getCondition() {
            return condition;
        }

        @Nullable
        public String getConditionValue() {
            return conditionValue;
        }
    }

    private interface Condition extends Predicate<String> {

        String getType();
    }

    private static final class ConditionOnClass implements Condition {

        @Override
        public String getType() {
            return "onClass";
        }

        @Override
        public boolean test(String className) {
            return Loader.hasClass(className);
        }
    }

    private static final class ConditionOnMissingClass implements Condition {

        @Override
        public String getType() {
            return "onMissingClass";
        }

        @Override
        public boolean test(String className) {
            return !Loader.hasClass(className);
        }
    }
}
