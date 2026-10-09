package Main;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.internal.compiler.ReadManager;
import org.omg.sysml.interactive.SysMLInteractive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class ReadManagerTwo {

	public static Path extractStandardLibrary(String ressourceName) throws IOException {
		Path tempDirectory = Files.createTempDirectory(ressourceName + "_temp");
		try (var input = ReadManager.class.getClassLoader().getResourceAsStream(ressourceName + ".zip")) {
			if (input == null) {
				throw new IllegalStateException("Bundled SysML standard library not found.");
			}
			try (var zip = new java.util.zip.ZipInputStream(input)) {
				java.util.zip.ZipEntry entry;
				while ((entry = zip.getNextEntry()) != null) {
					Path destination = tempDirectory.resolve(entry.getName()).normalize();
					if (!destination.startsWith(tempDirectory)) {
						throw new IOException("Invalid ZIP entry: " + entry.getName());
					}
					if (entry.isDirectory()) {
						Files.createDirectories(destination);
					} else {
						Files.createDirectories(destination.getParent());
						Files.copy(zip, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
					}
					zip.closeEntry();
				}
			}
		}
		return tempDirectory;
	}


	public static List<Resource> readAllInput(SysMLInteractive sysMLInteractive,String path, String extension) {
		sysMLInteractive.readAll(path,true,extension);
		return sysMLInteractive.getInputResources();
	}

	public static List<Resource> readAllLibrary(SysMLInteractive s, String path, String ext) {
		Set<Resource> before = Set.copyOf(s.getResourceSet().getResources());   // std lib is in here
		s.readAll(path, false, ext);
		return s.getResourceSet().getResources().stream()
				.filter(r -> !before.contains(r))                              // only what this call added
				.toList();
	}
}
