package xyz.srclab.common.net.http;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.collect.FsCollect;

import java.util.*;
import java.util.stream.Collectors;

class FsHttpContent {
    private @Nullable Map<String, Object> headers;

    /**
     * Returns headers.
     * <p>
     * Returned object is immutable, and type of its value is {@link String} or {@link List}&lt;{@link String}>.
     * <p>
     * If a value is instance of {@link Collection}, it will be considered as repeated header.
     * All values (and elements if the value is a collection) will be converted to string
     * by {@link String#valueOf(Object)} when put into actual http headers.
     */
    public Map<String, Object> getHeaders() {
        if (headers == null) {
            return Collections.emptyMap();
        }
        return FsCollect.immutableMap(headers);
    }

    /**
     * Adds header.
     * <p>
     * If the value is instance of {@link Collection}, it will be considered as repeated header and converts to
     * {@link List}&lt;{@link String}> by {@link String#valueOf(Object)} for each element,
     * then add into the associated key.
     *
     * @param key   header key
     * @param value header value
     */
    public void addHeader(String key, Object value) {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        if (value instanceof List) {
            List<Object> list = Fs.as(value);
            if (list.size() == 1) {
                headers.put(key, String.valueOf(list.get(0)));
            } else {
                List<String> newValues = list.stream().map(String::valueOf).collect(Collectors.toList());
                LinkedList<String> listValue = Fs.as(headers.computeIfAbsent(key, k -> new LinkedList<>()));
                listValue.addAll(newValues);
            }
        }
        if (value instanceof Collection) {
            Collection<Object> collection = Fs.as(value);
            if (collection.size() == 1) {
                headers.put(key, String.valueOf(collection.iterator().next()));
            } else {
                List<String> newValues = collection.stream().map(String::valueOf).collect(Collectors.toList());
                LinkedList<String> listValue = Fs.as(headers.computeIfAbsent(key, k -> new LinkedList<>()));
                listValue.addAll(newValues);
            }
        } else {
            headers.put(key, String.valueOf(value));
        }
    }

    /**
     * Adds headers.
     * <p>
     * If a value is instance of {@link Collection}, it will be considered as repeated header and converts to
     * {@link List}&lt;{@link String}> by {@link String#valueOf(Object)} for each element,
     * then add into the associated key.
     *
     * @param headers headers to be added
     */
    public void addHeaders(Map<String, ?> headers) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        headers.forEach(this::addHeader);
    }
}
