package Model.Predefined.MetaClasses.Expression;

import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import Model.Predefined.MetaClasses.Function.FunctionDefinition;
import lombok.Getter;

import org.omg.sysml.lang.sysml.InvocationExpression;

@MappedMetaClass(value = InvocationExpression.class)
public class CalculationUsage extends InvocationUsage<InvocationExpression> {

	@Getter private FunctionDefinition<?> invokeType;

	public CalculationUsage(InvocationExpression sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
	}

	@Override
	public void fillSlots() {
		super.fillSlots();
		mapInvokedType();
	}


	protected void mapInvokedType() {
		invokeType = instance.map(sysmlElement.getInstantiatedType(), null, FunctionDefinition.class);
	}
}
