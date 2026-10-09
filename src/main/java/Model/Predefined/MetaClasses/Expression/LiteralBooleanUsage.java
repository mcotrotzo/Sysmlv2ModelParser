package Model.Predefined.MetaClasses.Expression;


import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import org.omg.sysml.lang.sysml.LiteralBoolean;

@MappedMetaClass(value = LiteralBoolean.class)
public class LiteralBooleanUsage extends LiteralUsage<Boolean> {
	public LiteralBooleanUsage(LiteralBoolean sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
		this.value = sysmlElement.isValue();
	}



}
