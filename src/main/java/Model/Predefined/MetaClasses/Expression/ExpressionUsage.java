package Model.Predefined.MetaClasses.Expression;


import Mapper.Mapper;
import Model.Definition;
import Model.EmptyCore;
import Model.Usage;
import org.omg.sysml.lang.sysml.Expression;

import java.util.function.Supplier;


public abstract class ExpressionUsage<T extends Expression> extends Usage<EmptyCore, T, Definition<?,?>> {
	protected ExpressionUsage(T sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
	}


	@Override
	protected Supplier<EmptyCore> getCoreFactory() {
		return ()-> new EmptyCore(sysmlElement, instance);
	}
}
