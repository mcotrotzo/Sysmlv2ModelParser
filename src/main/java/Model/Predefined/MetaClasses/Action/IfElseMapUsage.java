package Model.Predefined.MetaClasses.Action;

import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import Model.Predefined.MetaClasses.Expression.ExpressionUsage;
import Model.Slots;
import lombok.Getter;

import org.omg.sysml.lang.sysml.IfActionUsage;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@MappedMetaClass(value = IfActionUsage.class)
public class IfElseMapUsage extends ActionMapUsage<EmptyActionCore, IfActionUsage, ActionMapDefinition<EmptyActionCore>> {

	@Getter private Optional<ExpressionUsage<?>> condition = Optional.empty();
	@Getter private Optional<ActionMapUsage<?, ?, ?>> thenAction = Optional.empty();
	@Getter private Optional<ActionMapUsage<?, ?, ?>> elseAction = Optional.empty();

	public IfElseMapUsage(IfActionUsage sysmlElement, Mapper mapper) {
		super(sysmlElement, mapper);
	}

	@Override
	public void fillSlots() {
		super.fillSlots();
		mapCondition();
		mapThenAction();
		mapElseAction();
	}

	@Override
	protected Supplier<EmptyActionCore> getCoreFactory() {
		return () -> new EmptyActionCore(sysmlElement, instance);
	}

	protected void mapCondition() {
		condition = Slots.mapAll(instance, this, Stream.ofNullable(sysmlElement.getIfArgument()).toList(), Slots.<ExpressionUsage<?>>rawClassOf(ExpressionUsage.class)).stream().findFirst();
	}

	protected void mapThenAction() {
		thenAction = Slots.mapAll(instance, this, Stream.ofNullable(sysmlElement.getThenAction()).toList(), Slots.<ActionMapUsage<?, ?, ?>>rawClassOf(ActionMapUsage.class)).stream().findFirst();

	}

	protected void mapElseAction() {
		elseAction = Slots.mapAll(instance, this, Stream.ofNullable(sysmlElement.getElseAction()).toList(), Slots.<ActionMapUsage<?, ?, ?>>rawClassOf(ActionMapUsage.class)).stream().findFirst();

	}
}
