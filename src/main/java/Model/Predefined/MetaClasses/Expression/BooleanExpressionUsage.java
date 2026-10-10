package Model.Predefined.MetaClasses.Expression;


import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import Model.EmptyCore;
import org.omg.sysml.lang.sysml.BooleanExpression;
@MappedMetaClass(value = BooleanExpression.class, core = EmptyCore.class)
public class BooleanExpressionUsage extends ExpressionUsage<BooleanExpression> {
	public BooleanExpressionUsage(BooleanExpression sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
	}
}
