package Main;

import Executor.Executor;
import Mapper.Mapper;
import Mapper.NewUtil;
import Mapper.ResourceContainer;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsData;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;
import Executor.DefaultRuleExecutor;
import Executor.SemanticException;
import Mapper.Scanner;
import org.omg.sysml.interactive.SysMLInteractive;

import java.io.IOException;
import java.util.*;

public class SysmlConverterMain {

	private static final String STANDARD_LIBRARY_NAME= "sysml_library";
	private static final String DT_L_NAME = "DTLibrary";
	private String DT_PATH;
	private final Map<URI, Resource> uriToResourceMap = new HashMap<>();
	private SysMLInteractive sysMLInteractive;
	private ResultConverter result;
	private Collection<Resource> libraryResources;
	protected Scanner scanner;
	private Executor executor = new DefaultRuleExecutor();
	protected NewUtil newUtil;

	public SysmlConverterMain(Executor executor) throws IOException {
		this.executor = executor;
		init();
	}

	public SysmlConverterMain() throws IOException {
		init();
	}

	private void init() throws IOException {
		initSysMLInteractive();
		initStandardLibrary();
		initDTLibrary();
		newUtil = new NewUtil(new ResourceContainer(libraryResources, sysMLInteractive.getInputResources()));
		scanner = new Scanner(newUtil);
	}

	private void initSysMLInteractive() throws IOException {
		sysMLInteractive = SysMLInteractive.createInstance();
	}
	private void initStandardLibrary() throws IOException {
		sysMLInteractive.loadLibrary(ReadManagerTwo.extractStandardLibrary(STANDARD_LIBRARY_NAME).toString());
	}

	private void initDTLibrary() throws IOException {
		DT_PATH = ReadManagerTwo.extractStandardLibrary(DT_L_NAME).toString();
		loadDTLibrary();
	}

	public synchronized ResultConverter parse(String ... path) throws SemanticException {
		removeAllInputResources();
		parseUserResources(path);
		map();
		return result;
	}

	private void parseUserResources(String[] path) {
		for (String p : path) {
			List<Resource> resources = ReadManagerTwo.readAllInput(sysMLInteractive,p,".sysml");
			for (Resource resource : resources) {
				uriToResourceMap.put(resource.getURI(), resource);
			}

		}
		validateAllAndThrowIfInvalid(uriToResourceMap.values());
	}

	private void map() throws SemanticException {
		newUtil.getResourceContainer().updateUserResources(sysMLInteractive.getInputResources());
		Mapper mapper = getMapper();
		executor.executeGeneralRules(newUtil);
		mapper.parse();
		result = new ResultConverter(new TwinDataBase(mapper.getMapperMap().values()));
		executor.executeSemanticRules(newUtil,result);

	}

	private void loadDTLibrary() {
		libraryResources = List.copyOf(ReadManagerTwo.readAllLibrary(sysMLInteractive, DT_PATH, ".sysml"));
		validateAllAndThrowIfInvalid(libraryResources);
	}


	private void removeAllInputResources() {
		ResourceDescriptionsData index = ResourceDescriptionsData.ResourceSetAdapter
				.findResourceDescriptionsData(sysMLInteractive.getResourceSet());
		for (Resource resource : new ArrayList<>(sysMLInteractive.getInputResources())) {
			index.removeDescription(resource.getURI());
			resource.unload();
			sysMLInteractive.getResourceSet().getResources().remove(resource);
		}
		sysMLInteractive.getInputResources().clear();
		uriToResourceMap.clear();
	}

	private List<Issue> validate(Resource resource) {
		IResourceValidator validator = IResourceServiceProvider.Registry.INSTANCE
				.getResourceServiceProvider(resource.getURI())
				.getResourceValidator();

		return validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
	}

	private List<Issue> validateAll(Collection<Resource> resources) {
		List<Issue> allIssues = new ArrayList<>();
		for (Resource resource : resources) {
			allIssues.addAll(validate(resource));
		}
		return allIssues;
	}
	private void validateAllAndThrowIfInvalid(Collection<Resource> resources) {
		List<Issue> allIssues = validateAll(resources);
		if (!allIssues.isEmpty()) {
			throw new IllegalStateException("Validation failed with issues: " + allIssues);
		}
	}





	protected Mapper getMapper() {
		return new Mapper(scanner, newUtil);
	}



}
