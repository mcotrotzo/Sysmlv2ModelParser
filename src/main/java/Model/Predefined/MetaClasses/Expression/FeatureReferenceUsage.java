package Model.Predefined.MetaClasses.Expression;


import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import Model.EmptyCore;
import Model.Usage;
import org.omg.sysml.lang.sysml.FeatureReferenceExpression;

import java.util.List;

@MappedMetaClass(value = FeatureReferenceExpression.class, core = EmptyCore.class)
public class FeatureReferenceUsage extends ReferenceUsage<FeatureReferenceExpression> {



	public FeatureReferenceUsage(FeatureReferenceExpression sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
	}

	@Override
	public void fillSlots() {
		super.fillSlots();
		mapTarget();
	}

	protected void mapTarget() {
		target = instance.mapChain(List.of(sysmlElement.getReferent()), this, Usage.class);
	}

}
