package de.caluga.morphium.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: Stephan Bösebeck
 * Date: 20.06.12
 * Time: 10:16
 * <p/>
 * define the indices to be ensured when the corresponding collection is created
 * when morphium.ensureIndicesFor
 * can be used with a field like:
 * <code>
 *
 * @Entity
 * @Index pbulic class MyClass {
 * @Index private String myField;
 * @Index(decrement=true) private String timestamp;
 * ....
 * }
 * </code>
 * or, if necessary, at the class level, defining combined indices
 * unfortunately, the indices have to be specified each as a string
 * <code>
 * @Entity
 * @Index({"-timestamp,name","timestamp,-name"}) public class MyClass {
 * private long timestamp;
 * private String name;
 * }
 * </code>
 * As usual in Morphium, these strings can either be the variable name or the name of the field in MongoDB, or an alias
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    boolean decrement() default false;

    String[] value() default {};
}
