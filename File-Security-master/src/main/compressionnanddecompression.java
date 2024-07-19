package main;
import java.io.*;
import java.util.Scanner;
import java.util.zip.*;
public class compressionnanddecompression 
{
        private static final int BUFFER_SIZE = 4096;

        public static void compressFile(String sourceFilePath, String compressedFilePath) {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFilePath);
                FileOutputStream fileOutputStream = new FileOutputStream(compressedFilePath);
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    gzipOutputStream.write(buffer, 0, bytesRead);
                }

                fileInputStream.close();
                gzipOutputStream.finish();
                gzipOutputStream.close();

                System.out.println("File compressed successfully.");

                File sourceFile = new File(sourceFilePath);
                File compressedFile = new File(compressedFilePath);
                System.out.println("Original file size: " + sourceFile.length() + " bytes");
                System.out.println("Compressed file size: " + compressedFile.length() + " bytes");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void decompressFile(String compressedFilePath, String decompressedFilePath) {
            try {
                FileInputStream fileInputStream = new FileInputStream(compressedFilePath);
                GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(decompressedFilePath);

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                gzipInputStream.close();
                fileOutputStream.close();

                System.out.println("File decompressed successfully.");

                File compressedFile = new File(compressedFilePath);
                File decompressedFile = new File(decompressedFilePath);
                System.out.println("Compressed file size: " + compressedFile.length() + " bytes");
                System.out.println("Decompressed file size: " + decompressedFile.length() + " bytes");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter the path of the file to compress: ");
            String sourceFilePath = scanner.nextLine();

            System.out.print("Enter the path to save the compressed file: ");
            String compressedFilePath = scanner.nextLine();

            System.out.print("Enter the path to save the decompressed file: ");
            String decompressedFilePath = scanner.nextLine();

            scanner.close();

            // Compress the file
            compressFile(sourceFilePath, compressedFilePath);

            // Decompress the file
            decompressFile(compressedFilePath, decompressedFilePath);
        }
}





