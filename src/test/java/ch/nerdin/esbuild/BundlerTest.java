package ch.nerdin.esbuild;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BundlerTest {

    @Test
    public void shouldBundleMvnpm() throws URISyntaxException, IOException {
        executeTest("/stimulus-3.2.1.jar", Bundler.BundleType.MVNPM, "/application-mvnpm.js");
    }

    @Test
    public void shouldBundle() throws URISyntaxException, IOException {
        executeTest("/htmx.org-1.8.4.jar", Bundler.BundleType.WEBJARS, "/application-webjar.js");
    }

    @Test
    public void shouldWatch() throws URISyntaxException, IOException, InterruptedException {
        // given
        final BundleOptions options = getBundleOptions("/stimulus-3.2.1.jar", Bundler.BundleType.MVNPM, "/application-mvnpm.js");

        // when
        AtomicBoolean isCalled = new AtomicBoolean(false);
        final Watch watch = Bundler.watch(options, () -> isCalled.set(true));

        // then
        Thread.sleep(2000);
        watch.change(options.getEntries());
        watch.stop();
        assertTrue(isCalled.get());
    }

    private void executeTest(String jarName, Bundler.BundleType type, String scriptName) throws URISyntaxException, IOException {
        final BundleOptions bundleOptions = getBundleOptions(jarName, type, scriptName);
        final Path path = Bundler.bundle(bundleOptions);

        assertTrue(path.toFile().exists());
    }

    private BundleOptions getBundleOptions(String jarName, Bundler.BundleType type, String scriptName) throws URISyntaxException {
        final File jar = new File(getClass().getResource(jarName).toURI());
        final List<Path> dependencies = Collections.singletonList(jar.toPath());
        final Path entry = new File(getClass().getResource(scriptName).toURI()).toPath();

        final BundleOptions bundleOptions = new BundleOptionsBuilder().withDependencies(dependencies)
                .withEntry(entry).withType(type).build();
        return bundleOptions;
    }

}
