// package com.sqlquery.project;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.*;
// import java.util.zip.ZipEntry;
// import java.util.zip.ZipInputStream;

// @Controller
// public class zipfile {

//     @PostMapping("/upload")
//     public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {

//         // Save the uploaded zip file to disk
//         File tempFile = File.createTempFile("temp", ".zip");
//         FileOutputStream fos = new FileOutputStream(tempFile);
//         fos.write(file.getBytes());
//         fos.close();

//         // Unzip the file and read each entry
//         FileInputStream fis = new FileInputStream(tempFile);
//         ZipInputStream zis = new ZipInputStream(fis);

//         ZipEntry entry;
//         while ((entry = zis.getNextEntry()) != null) {
//             // Read the file contents
//             ByteArrayOutputStream baos = new ByteArrayOutputStream();
//             byte[] buffer = new byte[1024];
//             int count;
//             while ((count = zis.read(buffer)) != -1) {
//                 baos.write(buffer, 0, count);
//             }

//             // Print the contents of the file
//             String contents = baos.toString();
//             System.out.println(contents);

//             // Close the ByteArrayOutputStream
//             baos.close();
//         }

//         // Close the ZipInputStream and FileInputStream
//         zis.close();
//         fis.close();

//         // Delete the temporary file
//         tempFile.delete();

//         return "upload-success";
//     }
// }
