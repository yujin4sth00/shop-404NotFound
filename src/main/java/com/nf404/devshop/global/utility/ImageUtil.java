package com.nf404.devshop.global.utility;

import com.nf404.devshop.product.model.dto.ImageDto;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.io.InputStream;

@Component
public class ImageUtil {

    @Value("${ftp.server.host}")
    private String server;

    @Value("${ftp.server.port}")
    private int port;

    @Value("${ftp.server.username}")
    private String username;

    @Value("${ftp.server.password}")
    private String password;

    public ImageDto upload(MultipartFile multipartFile, String folderName) throws IOException {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            try (InputStream inputStream = multipartFile.getInputStream()) {
                String originalFilename = multipartFile.getOriginalFilename();
                String dir = folderName + "/";
                String renamedFilename = getRenamedFilename(originalFilename);
                // 실제 파일전송

                boolean done = ftpClient.storeFile(dir + renamedFilename, inputStream);

                if (!done)
                    throw new RuntimeException("[" + multipartFile + "] 파일 업로드에 실패했습니다.");

                // Builder패턴을 사용한 객체 생성
                return ImageDto.builder()
                        .originFilename(originalFilename)
                        .uuidFilename(renamedFilename)
                        .build();
            }
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getRenamedFilename(String originalFilename) {
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        return "%s%s".formatted(UUID.randomUUID().toString(), ext);
    }


    public String convertFilenameToUrl(String filePath, String uuidFilename) {
        return filePath + uuidFilename;
    }

    public String convertUrlToFilename(String url) {
        String[] parts = url.split("/");
        String filename = parts[parts.length - 1];

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename;
        } else {
            return "";
        }
    }

    public void deleteFile(String filename, String folderName) throws IOException {
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(server, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            filename = folderName + "/" + filename;
            boolean done = ftpClient.deleteFile(filename);

            if (!done)
                throw new RuntimeException("[" + filename + "] 파일 업로드에 실패했습니다.");

        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
