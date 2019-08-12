package com.horizon.contentframe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/*By Horizon*/
public interface ReferenceDisposer {
    default void dispose(){
        try {
            Class<?> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                //Ignore Primitive types
                if(field.getType().isPrimitive()) continue;

                //Ignore Special Modifiers
                int modifiers = field.getModifiers();
                if (Modifier.isTransient(modifiers) || //Consider later => use in serialize
                    Modifier.isFinal(modifiers) ||     //Ignore => final can't set value
                    Modifier.isVolatile(modifiers) ||  //Consider later => notify change to multi-threads
                    Modifier.isStatic(modifiers))
                    continue;

                //Accept Object Only
                field.setAccessible(true);
                field.set(this, null);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
