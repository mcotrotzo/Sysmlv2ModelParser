package Model.Predefined.MetaClasses.Expression;


import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import org.omg.sysml.lang.sysml.LiteralBoolean;
import org.omg.sysml.lang.sysml.LiteralInteger;
@MappedMetaClass(value = LiteralInteger.class)
public class LiteralIntegerUsage extends LiteralUsage<Integer> {
	public LiteralIntegerUsage(LiteralInteger sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
		this.value = sysmlElement.getValue();
	}
}
