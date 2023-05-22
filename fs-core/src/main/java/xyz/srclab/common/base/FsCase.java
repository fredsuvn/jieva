package xyz.srclab.common.base;

import xyz.srclab.common.collect.FsCollect;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Naming case utilities.
 *
 * @author fredsuvn
 */
public interface FsCase {

    /**
     * Upper camel case.
     */
    FsCase UPPER_CAMEL = camelCase(true);
    /**
     * Lower camel case.
     */
    FsCase LOWER_CAMEL = camelCase(false);
    /**
     * Underscore separator case.
     */
    FsCase UNDERSCORE = separatorCase("_", null);
    /**
     * Hyphen separator case.
     */
    FsCase HYPHEN = separatorCase("-", null);

    /**
     * Returns camel case (upper or lower) with given upper setting.
     *
     * @param isUpper given upper setting
     */
    static FsCase camelCase(boolean isUpper) {
        return new CamelCase(isUpper);
    }

    /**
     * Returns separator case with given separator and upper setting.
     * If upper setting is not null, this case will process each part from {@link #split(CharSequence)}
     * by upper setting.
     * If upper setting is null, there is no upper process.
     *
     * @param separator given separator
     * @param upper     upper setting
     */
    static FsCase separatorCase(CharSequence separator, Boolean upper) {
        return new SeparatorCase(separator, upper);
    }

    /**
     * Splits given chars in rules of this case.
     *
     * @param chars given chars
     */
    List<CharSequence> split(CharSequence chars);

    /**
     * Joins given split words to one String in rules of this case.
     * It is probable that the words is split by {@link #split(CharSequence)}.
     *
     * @param words given split words
     */
    String join(List<CharSequence> words);

    /**
     * Converts given chars from this case to other case:
     * <pre>
     *     return otherCase.join(split(chars));
     * </pre>
     *
     * @param chars     given chars
     * @param otherCase other case
     * @see #split(CharSequence)
     * @see #join(List)
     */
    default String convert(CharSequence chars, FsCase otherCase) {
        return otherCase.join(split(chars));
    }

    /**
     * Camel case implementation.
     */
    class CamelCase implements FsCase {

        private final boolean isUpper;

        /**
         * Constructs with given upper setting.
         *
         * @param isUpper given upper setting
         */
        public CamelCase(boolean isUpper) {
            this.isUpper = isUpper;
        }

        @Override
        public List<CharSequence> split(CharSequence chars) {
            if (FsString.isBlank(chars)) {
                return Collections.emptyList();
            }
            int len = chars.length();
            if (len == 1) {
                return Collections.singletonList(chars);
            }
            List<CharSequence> result = new LinkedList<>();
            int wordStart = 0;
            boolean lastIsUpper = Character.isUpperCase(chars.charAt(0));
            for (int i = 1; i < chars.length(); i++) {
                char c = chars.charAt(i);
                boolean currentIsUpper = Character.isUpperCase(c);
                if (Fs.equals(lastIsUpper, currentIsUpper)) {
                    continue;
                }
                // Aa: one word
                if (lastIsUpper && !currentIsUpper) {
                    int wordEnd = i - 1;
                    if (wordEnd > wordStart) {
                        result.add(chars.subSequence(wordStart, wordEnd));
                    }
                    wordStart = wordEnd;
                }
                // aA: two words
                else {
                    if (i > wordStart) {
                        result.add(chars.subSequence(wordStart, i));
                    }
                    wordStart = i;
                }
                lastIsUpper = currentIsUpper;
            }
            if (wordStart < len) {
                result.add(chars.subSequence(wordStart, len));
            }
            return result;
        }

        @Override
        public String join(List<CharSequence> words) {
            if (FsCollect.isEmpty(words)) {
                return "";
            }
            Iterator<CharSequence> it = words.iterator();
            CharSequence first = it.next();
            CharSequence firstR;
            if (FsString.allUpperCase(first)) {
                firstR = first;
            } else {
                firstR = FsString.firstCase(first, isUpper);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(firstR);
            while (it.hasNext()) {
                CharSequence chars = it.next();
                sb.append(processCase(chars));
            }
            return sb.toString();
        }

        private CharSequence processCase(CharSequence chars) {
            if (FsString.allUpperCase(chars)) {
                return chars;
            }
            return FsString.firstCase(chars, true);
        }

        @Override
        public String convert(CharSequence chars, FsCase otherCase) {
            if (otherCase instanceof CamelCase) {
                CamelCase other = (CamelCase) otherCase;
                if (other.isUpper == isUpper) {
                    return chars.toString();
                }
                if (FsString.isEmpty(chars)) {
                    return chars.toString();
                }
                int len = chars.length();
                if (len == 1) {
                    char c = chars.charAt(0);
                    return String.valueOf(other.isUpper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                }
                if (len == 2) {
                    char c1 = chars.charAt(0);
                    char c2 = chars.charAt(1);
                    boolean u1 = Character.isUpperCase(c1);
                    boolean u2 = Character.isUpperCase(c2);
                    if (u1 && u2) {
                        return chars.toString();
                    } else {
                        return new String(new char[]{
                            other.isUpper ? Character.toUpperCase(c1) : Character.toLowerCase(c1),
                            c2
                        });
                    }
                }
                if (len >= 3) {
                    char c1 = chars.charAt(0);
                    char c2 = chars.charAt(1);
                    char c3 = chars.charAt(2);
                    boolean u1 = Character.isUpperCase(c1);
                    boolean u2 = Character.isUpperCase(c2);
                    boolean u3 = Character.isUpperCase(c3);
                    if (u1 && u2 && u3) {
                        return chars.toString();
                    } else {
                        char[] cs = new char[len];
                        cs[0] = other.isUpper ? Character.toUpperCase(c1) : Character.toLowerCase(c1);
                        FsString.getChars(chars.subSequence(1, len), cs, 1, len - 1);
                        return new String(cs);
                    }
                }
            }
            return FsCase.super.convert(chars, otherCase);
        }
    }

    /**
     * Separator case implementation.
     */
    class SeparatorCase implements FsCase {

        private final CharSequence separator;
        private final Boolean upper;

        /**
         * Constructs with given separator and upper setting.
         * If upper setting is not null, this case will process each part from {@link #split(CharSequence)}
         * by upper setting.
         *
         * @param separator given separator
         * @param upper     upper setting
         */
        public SeparatorCase(CharSequence separator, Boolean upper) {
            this.separator = separator;
            this.upper = upper;
        }

        @Override
        public List<CharSequence> split(CharSequence chars) {
            return FsString.split(chars, separator);
        }

        @Override
        public String join(List<CharSequence> words) {
            if (words.isEmpty()) {
                return "";
            }
            if (upper == null) {
                return FsString.join(separator, words);
            } else {
                return FsString.join(separator, words.stream().map(it ->
                    upper ? FsString.upperCase(it) : FsString.lowerCase(it)).collect(Collectors.toList()));
            }
        }
    }
}
