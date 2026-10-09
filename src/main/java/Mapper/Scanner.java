package Mapper;

import Model.AbstractType;
import Model.Annotation.MappedLibrary;

import Model.Annotation.MappedMetaClass;
import io.github.classgraph.AnnotationClassRef;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.omg.sysml.lang.sysml.Type;

import java.lang.reflect.Constructor;
import java.util.*;

@Slf4j
public class Scanner {

	private final NewUtil newUtil;




	private static final List<?> anntations = List.of(MappedMetaClass.class.getName(), MappedLibrary.class.getName());

	@Getter
	private Map<String ,MappedLibraryTypeInfo> standardinfos = new HashMap<>();
	@Getter private Map<Class<? extends Type>,MetaclassInfo<?>> metaclassinfos = new HashMap<>();


	private MappedSort<MappedLibraryTypeInfo> mappedLibraryTypeInfos;
	private MappedSort<MetaclassInfo<?>> metaclassInfo;

	public Scanner(NewUtil newUtil) {
		this.newUtil = newUtil;
		scan();
	}


	private void scan() {
		try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {


			List<ClassInfo> infos = scanResult.getAllClasses().stream()
					.filter(info -> !info.isAbstract())
					.filter(this::hasAnnotation)
					.distinct()
					.toList();
			ScanMappedResult standardResult = new ScanMappedResult();
			ScanMappedResult userResult = new ScanMappedResult();

			for (ClassInfo classInfo : infos) {
				if(classInfo.getPackageName().equals("Model.Predefined") || classInfo.getPackageName().startsWith("Model.Predefined.")) {
					handle(classInfo, standardResult);
				}
				else {
					handle(classInfo, userResult);
				}
			}

			standardinfos = standardResult.getStandardinfos();
			metaclassinfos = standardResult.getMetaclassinfos();

			for(var s: userResult.getStandardinfos().entrySet()){
				if(!standardinfos.containsKey(s.getKey())){
					standardinfos.put(s.getKey(), s.getValue());
					continue;
				}
				if(s.getValue().getDefinitionClass().isPresent()){
					standardinfos.get(s.getKey()).setClass(s.getValue().getDefinitionClass().get(),true);
					log.info("Overriding definition class for library {} with {}", s.getKey(), s.getValue().getDefinitionClass().get().getName());
				}

				if(s.getValue().getUsageClass().isPresent()){
					standardinfos.get(s.getKey()).setClass(s.getValue().getUsageClass().get(),true);
					log.info("Overriding usage class for library {} with {}", s.getKey(), s.getValue().getUsageClass().get().getName());

				}


			}
			metaclassinfos.putAll(userResult.getMetaclassinfos());

			metaclassInfo = new MappedSort<MetaclassInfo<?>>(metaclassinfos.values());
			mappedLibraryTypeInfos = new MappedSort<MappedLibraryTypeInfo>(standardinfos.values());

			metaclassInfo.sort();
			mappedLibraryTypeInfos.sort();


		}
	}

	private boolean hasAnnotation(ClassInfo classInfo) {
		int hit=0;
		for (Object annotation : anntations) {
			if(classInfo.hasAnnotation(annotation.toString())){
				hit++;
			}
		}
		if(hit==0){
			return false;
		}
		if(hit>1){
			throw new IllegalStateException("Class %s has more than one annotation: %s".formatted(classInfo.getName(), anntations));
		}

		if(!AbstractType.class.isAssignableFrom(classInfo.loadClass())){
			throw new IllegalStateException("Class %s is not a subclass of AbstractType".formatted(classInfo.getName()));
		}

		return  hit ==1;
	}






	public void handle(ClassInfo classInfo,ScanMappedResult c) {
		addMetaClass(classInfo,c);
	}

