package Mapper;

import Model.AbstractType;
import Model.Definition;
import Model.Usage;
import Model.Annotation.MappedLibrary;

import Model.Annotation.MappedMetaClass;
import Model.Core.Core;
import io.github.classgraph.AnnotationClassRef;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.omg.sysml.lang.sysml.Type;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.*;

@Slf4j
public class Scanner {

	private final NewUtil newUtil;




	private static final List<?> anntations = List.of(MappedMetaClass.class.getName(), MappedLibrary.class.getName());

	@Getter
	private Map<String ,MappedLibraryTypeInfo> standardinfos = new HashMap<>();
	@Getter private Map<Class<? extends Type>,MetaclassInfo<?>> metaclassinfos = new HashMap<>();
	private final Map<Class<?>, Class<?>> definitionTypes = new HashMap<>();


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
				MappedLibraryTypeInfo user = s.getValue();
				if(user.getDefinitionClass().isPresent()){
					standardinfos.get(s.getKey()).setClass(user.getDefinitionClass().get(), user.getDefinitionCore(), true);
					log.info("Overriding definition class for library {} with {}", s.getKey(), user.getDefinitionClass().get().getName());
				}
				if(user.getUsageClass().isPresent()){
					standardinfos.get(s.getKey()).setClass(user.getUsageClass().get(), user.getUsageCore(), true);
					log.info("Overriding usage class for library {} with {}", s.getKey(), user.getUsageClass().get().getName());
				}
			}
			metaclassinfos.putAll(userResult.getMetaclassinfos());

			metaclassInfo = new MappedSort<MetaclassInfo<?>>(metaclassinfos.values());
			mappedLibraryTypeInfos = new MappedSort<MappedLibraryTypeInfo>(standardinfos.values());

			metaclassInfo.sort();
			mappedLibraryTypeInfos.sort();
			checkDefinitionTypes();


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
		Class<? extends AbstractType<?, ?>> mappedClass = (Class<? extends AbstractType<?, ?>>) classInfo.loadClass();

		if(classInfo.hasAnnotation(MappedMetaClass.class)){
			AnnotationParameterValueList values = classInfo.getAnnotationInfo(MappedMetaClass.class).getParameterValues();
			MetaclassInfo metaclassInfo = new MetaclassInfo(getCastedClass(values.getValue("value")));
			if(scanMappedResult.getMetaclassinfos().containsKey(metaclassInfo.getMetaclass())){
				throw new IllegalStateException("Duplicate metaclass mapping for %s: %s and %s".formatted(metaclassInfo.getMetaclass().getName(), scanMappedResult.getMetaclassinfos().get(metaclassInfo.getMetaclass()).getMappedClass().getSimpleName(), classInfo.getName()));
			}
			Class<? extends Core<?>> core = getCoreClass(values.getValue("core"), mappedClass);
			metaclassInfo.setClass(mappedClass, core);
			scanMappedResult.getMetaclassinfos().put(metaclassInfo.getMetaclass(), metaclassInfo);
			log.info("Found metaclass mapping: {} -> {} (core {})", classInfo.getName(), metaclassInfo.getMetaclass().getName(), core.getSimpleName());
		}

		if(classInfo.hasAnnotation(MappedLibrary.class)){
			AnnotationParameterValueList values = classInfo.getAnnotationInfo(MappedLibrary.class).getParameterValues();
			String libraryName = values.getValue("libraryName").toString();
			MappedLibraryTypeInfo mappedLibraryInfo = scanMappedResult.getStandardinfos()
					.computeIfAbsent(libraryName, name -> new MappedLibraryTypeInfo(newUtil, name));
			Class<? extends Core<?>> core = getCoreClass(values.getValue("core"), mappedClass);
			mappedLibraryInfo.setClass(mappedClass, core);
			log.info("Found library mapping: {} -> {} (core {})", classInfo.getName(), libraryName, core.getSimpleName());
		}
	}

	private Class<? extends Core<?>> getCoreClass(Object value, Class<?> mappedClass) {
		if (!(value instanceof AnnotationClassRef ref) || !Core.class.isAssignableFrom(ref.loadClass())) {
			throw new IllegalStateException("Class %s: 'core' must name a subclass of Core".formatted(mappedClass.getName()));
		}
		Class<? extends Core<?>> core = (Class<? extends Core<?>>) ref.loadClass();
		if (Modifier.isAbstract(core.getModifiers())) {
			throw new IllegalStateException("Class %s: core %s is abstract".formatted(mappedClass.getName(), core.getName()));
		}
		for (Class<?> required : requiredCoreTypes(mappedClass)) {
			if (!required.isAssignableFrom(core)) {
				throw new IllegalStateException("Class %s: core %s does not fit the core type %s"
						.formatted(mappedClass.getName(), core.getSimpleName(), required.getSimpleName()));
			}
		}
		return core;
	}

	private List<Class<?>> requiredCoreTypes(Class<?> mappedClass) {
		return resolveTypeArgument(mappedClass, AbstractType.class, 1);
	}


	public Class<?> requiredDefinitionType(Class<?> usageClass) {
		return definitionTypes.computeIfAbsent(usageClass, c -> {
			List<Class<?>> types = resolveTypeArgument(c, Usage.class, 2);
			if (types.size() > 1) {
				throw new IllegalStateException("Class %s: the definition type D must have a single bound, found %s".formatted(c.getName(), types));
			}
			return types.isEmpty() ? Definition.class : types.getFirst();
		});
	}


	private List<Class<?>> resolveTypeArgument(Class<?> clazz, Class<?> base, int index) {
		Map<TypeVariable<?>, java.lang.reflect.Type> bindings = new HashMap<>();
		Class<?> current = clazz;
		while (current != null && current != base) {
			java.lang.reflect.Type superType = current.getGenericSuperclass();
			if (superType instanceof ParameterizedType parameterized && parameterized.getRawType() instanceof Class<?> raw) {
				TypeVariable<?>[] variables = raw.getTypeParameters();
				java.lang.reflect.Type[] arguments = parameterized.getActualTypeArguments();
				for (int i = 0; i < variables.length; i++) {
					bindings.put(variables[i], bindings.getOrDefault(arguments[i], arguments[i]));
				}
				current = raw;
			} else {
				current = current.getSuperclass();
			}
		}
		return erasures(bindings.get(base.getTypeParameters()[index]));
	}

	private void checkMetaclassDefinitionTypes() {
		List<Class<?>> definitionClasses = new ArrayList<>();
		standardinfos.values().forEach(info -> info.getDefinitionClass().ifPresent(definitionClasses::add));
		metaclassinfos.values().stream().map(MetaclassInfo::getMappedClass)
				.filter(Definition.class::isAssignableFrom).forEach(definitionClasses::add);

		for (MetaclassInfo<?> info : metaclassinfos.values()) {
			if (!Usage.class.isAssignableFrom(info.getMappedClass())) continue;
			Class<?> required = requiredDefinitionType(info.getMappedClass());
			if (required == Definition.class) continue;
			if (definitionClasses.stream().noneMatch(required::isAssignableFrom)) {
				throw new IllegalStateException("%s expects definition %s, but no mapped definition class is a %s"
						.formatted(info.getMappedClass().getSimpleName(), required.getSimpleName(), required.getSimpleName()));
			}
		}
	}


	private void checkDefinitionTypes() {
		checkMetaclassDefinitionTypes();
		for (MappedLibraryTypeInfo info : standardinfos.values()) {
			if (info.getUsageClass().isEmpty()) continue;
			Optional<MappedLibraryTypeInfo> definitionInfo = mappedLibraryTypeInfos.get(info.getElement());
			if (definitionInfo.isEmpty() || definitionInfo.get().getDefinitionClass().isEmpty()) continue;
			Class<?> definitionClass = definitionInfo.get().getDefinitionClass().get();
			Class<?> required = requiredDefinitionType(info.getUsageClass().get());
			if (!required.isAssignableFrom(definitionClass)) {
				throw new IllegalStateException("%s expects definition %s, but library type %s builds %s"
						.formatted(info.getUsageClass().get().getSimpleName(), required.getSimpleName(), info.getLibraryName(), definitionClass.getSimpleName()));
			}
		}
	}

	private List<Class<?>> erasures(java.lang.reflect.Type type) {
		if (type instanceof Class<?> c) return List.of(c);
		if (type instanceof ParameterizedType p && p.getRawType() instanceof Class<?> raw) return List.of(raw);
		if (type instanceof TypeVariable<?> variable) {
			List<Class<?>> result = new ArrayList<>();
			for (java.lang.reflect.Type bound : variable.getBounds()) result.addAll(erasures(bound));
			return result;
		}
		return List.of();
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

		Optional<AbstractType<?, ?>> created = mappedLibraryTypeInfos.get(element).<AbstractType<?, ?>>map(x -> x.create(element, mapper))
				.or(() -> metaclassInfo.get(element).<AbstractType<?, ?>>map(x -> x.create(element, mapper)));
		return created.map(clazz::cast);
	}

	public Optional<AbstractType<?, ?>> getMappedClass(Type element,Mapper mapper) {

		return getMappedClass(element,mapper,(Class<AbstractType<?, ?>>) (Class<?>) AbstractType.class);
	}


}
