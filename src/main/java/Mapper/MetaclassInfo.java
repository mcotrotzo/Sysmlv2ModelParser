package Mapper;

import Model.AbstractType;
import Model.Core.Core;
import lombok.Getter;
import org.omg.sysml.lang.sysml.Type;

public class MetaclassInfo<T extends Type> implements MappedComprable<T, MetaclassInfo<?>> {
	@Getter private final Class<T> metaclass;
	@Getter private Class<? extends AbstractType<?, ?>> mappedClass;
	@Getter private Class<? extends Core<?>> core;

	public MetaclassInfo(Class<T> metaclass) {
		this.metaclass = metaclass;
	}

	@Override
	public boolean specialices(MetaclassInfo<?> other) {
		return metaclass.isAssignableFrom(other.getMetaclass());
	}

	@Override
	public boolean isSpecilizedBy(Type element) {
		return metaclass.isInstance(element);
	}

	@Override
	public boolean canCreate(Type element) {
		return mappedClass != null;
	}

	@Override
	public AbstractType<?, ?> create(Type element, Mapper mapper) {
		return instantiate(mappedClass, core, element, mapper);
	}

	public void setClass(Class<? extends AbstractType<?, ?>> aClass, Class<? extends Core<?>> core) {
		if (mappedClass != null) {
			throw new IllegalStateException("Metaclass %s is already mapped to %s".formatted(metaclass.getSimpleName(), mappedClass.getName()));
		}
		this.mappedClass = aClass;
		this.core = core;
	}
}
