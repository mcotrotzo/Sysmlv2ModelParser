package Model.Predefined.MetaClasses.Expression;


import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import org.omg.sysml.lang.sysml.LiteralBoolean;
import org.omg.sysml.lang.sysml.LiteralRational;
@MappedMetaClass(value = LiteralRational.class)
public class LiteralRealUsage extends LiteralUsage<Double> {
	public LiteralRealUsage(LiteralRational sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
		this.value = sysmlElement.getValue();
	}
}
