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
import java.util.function.Supplier;

@MappedMetaClass(value = SuccessionAsUsage.class)
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

	@Override
	protected Supplier<EmptyCore> getCoreFactory() {
		return ()->new EmptyCore(sysmlElement, instance);
	}

	protected void mapTargets(){
		targets.add(instance.mapChain(List.of(sysmlElement.getSourceFeature()), this, ActionMapUsage.class));
		for (Feature target : sysmlElement.getTargetFeature()) {
			targets.add(instance.mapChain(List.of(target), this, ActionMapUsage.class));
		}
	}
}
