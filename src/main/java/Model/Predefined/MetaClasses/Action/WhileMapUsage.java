package Model.Predefined.MetaClasses.Action;

import Mapper.Mapper;
import Model.Annotation.MappedMetaClass;
import Model.Predefined.MetaClasses.Expression.ExpressionUsage;
import Model.Slots;
import lombok.Getter;
import org.omg.sysml.lang.sysml.WhileLoopActionUsage;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@MappedMetaClass(value = WhileLoopActionUsage.class)
public class WhileMapUsage extends ActionMapUsage<EmptyActionCore, WhileLoopActionUsage, ActionMapDefinition<EmptyActionCore>> {

	@Getter private Optional<ExpressionUsage<?>> condition = Optional.empty();
	@Getter private Optional<ExpressionUsage<?>> until = Optional.empty();
	@Getter private Optional<ActionMapUsage<?, ?, ?>> body = Optional.empty();

	public WhileMapUsage(WhileLoopActionUsage sysmlElement, Mapper mapper) {
		super(sysmlElement,  mapper);
	}

	@Override
	public void fillSlots() {
		super.fillSlots();
		mapCondition();
		mapUntil();
		mapBody();
	}

	protected void mapCondition() {
		condition = Slots.mapAll(instance, this, Stream.ofNullable(sysmlElement.getWhileArgument()).toList(), Slots.<ExpressionUsage<?>>rawClassOf(ExpressionUsage.class)).stream().findFirst();

	}

	protected void mapBody() {
		body = Slots.mapAll(instance, this, Stream.ofNullable(sysmlElement.getBodyAction()).toList(), Slots.<ActionMapUsage<?, ?, ?>>rawClassOf(ActionMapUsage.class)).stream().findFirst();

	}

	protected void mapUntil() {
		until = Slots.mapAll(instance, this, Stream.ofNullable(sysmlElement.getUntilArgument()).toList(), Slots.<ExpressionUsage<?>>rawClassOf(ExpressionUsage.class)).stream().findFirst();

	}

	@Override
	protected Supplier<EmptyActionCore> getCoreFactory() {
		return () -> new EmptyActionCore(sysmlElement, instance);
	}
}
