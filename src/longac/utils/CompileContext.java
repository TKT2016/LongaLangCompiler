package longac.utils;

import longac.diagnostics.SimpleLog;
import longac.symbols.ProjectSymbol;

import java.util.*;
//CompileGlobalContext
public class CompileContext {

    public int errors = 0;
    public int warnings = 0;

    public SimpleLog log = new SimpleLog(this);

    public ProjectSymbol projectSymbol ;

    /** The client creates an instance of this class for each key.
     */
    public static class Key<T> {
        // note: we inherit identity equality from Object.
    }

    /**
     * The client can register a factory for lazy creation of the
     * instance.
     */
    public static interface Factory<T> {
        T make(CompileContext c);
    }

    /**
     * The underlying map storing the data.
     * We maintain the invariant that this table contains only
     * mappings of the form
     * {@literal Key<T> -> T }
     * or
     * {@literal Key<T> -> Factory<T> }
     */
    protected final Map<Key<?>,Object> ht = new HashMap<>();

    /** Set the factory for the key in this context. */
    public <T> void put(Key<T> key, Factory<T> fac) {
        checkState(ht);
        Object old = ht.put(key, fac);
        if (old != null)
            throw new AssertionError("duplicate context value");
        checkState(ft);
        ft.put(key, fac); // cannot be duplicate if unique in ht
    }

    /** Set the value for the key in this context. */
    public <T> void put(Key<T> key, T data) {
        if (data instanceof Factory<?>)
            throw new AssertionError("T extends Context.Factory");
        checkState(ht);
        Object old = ht.put(key, data);
        if (old != null && !(old instanceof Factory<?>) && old != data && data != null)
            throw new AssertionError("duplicate context value");
    }

    /** Get the value for the key in this context. */
    public <T> T get(Key<T> key) {
        checkState(ht);
        Object o = ht.get(key);
        if (o instanceof Factory<?>) {
            Factory<?> fac = (Factory<?>)o;
            o = fac.make(this);
            if (o instanceof Factory<?>)
                throw new AssertionError("T extends Context.Factory");
            Assert.check(ht.get(key) == o);
        }

        /* The following cast can't fail unless there was
         * cheating elsewhere, because of the invariant on ht.
         * Since we found a key of type Key<T>, the value must
         * be of type T.
         */
        return CompileContext.uncheckedCast(o);
    }

    public CompileContext() {}

    /**
     * The table of preregistered factories.
     */
    private final Map<Key<?>,Factory<?>> ft = new HashMap<>();

    /*
     * The key table, providing a unique Key<T> for each Class<T>.
     */
    private final Map<Class<?>, Key<?>> kt = new HashMap<>();

    protected <T> Key<T> key(Class<T> clss) {
        checkState(kt);
        Key<T> k = uncheckedCast(kt.get(clss));
        if (k == null) {
            k = new Key<>();
            kt.put(clss, k);
        }
        return k;
    }

    public <T> T get(Class<T> clazz) {
        return get(key(clazz));
    }

    public <T> void put(Class<T> clazz, T data) {
        put(key(clazz), data);
    }
    public <T> void put(Class<T> clazz, Factory<T> fac) {
        put(key(clazz), fac);
    }

    /**
     * TODO: This method should be removed and Context should be made type safe.
     * This can be accomplished by using class literals as type tokens.
     */
    @SuppressWarnings("unchecked")
    private static <T> T uncheckedCast(Object o) {
        return (T)o;
    }
/*
    public void dump() {
        for (Object value : ht.values())
            System.err.println(value == null ? null : value.getClass());
    }*/

    private static void checkState(Map<?,?> t) {
        if (t == null)
            throw new IllegalStateException();
    }
}
