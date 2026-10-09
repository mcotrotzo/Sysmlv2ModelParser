package Mapper;

import Model.AbstractType;
import lombok.Getter;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Type;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class MetaclassInfo <T extends Type> implements MappedComprable<T,MetaclassInfo<?>> {
	@Getter private Class<T> metaclass;
	@Getter private Class<? extends AbstractType<?,?>>  mappedClass;

	public MetaclassInfo(Class<T> metaclass) {
		this.metaclass = metaclass;
	}

	@Override
	public boolean specialices(MetaclassInfo<?> other) {
		return metaclass.isAssignableFrom(other.getMetaclass());
	}

	@Override
	public Optional<Constructor<?>> getConstructor(Type type,Mapper mapper) {
		return Optional.of(findConstructor(mappedClass,type.getClass(),mapper));
	}



	@Override
	public boolean isSpecilizedBy(Type element) {
		return metaclass.isAssignableFrom(element.getClass());
	}

	public void setClass(Class<? extends AbstractType<?,?>> aClass) {

		if(mappedClass != null) {
			throw new RuntimeException("Cannot add mapped class");
		}
		this.mappedClass = aClass;
	}
}
