package Model.Predefined.MetaClasses.Function;


import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import org.omg.sysml.lang.sysml.Behavior;
import org.omg.sysml.lang.sysml.Function;

import java.util.function.Supplier;

@MappedMetaClass(value = Function.class)
public class BaseFunctionDefinition extends FunctionDefinition<FunctionCore> {
	public BaseFunctionDefinition(Behavior sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
	}

	@Override
	protected Supplier<FunctionCore> getCoreFactory() {
		return () -> new FunctionCore(getSysmlElement(), instance);
	}
}
