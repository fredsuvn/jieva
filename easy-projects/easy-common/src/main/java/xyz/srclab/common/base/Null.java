package xyz.srclab.common.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class Null {

    private static final Object OBJECT = new Object();

    private static final String STRING = "";

    private static final Set<?> SET = new NullSet<>();

    private static final List<?> LIST = new NullList<>();

    private static final Collection<?> COLLECTION = SET;

    private static final Map<?, ?> MAP = new NullMap<>();

    private static final Class<?> CLASS = NullClass.class;

    private static final Type TYPE = new NullType();

    private static final Constructor<?> CONSTRUCTOR;

    private static final Method METHOD;

    private static final Field FIELD;

    public static Object asObject() {
        return OBJECT;
    }

    public static String asString() {
        return STRING;
    }

    public static <E> Set<E> asSet() {
        return (Set<E>) SET;
    }

    public static <E> List<E> asList() {
        return (List<E>) LIST;
    }

    public static <E> Collection<E> asCollection() {
        return (Collection<E>) COLLECTION;
    }

    public static <K, V> Map<K, V> asMap() {
        return (Map<K, V>) MAP;
    }

    public static <T> Class<T> asClass() {
        return (Class<T>) CLASS;
    }

    public static Type asType() {
        return TYPE;
    }

    public static Constructor<?> asConstructor() {
        return CONSTRUCTOR;
    }

    public static Method asMethod() {
        return METHOD;
    }

    public static Field asField() {
        return FIELD;
    }

    public static boolean isNull(Object object) {
        return OBJECT == object;
    }

    public static boolean isNull(String string) {
        return STRING == string;
    }

    public static boolean isNull(Set<?> set) {
        return SET == set;
    }

    public static boolean isNull(List<?> list) {
        return LIST == list;
    }

    public static boolean isNull(Collection<?> collection) {
        return COLLECTION == collection;
    }

    public static boolean isNull(Map<?, ?> map) {
        return MAP == map;
    }

    public static boolean isNull(Class<?> cls) {
        return CLASS == cls;
    }

    public static boolean isNull(Type type) {
        return TYPE == type;
    }

    public static boolean isNull(Constructor<?> constructor) {
        return CONSTRUCTOR == constructor;
    }

    public static boolean isNull(Method method) {
        return METHOD == method;
    }

    public static boolean isNull(Field field) {
        return FIELD == field;
    }

    static {
        try {
            CONSTRUCTOR = NullClass.class.getDeclaredConstructor();
            METHOD = NullClass.class.getDeclaredMethod("nullMethod");
            FIELD = NullClass.class.getDeclaredField("nullField");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static final class NullSet<E> extends NullParent implements Set<E> {

        @Override
        public int size() {
            throw new NullPointerException();
        }

        @Override
        public boolean isEmpty() {
            throw new NullPointerException();
        }

        @Override
        public boolean contains(Object o) {
            throw new NullPointerException();
        }

        @Override
        public Iterator<E> iterator() {
            throw new NullPointerException();
        }

        @Override
        public Object[] toArray() {
            throw new NullPointerException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new NullPointerException();
        }

        @Override
        public boolean add(E e) {
            throw new NullPointerException();
        }

        @Override
        public boolean remove(Object o) {
            throw new NullPointerException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new NullPointerException();
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new NullPointerException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new NullPointerException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new NullPointerException();
        }

        @Override
        public void clear() {
            throw new NullPointerException();
        }
    }

    private static final class NullList<E> extends NullParent implements List<E> {

        @Override
        public int size() {
            throw new NullPointerException();
        }

        @Override
        public boolean isEmpty() {
            throw new NullPointerException();
        }

        @Override
        public boolean contains(Object o) {
            throw new NullPointerException();
        }

        @Override
        public Iterator<E> iterator() {
            throw new NullPointerException();
        }

        @Override
        public Object[] toArray() {
            throw new NullPointerException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new NullPointerException();
        }

        @Override
        public boolean add(E e) {
            throw new NullPointerException();
        }

        @Override
        public boolean remove(Object o) {
            throw new NullPointerException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new NullPointerException();
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new NullPointerException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            throw new NullPointerException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new NullPointerException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new NullPointerException();
        }

        @Override
        public void clear() {
            throw new NullPointerException();
        }

        @Override
        public E get(int index) {
            throw new NullPointerException();
        }

        @Override
        public E set(int index, E element) {
            throw new NullPointerException();
        }

        @Override
        public void add(int index, E element) {
            throw new NullPointerException();
        }

        @Override
        public E remove(int index) {
            throw new NullPointerException();
        }

        @Override
        public int indexOf(Object o) {
            throw new NullPointerException();
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new NullPointerException();
        }

        @Override
        public ListIterator<E> listIterator() {
            throw new NullPointerException();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            throw new NullPointerException();
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            throw new NullPointerException();
        }
    }

    private static final class NullMap<K, V> extends NullParent implements Map<K, V> {

        @Override
        public int size() {
            throw new NullPointerException();
        }

        @Override
        public boolean isEmpty() {
            throw new NullPointerException();
        }

        @Override
        public boolean containsKey(Object key) {
            throw new NullPointerException();
        }

        @Override
        public boolean containsValue(Object value) {
            throw new NullPointerException();
        }

        @Override
        public V get(Object key) {
            throw new NullPointerException();
        }

        @Override
        public V put(K key, V value) {
            throw new NullPointerException();
        }

        @Override
        public V remove(Object key) {
            throw new NullPointerException();
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            throw new NullPointerException();
        }

        @Override
        public void clear() {
            throw new NullPointerException();
        }


        @Override
        public Set<K> keySet() {
            throw new NullPointerException();
        }


        @Override
        public Collection<V> values() {
            throw new NullPointerException();
        }


        @Override
        public Set<Entry<K, V>> entrySet() {
            throw new NullPointerException();
        }
    }

    private static final class NullClass extends NullParent {

        private final String nullField = null;

        private NullClass() {
        }

        private void nullMethod() {
        }
    }

    private static final class NullType extends NullParent implements Type {
    }

    private static class NullParent {

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || getClass().equals(obj.getClass());
        }

        @Override
        public String toString() {
            return "null";
        }
    }
}
