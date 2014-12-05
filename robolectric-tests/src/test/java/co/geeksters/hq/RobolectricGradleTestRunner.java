package co.geeksters.hq;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;
import org.robolectric.res.FsFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RobolectricGradleTestRunner extends RobolectricTestRunner {
    private static final int MAX_SDK_SUPPORTED_BY_ROBOLECTRIC = 18;

    public RobolectricGradleTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    /*@Override
    protected AndroidManifest getAppManifest(Config config) {
        String manifestProperty = "app/src/main/AndroidManifest.xml";
        String resProperty = "app/src/main/res";
        return new AndroidManifest(Fs.fileFromPath(manifestProperty), Fs.fileFromPath(resProperty)) {
            @Override
            public int getTargetSdkVersion() {
                return MAX_SDK_SUPPORTED_BY_ROBOLECTRIC;
            }
        };
    }*/

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String manifestProperty = System.getProperty("android.manifest");
        if (config.manifest().equals(Config.DEFAULT) && manifestProperty != null) {
            String resProperty = System.getProperty("android.resources");
            String assetsProperty = System.getProperty("android.assets");
            AndroidManifest manifest = new AndroidManifest(Fs.fileFromPath(manifestProperty), Fs.fileFromPath(resProperty),
                    Fs.fileFromPath(assetsProperty));
            //manifest.setPackageName("com.mypackagename");
            return manifest;
        }
        return super.getAppManifest(config);
    }

    @Override
    protected AndroidManifest createAppManifest(FsFile manifestFile, FsFile resDir, FsFile assetsDir) {
        return new MavenAndroidManifest( manifestFile,  resDir,  assetsDir);
    }

    public static class MavenAndroidManifest extends AndroidManifest {
        public MavenAndroidManifest(FsFile androidManifestFile, FsFile resDirectory, FsFile assetsDirectory) {
            super(androidManifestFile, resDirectory, assetsDirectory);
        }

        public MavenAndroidManifest(FsFile libraryBaseDir) {
            super(libraryBaseDir);
        }

        @Override
        protected List<FsFile> findLibraries() {
            // Change "target/unpacked-libs" to "target/unpack/apklibs"
            //if youe have older version of android-maven-plugin
            FsFile unpack = getBaseDir().join("target/unpacked-libs");
            if (unpack.exists()) {
                FsFile[] libs = unpack.listFiles();
                if (libs != null) {
                    return Arrays.asList(libs);
                }
            }
            return Collections.emptyList();
        }

        @Override
        protected AndroidManifest createLibraryAndroidManifest(FsFile libraryBaseDir) {
            return new MavenAndroidManifest(libraryBaseDir);
        }
    }
}
