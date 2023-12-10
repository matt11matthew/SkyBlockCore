package net.cloudescape.skyblock.utils;


import com.google.common.collect.Lists;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Matthew E on 4/12/2018.
 */
public class ZipUtils {
    private List<String> fileList;

    public static void zip(File source, File output) throws IOException {
        ZipUtils fileUtil = new ZipUtils();
        fileUtil.fileList = Lists.newArrayList();

        fileUtil.generateFileList(fileUtil, source);

        byte[] buffer = new byte[1024];
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output))) {
            for(String file : fileUtil.fileList) {
                System.out.println("# " + fileUtil.replace(file.replace(source.getCanonicalPath(), "")));
                ZipEntry entry = new ZipEntry(fileUtil.replace(file.replace(source.getCanonicalPath(), "")));
                zos.putNextEntry(entry);

                FileInputStream inputStream = new FileInputStream(new File(file));
                int length;
                while((length = inputStream.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                inputStream.close();
            }

            zos.close();
        }
    }
    public static void createZipFile(String inputFolder, String outputFile) throws ZipException {

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        parameters.setIncludeRootFolder(false);
        ZipFile zipFile = new ZipFile(outputFile);
        File targetFile = new File(inputFolder);
        if (targetFile.isFile())
            zipFile.addFile(targetFile, parameters);
        else if (targetFile.isDirectory())
            zipFile.addFolder(targetFile, parameters);
        else
            System.out.println("[ZIPPER] - Don't know how to handle " + targetFile.getName());
    }
    public static void unzip(File source, File output) throws IOException {
        ZipUtils fileUtil = new ZipUtils();
        fileUtil.fileList = Lists.newArrayList();
        byte[] buffer = new byte[1024];
        fileUtil.createDirectory(output.getCanonicalPath());

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                String fileName = entry.getName();
                File file = new File(output, fileName);
                System.out.println(fileName);

                new File(file.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(file);
                int length;
                while ((length = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }

                fos.close();
                entry = zis.getNextEntry();
                fos.close();
            }

            zis.closeEntry();
        }
    }

    private void generateFileList(ZipUtils fileUtil, File node) throws IOException {
        if(node.isDirectory()) {
            String[] subNode = node.list();
            assert subNode != null;
            for(String name : subNode) {
                generateFileList(fileUtil, new File(node, name));
            }
        }
        else fileUtil.fileList.add(node.getCanonicalPath());
    }

    private void createDirectory(String dir) {
        File file = new File(dir);
        if(!file.exists()) file.mkdirs();
    }

    private String replace(String input) {
        return input.replaceFirst(Pattern.quote("\\"), "").replaceFirst(Pattern.quote("/"), "");
    }
}
