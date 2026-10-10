package Model.Predefined.MetaClasses.Action;

import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import Model.Definition;
import Model.EmptyCore;
import Model.Usage;
import lombok.Getter;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;

import java.util.ArrayList;
import java.util.List;

@MappedMetaClass(value = SuccessionAsUsage.class, core = EmptyCore.class)
public class SuccessionMapUsage extends Usage<EmptyCore, SuccessionAsUsage, Definition<?,?>> {

	@Getter private List<ActionMapUsage<?, ?, ?>> targets = new ArrayList<>();
	public SuccessionMapUsage(SuccessionAsUsage sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
	}


	@Override
	public void fillSlots() {
		super.fillSlots();
		mapTargets();
	}

	protected void mapTargets(){
		targets.add(instance.mapChain(List.of(sysmlElement.getSourceFeature()), this, ActionMapUsage.class));
		for (Feature target : sysmlElement.getTargetFeature()) {
			targets.add(instance.mapChain(List.of(target), this, ActionMapUsage.class));
		}
	}
}
