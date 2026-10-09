package Model.Predefined.MetaClasses.Expression;


import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import org.omg.sysml.lang.sysml.LiteralBoolean;
import org.omg.sysml.lang.sysml.LiteralString;
@MappedMetaClass(value = LiteralString.class)
public class LiteralStringUsage extends LiteralUsage<String> {
	public LiteralStringUsage(LiteralString sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
		this.value = sysmlElement.getValue();
	}
}
