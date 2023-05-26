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
     * Upper underscore separator case.
     */
    FsCase UPPER_UNDERSCORE = separatorCase("_", true);
    /**
     * Lower underscore separator case.
     */
    FsCase LOWER_UNDERSCORE = separatorCase("_", false);
    /**
     * Upper hyphen separator case.
     */
    FsCase UPPER_HYPHEN = separatorCase("-", true);
    /**
     * Lower hyphen separator case.
     */
    FsCase LOWER_HYPHEN = separatorCase("-", false);

    /**
     * Returns camel case (upper or lower) with given upper setting.
     * This implementation uses
     * {@link Character#isUpperCase(char)}, {@link Character#toUpperCase(char)} and {@link Character#toLowerCase(char)}
     * to check and convert a char.
     *
     * @param isUpper given upper setting
     */
    static FsCase camelCase(boolean isUpper) {
        return new CamelCase(isUpper);
    }

    /**
     * Returns separator case with given separator and upper setting.
     * If upper setting is not null, this case will process each split part from {@link #split(CharSequence)}
     * by upper setting.
     * If upper setting is null, there is no case process for each split part.
     *
     * @param separator given separator
     * @param upper     upper setting
     */
    static FsCase separatorCase(CharSequence separator, Boolean upper) {
        return new SeparatorCase(separator, upper);
    }

    /**
     * Splits given chars into a word list using rules of this implementation.
     * Default implementations use {@link FsString#subRef(CharSequence, int, int)} to build sub-sequence.
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
     * This implementation uses
     * {@link Character#isUpperCase(char)}, {@link Character#toUpperCase(char)} and {@link Character#toLowerCase(char)}
     * to check and convert a char.
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
                        result.add(FsString.subRef(chars, wordStart, wordEnd));
                    }
                    wordStart = wordEnd;
                }
                // aA: two words
                else {
                    if (i > wordStart) {
                        result.add(FsString.subRef(chars, wordStart, i));
                    }
                    wordStart = i;
                }
                lastIsUpper = currentIsUpper;
            }
            if (wordStart < len) {
                result.add(FsString.subRef(chars, wordStart, len));
            }
            return result;
        }

        @Override
        public String join(List<CharSequence> words) {
            if (FsCollect.isEmpty(words)) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            Iterator<CharSequence> it = words.iterator();
            if (!it.hasNext()) {
                return "";
            }
            CharSequence first = it.next();
            sb.append(processCase(first, isUpper));
            while (it.hasNext()) {
                sb.append(processCase(it.next(), true));
            }
            return sb.toString();
        }

        private CharSequence processCase(CharSequence chars, boolean upper) {
            if (FsString.isEmpty(chars)) {
                return "";
            }
            if (chars.length() > 1 && FsString.allUpperCase(chars)) {
                return chars;
            }
            return FsString.firstCase(chars, upper);
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
         * If upper setting is not null, this case will process each split part from {@link #split(CharSequence)}
         * by upper setting.
         * If upper setting is null, there is no case process for each split part.
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
                if (upper) {
                    return FsString.join(separator, words.stream().map(FsString::upperCase).collect(Collectors.toList()));
                } else {
                    return FsString.join(separator, words.stream().map(FsString::lowerCase).collect(Collectors.toList()));
                }
            }
        }
    }
}
