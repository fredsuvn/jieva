package xyz.fsgek.common.base;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.collect.GekColl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is used to convert name case in different case format such as camel-case, underscore-case.
 *
 * @author fredsuvn
 */
public abstract class GekCase {

    /**
     * Upper camel case with {@link CamelCase#AS_LOWER}.
     */
    public static final GekCase UPPER_CAMEL = camelCase(true, CamelCase.AS_LOWER);
    /**
     * Lower camel case with {@link CamelCase#AS_LOWER}.
     */
    public static final GekCase LOWER_CAMEL = camelCase(false, CamelCase.AS_LOWER);
    /**
     * Upper underscore separator case.
     */
    public static final GekCase UPPER_UNDERSCORE = delimiterCase("_", t -> GekString.upperCase(t.toChars()));
    /**
     * Lower underscore separator case.
     */
    public static final GekCase LOWER_UNDERSCORE = delimiterCase("_", t -> GekString.lowerCase(t.toChars()));
    /**
     * Upper hyphen separator case.
     */
    public static final GekCase UPPER_HYPHEN = delimiterCase("-", t -> GekString.upperCase(t.toChars()));
    /**
     * Lower hyphen separator case.
     */
    public static final GekCase LOWER_HYPHEN = delimiterCase("-", t -> GekString.lowerCase(t.toChars()));

    /**
     * Returns a new camel case with given character policy.
     * The character policy specifies how to deal with non-lower and non-upper characters such as digit, supports
     * {@link CamelCase#AS_LOWER}, {@link CamelCase#AS_UPPER} and {@link CamelCase#AS_INDEPENDENT}.
     *
     * @param isUpper    whether this case is upper camel case
     * @param charPolicy given character policy
     * @return a new camel case with given character policy
     */
    public static GekCase camelCase(boolean isUpper, int charPolicy) {
        return new CamelCase(isUpper, charPolicy);
    }

    /**
     * Returns a new delimiter case with specified delimiter and token processor.
     * Each token will be processed by token processor before joining.
     *
     * @param delimiter      specified delimiter
     * @param tokenProcessor token processor
     * @return a new delimiter case with specified delimiter and token processor
     */
    public static GekCase delimiterCase(CharSequence delimiter, Function<Token, CharSequence> tokenProcessor) {
        return new DelimiterCase(delimiter, tokenProcessor);
    }

    /**
     * Tokenize operation, to split given chars into a token list with rules of this case.
     *
     * @param chars given chars
     * @return token list
     */
    public abstract List<Token> tokenize(CharSequence chars);

    /**
     * Join operation, to join given tokens to one String in rules of this case.
     *
     * @param tokens given tokens
     * @return joined string
     */
    public abstract String join(List<Token> tokens);

    /**
     * Converts given chars from this case to other case. The default implementation is:
     * <pre>
     *     return otherCase.join(tokenize(chars));
     * </pre>
     *
     * @param chars     given chars
     * @param otherCase other case
     * @return converted string
     * @see #tokenize(CharSequence)
     * @see #join(List)
     */
    public String toCase(CharSequence chars, GekCase otherCase) {
        return otherCase.join(tokenize(chars));
    }

    /**
     * Token split from a name in some case-rules.
     */
    public interface Token {

        /**
         * Returns this token as char sequence.
         *
         * @return this token as char sequence
         */
        CharSequence toChars();
    }

    /**
     * Camel case of {@link GekCase}.
     */
    public static class CamelCase extends GekCase {

        /**
         * Let non-lower and non-upper character as lower character.
         */
        public static final int AS_LOWER = 0;
        /**
         * Let non-lower and non-upper character as upper character.
         */
        public static final int AS_UPPER = 1;
        /**
         * Let non-lower and non-upper character as independent character.
         * The independent characters will be tokenized into independent token, not mixed with lower/upper characters.
         */
        public static final int AS_INDEPENDENT = 2;

        private final boolean isUpper;
        private final int charPolicy;

        /**
         * Constructs with given character policy.
         * The character policy specifies how to deal with non-lower and non-upper characters such as digit, supports
         * {@link #AS_LOWER}, {@link #AS_UPPER} and {@link #AS_INDEPENDENT}.
         *
         * @param isUpper    whether this case is upper camel case
         * @param charPolicy given character policy
         */
        public CamelCase(boolean isUpper, int charPolicy) {
            this.isUpper = isUpper;
            this.charPolicy = charPolicy;
        }

