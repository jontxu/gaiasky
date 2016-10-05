package gaia.cu9.ari.gaiaorbit.util.concurrent;

import java.util.Map;

import gaia.cu9.ari.gaiaorbit.util.Logger;

public abstract class ThreadLocalFactory {

    public static ThreadLocalFactory instance;

    protected Map<Class, Class<? extends IThreadLocal>> map;

    public static void initialize(ThreadLocalFactory fac) {
        instance = fac;
    }

    public IThreadLocal get(Class clazz) {
        try {
            return map.get(clazz).newInstance();
        } catch (Exception e) {
            Logger.error(e);
            return null;
        }
    }
}
