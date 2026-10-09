package Model.Predefined.MetaClasses.Function;


import Mapper.Mapper;
import Model.Predefined.MetaClasses.Action.ActionCore;
import Model.Annotation.MappedMetaClass;
import org.omg.sysml.lang.sysml.Behavior;
import org.omg.sysml.lang.sysml.CalculationDefinition;

import java.util.function.Supplier;

@MappedMetaClass(value = CalculationDefinition.class)
public class CustomCalculationDefinition extends FunctionDefinition<ActionCore> {
	public CustomCalculationDefinition(Behavior sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
	}

	@Override
	protected Supplier<ActionCore> getCoreFactory() {
		return () -> new ActionCore((CalculationDefinition) sysmlElement, instance);
	}
}
