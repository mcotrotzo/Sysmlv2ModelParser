package Model.Predefined.MetaClasses.Function;


import Mapper.Mapper;
import Model.Predefined.MetaClasses.Action.ActionCore;
import Model.Annotation.MappedMetaClass;
import org.omg.sysml.lang.sysml.Behavior;
import org.omg.sysml.lang.sysml.CalculationDefinition;


@MappedMetaClass(value = CalculationDefinition.class, core = ActionCore.class)
public class CustomCalculationDefinition extends FunctionDefinition<ActionCore> {
	public CustomCalculationDefinition(Behavior sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
	}
}
