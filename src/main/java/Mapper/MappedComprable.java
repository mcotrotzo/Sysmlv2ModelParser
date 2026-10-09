package Mapper;

import Model.AbstractType;
import org.omg.sysml.lang.sysml.Type;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;

public interface MappedComprable<T extends  Type,C extends MappedComprable<?,C>> {


	boolean specialices(C other);


	Optional<Constructor<?>> getConstructor(Type type,Mapper mapper);


	default Constructor<?> findConstructor(Class<? extends AbstractType<?,?>> aClass, Class<? extends Type> aClass1, Mapper mapper) {

		var constructorsTwo = Arrays.stream(aClass.getConstructors()).filter(x -> x.getParameterCount()==2);
		var validParam = constructorsTwo.filter(
				x -> x.getParameterTypes()[0].isAssignableFrom(aClass1) && x.getParameterTypes()[1].isInstance(mapper)

		).toList();

		if (validParam.size() != 1) {
			throw new IllegalStateException("Expected exactly one valid constructor in class %s for type %s, but found %d matching constructors".formatted(aClass.getName(), aClass1.getName(), validParam.size()));
		}

		return validParam.get(0);
	}

	boolean isSpecilizedBy(Type element);
}
