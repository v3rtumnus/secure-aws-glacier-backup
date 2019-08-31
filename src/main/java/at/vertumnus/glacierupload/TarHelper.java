package at.vertumnus.glacierupload;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class TarHelper {

    public static Path createTarFile(String... files) throws IOException {
        Path tarFile = Files.createTempFile("backup", ".tar");

        FileOutputStream dest = new FileOutputStream(tarFile.toFile().getAbsolutePath());

        // Create a TarOutputStream
        TarOutputStream out = new TarOutputStream(new BufferedOutputStream(dest));

        // Files to tar
        File[] filesToTar = new File[files.length];

        for (int i = 0; i < files.length; i++) {
            filesToTar[i] = new File(files[i]);
        }

        for (File f : filesToTar) {
            out.putNextEntry(new TarEntry(f, f.getName()));
            BufferedInputStream origin = new BufferedInputStream(new FileInputStream(f));

            int count;
            byte data[] = new byte[2048];
            while ((count = origin.read(data)) != -1) {
                out.write(data, 0, count);
            }

            out.flush();
            origin.close();
        }

        out.close();

        return tarFile;
    }
}
