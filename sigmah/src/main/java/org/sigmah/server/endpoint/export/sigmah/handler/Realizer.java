package org.sigmah.server.endpoint.export.sigmah.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.collection.PersistentBag;

/**
 * Creates plain objects from Hibernate proxies.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
class Realizer {

    private final static Log LOG = LogFactory.getLog(Realizer.class);

    private Realizer() {
    }

    /**
     * Creates a new instance of <code>object</code> with <code>ArrayList</code>s instead of
     * <code>PersistentBag</code>s.
     * <br>
     * <b>Note:</b> do not use this without testing its compatibility with your objects.
     * @param <T>
     * @param object An hibernate proxy.
     * @return
     */
    public static <T> T realize(T object) {
        return realize(object, new HashMap<Object, Object>());
    }

    private static <T> T realize(T object, Map<Object, Object> alreadyRealizedObjects) {
        T result = null;

        if (object != null) {

            // If the given object has already been instantiated, no need to instantiate it again
            if (alreadyRealizedObjects.containsKey(object)) {
                return (T) alreadyRealizedObjects.get(object);
            }

            try {
                // Extracting the class of the current object
                final Class<T> clazz = (Class<T>) object.getClass();
                final Constructor<T> emptyConstructor = clazz.getConstructor();

                // Creating a new instance of the current object
                // REM: this will crash if the object doesn't have an empty constructor
                final T instance = emptyConstructor.newInstance();
                alreadyRealizedObjects.put(object, instance);

                // Accessing fields from the given object
                final Field[] fields = clazz.getDeclaredFields();
                for (final Field field : fields) {
                    if (!Modifier.isStatic(field.getModifiers())) { // Avoid trying to modify static fields
                        field.setAccessible(true); // Force the accessibility of the current field

                        final Object sourceValue = field.get(object);
                        final Object destinationValue;

                        if (sourceValue == null) {
                            destinationValue = null;

                        } else if (sourceValue instanceof PersistentBag) {
                            // Turning persistent bags into lists

                            final ArrayList<Object> list = new ArrayList<Object>();

                            for (Object value : (PersistentBag) sourceValue) {
                                list.add(realize(value, alreadyRealizedObjects));
                            }

                            destinationValue = list;

                        } else if (sourceValue.getClass().getName().startsWith("java.lang")
                                || sourceValue.getClass().isEnum()) {
                            // Simple copy if the current field is a primitive type or an enum
                            destinationValue = sourceValue;

                        } else {
                            destinationValue = realize(sourceValue, alreadyRealizedObjects);
                        }

                        // Setting the field of the new object
                        field.set(instance, destinationValue);
                    }
                }

                result = instance;

            } catch (Exception e) {
                LOG.debug("An error occured while realizing " + object, e);
            }

        }

        return result;
    }
}
