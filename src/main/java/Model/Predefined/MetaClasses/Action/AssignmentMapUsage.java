package Model.Predefined.MetaClasses.Action;

import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import Model.Predefined.MetaClasses.Expression.ExpressionUsage;
import Model.Slots;
import Model.Usage;
import lombok.Getter;
import org.omg.sysml.lang.sysml.*;


import java.util.ArrayList;
import java.util.List;

@MappedMetaClass(value = AssignmentActionUsage.class, core = EmptyActionCore.class)
public class AssignmentMapUsage extends ActionMapUsage<EmptyActionCore,AssignmentActionUsage,ActionMapDefinition<EmptyActionCore>> {

	@Getter
	private Usage<?, ?, ?> referent;
	@Getter
	private ExpressionUsage<?> value;

	public AssignmentMapUsage(AssignmentActionUsage sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
	}

	@Override
	public void fillSlots() {
		super.fillSlots();
		mapReferent();
		mapValue();

	}


	public void mapReferent() {
		Expression ta = sysmlElement.getTargetArgument();
		List<Feature> chain = new ArrayList<>();
		if (ta instanceof FeatureReferenceExpression r) {
			chain.add(r.getReferent());
		} else if (ta instanceof FeatureChainExpression c) {
			if (c.getArgument().getFirst() instanceof FeatureReferenceExpression base) {
				chain.add(base.getReferent());
			}
			Feature t = c.getTargetFeature();
			chain.addAll(t.getChainingFeature().isEmpty() ? List.of(t) : t.getChainingFeature());
		}
		Feature ref = sysmlElement.getReferent();
		chain.addAll(ref.getChainingFeature().isEmpty() ? List.of(ref) : ref.getChainingFeature());
		referent = instance.mapChain(chain, this, Usage.class);
	}
	public void mapValue()
	{
		value = Slots.mapAll(instance, this, List.of(sysmlElement.getValueExpression()), ExpressionUsage.class).getFirst();

	}
}