        @Override
        public List<Token> tokenize(CharSequence chars) {
            if (GekString.isBlank(chars)) {
                return Collections.emptyList();
            }
            int len = chars.length();
            if (len == 1) {
                return Collections.singletonList(CharsToken.of(chars, 0, chars.length()));
            }
            List<Token> result = new LinkedList<>();
            int tokenStartIndex = 0;
            int lastCharType = getCharType(chars.charAt(0));
            for (int i = 1; i < chars.length(); i++) {
                char c = chars.charAt(i);
                int currentCharType = getCharType(c);
                // AA, aa, 00: just i++
                if (lastCharType == currentCharType) {
                    continue;
                }
                // Aa: one token
                if (lastCharType == AS_UPPER && currentCharType == AS_LOWER) {
                    int tokenEndIndex = i - 1;
                    if (tokenEndIndex > tokenStartIndex) {
                        result.add(CharsToken.of(chars, tokenStartIndex, tokenEndIndex));
                    }
                    tokenStartIndex = tokenEndIndex;
                }
                // aA: two tokens
                else if (lastCharType == AS_LOWER && currentCharType == AS_UPPER) {
                    if (i > tokenStartIndex) {
                        result.add(CharsToken.of(chars, tokenStartIndex, i));
                    }
                    tokenStartIndex = i;
                }
                // A0: three tokens
                else if (lastCharType == AS_UPPER && currentCharType == AS_INDEPENDENT) {
                    // first token
                    int tokenEndIndex = i - 1;
                    if (tokenEndIndex > tokenStartIndex) {
                        result.add(CharsToken.of(chars, tokenStartIndex, tokenEndIndex));
                    }
                    // second token: A
                    result.add(CharsToken.of(chars, tokenEndIndex, i));
                    // third token
                    tokenStartIndex = i;
                }
                // 0A, a0, 0a: two words
                else {
                    if (i > tokenStartIndex) {
                        result.add(CharsToken.of(chars, tokenStartIndex, i));
                    }
                    tokenStartIndex = i;
                }
                lastCharType = currentCharType;
            }
            if (tokenStartIndex < len) {
                result.add(CharsToken.of(chars, tokenStartIndex, len));
            }
            return result;
        }

        private int getCharType(char c) {
            if (Character.isLowerCase(c)) {
                return AS_LOWER;
            }
            if (Character.isUpperCase(c)) {
                return AS_UPPER;
            }
            if (charPolicy == AS_LOWER) {
                return AS_LOWER;
            }
            if (charPolicy == AS_UPPER) {
                return AS_UPPER;
            }
            return AS_INDEPENDENT;
        }

        @Override
        public String join(List<Token> tokens) {
            if (GekColl.isEmpty(tokens)) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            Iterator<Token> it = tokens.iterator();
            if (!it.hasNext()) {
                return "";
            }
            Token first = it.next();
            sb.append(processToken(first, isUpper));
            while (it.hasNext()) {
                sb.append(processToken(it.next(), true));
            }
            return sb.toString();
        }

        private CharSequence processToken(Token token, boolean upper) {
            CharSequence chars = token.toChars();
            if (GekString.isEmpty(chars)) {
                return "";
            }
            if (chars.length() > 1 && GekString.allUpperCase(chars)) {
                return chars;
            }
            return GekString.firstCase(chars, upper);
        }
    }

    /**
     * Delimiter case of {@link GekCase}.
     * This implementation uses specified delimiter to split a name, and joins tokens with that delimiter.
     */
    public static class DelimiterCase extends GekCase {

        private final CharSequence delimiter;
        private final Function<Token, CharSequence> tokenProcessor;

        /**
         * Constructs with specified delimiter.
         *
         * @param delimiter specified delimiter
         */
        public DelimiterCase(CharSequence delimiter) {
            this(delimiter, null);
        }

        /**
         * Constructs with specified delimiter and token processor.
         * Each token will be processed by token processor before joining if it is not null.
         *
         * @param delimiter      specified delimiter
         * @param tokenProcessor token processor, may be null
         */
        public DelimiterCase(CharSequence delimiter, @Nullable Function<Token, CharSequence> tokenProcessor) {
            this.delimiter = delimiter;
            this.tokenProcessor = tokenProcessor == null ? Token::toChars : tokenProcessor;
        }

        @Override
        public List<Token> tokenize(CharSequence chars) {
            return GekString.split(chars, delimiter, CharsToken::of);
        }

        @Override
        public String join(List<Token> tokens) {
            if (tokens.isEmpty()) {
                return "";
            }
            return tokens.stream().map(tokenProcessor).collect(Collectors.joining(delimiter));
        }
    }

    private static final class CharsToken implements Token, CharSequence {

        private static CharsToken of(CharSequence source, int startIndex, int endIndex) {
            return new CharsToken(source, startIndex, endIndex);
        }

        private final CharSequence source;
        private final int startIndex;
        private final int endIndex;

        private CharsToken(CharSequence source, int startIndex, int endIndex) {
            this.source = source;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public CharSequence toChars() {
            return this;
        }

        @Override
        public int length() {
            return endIndex - startIndex;
        }

        @Override
        public char charAt(int index) {
            return source.charAt(startIndex + index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new CharsToken(source, startIndex + start, startIndex + end);
        }

        @Override
        public String toString() {
            return source.subSequence(startIndex, endIndex).toString();
        }
    }
}
