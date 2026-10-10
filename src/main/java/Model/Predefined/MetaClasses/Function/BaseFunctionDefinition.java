package Model.Predefined.MetaClasses.Function;


import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import org.omg.sysml.lang.sysml.Behavior;
import org.omg.sysml.lang.sysml.Function;


@MappedMetaClass(value = Function.class, core = FunctionCore.class)
public class BaseFunctionDefinition extends FunctionDefinition<FunctionCore> {
	public BaseFunctionDefinition(Behavior sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
	}
}
