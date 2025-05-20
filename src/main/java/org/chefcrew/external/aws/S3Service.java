package org.chefcrew.external.aws;

import org.chefcrew.common.exception.CustomException;
import org.chefcrew.common.exception.ErrorException;
import org.chefcrew.config.S3Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//@Service // AWS S3 기능을 비활성화하기 위해 주석 처리
public class S3Service {
    private final String bucketName;
    private final S3Config s3Config;
    private static final Long MAX_FILE_SIZE = 5 * 1024 * 1024L;


    public S3Service(@Value("${aws-property.s3-bucket-name}") final String bucketName, S3Config s3Config) {
        this.bucketName = bucketName;
        this.s3Config = s3Config;
    }

    public String uploadImage(MultipartFile multipartFile, String folder) {
        final String key = folder + createFileName(multipartFile.getOriginalFilename());
        final S3Client s3Client = s3Config.getS3Client();

        validateFileSize(multipartFile);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .contentDisposition("inline")
                .build();

        try {
            RequestBody requestBody = RequestBody.fromBytes(multipartFile.getBytes());
            s3Client.putObject(request, requestBody);
            return key;
        } catch (IOException e) {
            throw new CustomException(ErrorException.IMAGE_NOT_FOUND);
        }
    }

    public List<String> uploadImages(List<MultipartFile> multipartFileList, String folder) {
        final S3Client s3Client = s3Config.getS3Client();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < multipartFileList.size(); i++) {
            String key = folder + createFileName(multipartFileList.get(i).getOriginalFilename());
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(multipartFileList.get(i).getContentType())
                    .contentLength(multipartFileList.get(i).getSize())
                    .contentDisposition("inline")
                    .build();

            try {
                RequestBody requestBody = RequestBody.fromBytes(multipartFileList.get(i).getBytes());
                s3Client.putObject(request, requestBody);
                list.add(key);
            } catch (IOException e) {
                throw new CustomException(ErrorException.IMAGE_NOT_FOUND);
            }
        }
        return list;
    }

    // 파일명 (중복 방지)
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        if (fileName.length() == 0) {
            throw new CustomException(ErrorException.IMAGE_NOT_FOUND);
        }
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new CustomException(ErrorException.FILE_BAD_REQUEST);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    // 이미지 삭제
    public void deleteImage(String key) throws IOException {
        final S3Client s3Client = s3Config.getS3Client();

        s3Client.deleteObject((DeleteObjectRequest.Builder builder) ->
                builder.bucket(bucketName)
                        .key(key)
                        .build()
        );
    }

    private void validateFileSize(MultipartFile image) {
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(ErrorException.FILE_SIZE_BAD_REQUEST);
        }
    }

    public String getURL(String keyName) {
        final S3Client s3Client = s3Config.getS3Client();
        try {
            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            URL url = s3Client.utilities().getUrl(request);
            System.out.println("이미지 키 네임: "+keyName +"의 url: "+ url);
            return url.toString();

        } catch (S3Exception e) {
            //db에서 저장된 keyname -> url 변환과정에서 문제 발생시
            throw new CustomException(ErrorException.IMAGE_INTERNAL_SERVER_ERROR);
        }
    }

    public String getFoodImageUrl(String keyName) {
        if(keyName == null) {
            return null;
        }else{
            return getURL(keyName);
        }
    }
}