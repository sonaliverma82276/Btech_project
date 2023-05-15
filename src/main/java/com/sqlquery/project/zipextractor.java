package com.sqlquery.project;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class zipextractor {
    public static File extractDirectoryFromZip(MultipartFile zipFile) throws IOException {
        File tempDirectory = Files.createTempDirectory("temp").toFile();

        try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    String entryName = entry.getName();
                    File directory = new File(tempDirectory, entryName);
                    directory.mkdirs();
                } else {
                    String entryName = entry.getName();
                    File file = new File(tempDirectory, entryName);

                    try (OutputStream outputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                }
            }
        }

        return tempDirectory;
    }
}
