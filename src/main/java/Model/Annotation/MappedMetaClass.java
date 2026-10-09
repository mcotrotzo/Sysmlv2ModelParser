package Model.Annotation;


import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface MappedMetaClass {
	Class<? extends Type> value();
}
