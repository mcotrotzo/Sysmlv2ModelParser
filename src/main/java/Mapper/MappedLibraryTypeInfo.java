package Mapper;

import Model.AbstractType;
import Model.Definition;
import Model.Usage;
import lombok.Getter;
import org.omg.sysml.lang.sysml.Classifier;
import org.omg.sysml.lang.sysml.Feature;
import org.omg.sysml.lang.sysml.Type;
import org.omg.sysml.util.TypeUtil;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;

public class MappedLibraryTypeInfo implements MappedComprable<Type,MappedLibraryTypeInfo>{
	@Getter private NewUtil newUtil;
	@Getter private String libraryName;
	@Getter private Type element;
	@Getter private Optional<Class<? extends Definition<?,?>>> definitionClass =Optional.empty();
	@Getter private Optional<Class<? extends Usage<?,?,?>>> usageClass =Optional.empty();


	public MappedLibraryTypeInfo(NewUtil newUtil,String libraryName) {
		this.newUtil = newUtil;
		this.element = newUtil.getLibrayType(libraryName);
		this.libraryName = libraryName;

	}


	public void setClass(Class<? extends AbstractType<?,?>> definitionClass,boolean override) {
		if(Definition.class.isAssignableFrom(definitionClass)){
			if(this.definitionClass.isPresent() && !override) {
				throw  new RuntimeException("Cannot add definition class");
			}
			this.definitionClass = Optional.of(definitionClass.asSubclass(Definition.class));
		}

		if(Usage.class.isAssignableFrom(definitionClass)){
			if(this.usageClass.isPresent() && !override) {
				throw  new RuntimeException("Cannot add usage class");
			}
			this.usageClass = Optional.of(definitionClass.asSubclass(Usage.class));
		}
		throw new IllegalStateException("Class %s is not a subclass of Definition or Usage".formatted(definitionClass.getName()));

	}

	public void setClass(Class<? extends AbstractType<?,?>> definitionClass) {
		setClass(definitionClass,false);

	}


	@Override
	public boolean specialices(MappedLibraryTypeInfo other) {
		return TypeUtil.specializes(element,other.element);
	}

	@Override
	public Optional<Constructor<?>> getConstructor(Type type,Mapper mapper) {

		if(type instanceof Classifier){
			if(this.definitionClass.isPresent()){
				return Optional.of(findConstructor(definitionClass.get(), type.getClass(),mapper));
			}
		}

		if(type instanceof Feature){
			if(this.usageClass.isPresent()){
				return Optional.of(findConstructor(usageClass.get(), type.getClass(),mapper));
			}

		}

		return Optional.empty();
	}

	@Override
	public boolean isSpecilizedBy(Type element) {
		return TypeUtil.specializes(element,this.element);
	}


}
