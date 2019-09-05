package at.vertumnus.glacierupload;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TarHelper {

    static void createTarFile(File tarFile, String... files) throws IOException {

        try (TarArchiveOutputStream outputStream = new TarArchiveOutputStream(new FileOutputStream(tarFile))) {
            outputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            outputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);

            for (String file : files) {
                addFileToTar(outputStream, file, "");
            }
        }
    }

    private static void addFileToTar(TarArchiveOutputStream tOut, String path, String base)
            throws IOException {
        File f = new File(path);
        String entryName = base + f.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
        tOut.putArchiveEntry(tarEntry);

        if (f.isFile()) {
            IOUtils.copy(new FileInputStream(f), tOut);
            tOut.closeArchiveEntry();
        } else {
            tOut.closeArchiveEntry();
            File[] children = f.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFileToTar(tOut, child.getAbsolutePath(), entryName + "/");
                }
            }
        }
    }
}
