package Model.Predefined.MetaClasses.Action;

import Model.Core.CoreApi;
import Model.Usage;

import java.util.List;

public interface ActionCoreApi<C extends ActionCore> extends CoreApi<C> {

	default List<Usage<?, ?,?>> getInputs() { return getCore().getInputs(); }
	default List<Usage<?, ?,?>> getOutputs() { return getCore().getOutputs(); }
	default List<ActionMapUsage<?, ?, ?>> getActions() { return getCore().getActions(); }
	default List<SuccessionMapUsage> getSuccessions() { return getCore().getSuccessions(); }
}