	private void addMetaClass(ClassInfo classInfo, ScanMappedResult scanMappedResult) {
		if(classInfo.hasAnnotation(MappedMetaClass.class)){

			var s = classInfo.getAnnotationInfo(MappedMetaClass.class);
			var w = s.getParameterValues();
			if(w.size() != 1 || !w.get(0).getName().equals("value")){
				throw new IllegalStateException("Class %s has MappedMetaClass annotation but no parameters".formatted(classInfo.getName()));
			}


			MetaclassInfo metaclassInfo = new MetaclassInfo(getCastedClass(w.getFirst().getValue()));
			if(scanMappedResult.getMetaclassinfos().containsKey(metaclassInfo.getMetaclass())){
				throw new IllegalStateException("Duplicate metaclass mapping for %s: %s and %s".formatted(metaclassInfo.getMetaclass().getName(), scanMappedResult.getMetaclassinfos().get(metaclassInfo.getMetaclass()).getMappedClass().getSimpleName(), classInfo.getName()));
			}
			scanMappedResult.getMetaclassinfos().put(metaclassInfo.getMetaclass(), metaclassInfo);

			if(AbstractType.class.isAssignableFrom(classInfo.loadClass())){
				metaclassInfo.setClass((Class<? extends AbstractType<?, ?>>) classInfo.loadClass());
			}

			log.info("Found metaclass mapping: {} -> {}", classInfo.getName(), metaclassInfo.getMetaclass().getName());
		}

		if(classInfo.hasAnnotation(MappedLibrary.class)){
			var s = classInfo.getAnnotationInfo(MappedLibrary.class);
			var w = s.getParameterValues();
			if(w.size() != 1 || !w.get(0).getName().equals("libraryName")){
				throw new IllegalStateException("Class %s has MappedLibraryInfo annotation but no parameters".formatted(classInfo.getName()));
			}

			if(!scanMappedResult.getStandardinfos().containsKey(w.get(0).getValue())) {
				MappedLibraryTypeInfo mappedLibraryInfo = new MappedLibraryTypeInfo(newUtil,(String) w.getFirst().getValue());
				scanMappedResult.getStandardinfos().put(w.get(0).getValue().toString(), mappedLibraryInfo);
			}

			MappedLibraryTypeInfo mappedLibraryInfo = scanMappedResult.getStandardinfos().get(w.get(0).getValue().toString());

			if(AbstractType.class.isAssignableFrom(classInfo.loadClass())){
				mappedLibraryInfo.setClass((Class<? extends AbstractType<?, ?>>) classInfo.loadClass());
			}
			log.info("Found library mapping: {} -> {}", classInfo.getName(), mappedLibraryInfo.getLibraryName());
		}


	}




	private boolean isOfClass(Object type){
		return type instanceof AnnotationClassRef clazz && Type.class.isAssignableFrom(clazz.loadClass());
	}

	private Class<? extends Type> getCastedClass(Object type) {
		if(!isOfClass(type)){
			throw new IllegalStateException("Class %s is not a subclass of Element".formatted(type.getClass().getName()));
		}
		return ((AnnotationClassRef) type).loadClass().asSubclass(Type.class);
	}


	public <T extends AbstractType<?,?>,C extends Type> Optional<T> getMappedClass(C element,Mapper mapper,Class<T>clazz) {

		Optional<MappedLibraryTypeInfo> result = mappedLibraryTypeInfos.get(element);

		try {
			Optional<Constructor<?>> constructor = result.flatMap(x -> x.getConstructor(element,mapper)).or(() -> metaclassInfo.get(element).flatMap(x -> x.getConstructor(element,mapper)));
			if (constructor.isEmpty()) return Optional.empty();

			return Optional.of(clazz.cast(constructor.get().newInstance(element, mapper)));
		}

		catch (Exception e) {

			throw new IllegalStateException("Failed to create mapped class for element %s of type %s".formatted(element.getName(), element.getClass().getName()), e);
		}

	}

	public Optional<AbstractType<?, ?>> getMappedClass(Type element,Mapper mapper) {

		return getMappedClass(element,mapper,(Class<AbstractType<?, ?>>) (Class<?>) AbstractType.class);
	}


}
