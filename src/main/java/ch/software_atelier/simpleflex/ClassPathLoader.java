package ch.software_atelier.simpleflex;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClassPathLoader {
    static Logger LOG = LogManager.getLogger(ClassPathLoader.class);

    private static final Class<?>[] parameters = new Class[] {URL.class};

    public static void addFile(String s) throws IOException
    {
        File f = new File(s);
        addFile(f);
    }

    public static void addFile(File f) throws IOException
    {
        addURL(f.toURI().toURL());
    }

    public static void addURL(URL u) throws IOException
    {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] {u});
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException t) {
            LOG.error("could not add URL to system classloader",t);
            throw new IOException("Error, could not add URL to system classloader");
        }

    }
}
