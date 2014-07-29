package lv.kasparsj.android.util;

import java.io.File;
import java.io.FileFilter;

public class FileUtils
{
    public static String getExtension(File file) {
        return file.getName().substring(file.getName().lastIndexOf(".") + 1);
    }

    public static FileFilter createExtensionFilter(final String ext) {
        return new FileFilter() {
            @Override
            public boolean accept(File file) {
                return getExtension(file).toLowerCase().equals(ext.toLowerCase());
            }
        };
    }

    public static File[] getFiles(File dir) {
        return getFiles(dir, null);
    }

    public static File[] getFiles(File dir, final FileFilter fileFilter) {
        return dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && (fileFilter == null || fileFilter.accept(file));
            }
        });
    }

    public static File[] getFilesWithExtension(File dir, String ext) {
        return getFiles(dir, createExtensionFilter(ext));
    }

    public static File getLastModified(File[] files) {
        long maxModified = Long.MIN_VALUE;
        File lastModified = null;
        for (File file : files) {
            if (file.lastModified() > maxModified) {
                lastModified = file;
                maxModified = file.lastModified();
            }
        }
        return lastModified;
    }
}
