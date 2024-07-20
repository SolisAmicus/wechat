package com.solisamicus.utils;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.solisamicus.constants.Symbols.SLASH;

@Slf4j
public class MinIOUtils {
    private static MinioClient minioClient; // MinIO client instance for interacting with the MinIO server.

    private static String endpoint; // Endpoint URL of the MinIO server.
    private static String fileHost; // Base URL for accessing files.
    private static String bucketName; //Name of the default bucket used for file storage.
    private static String accessKey; // Access key for authenticating with the MinIO server.
    private static String secretKey; // Secret key for authenticating with the MinIO server.

    public MinIOUtils() {
    }

    /**
     * @param endpoint
     * @param fileHost
     * @param bucketName
     * @param accessKey
     * @param secretKey
     */
    public MinIOUtils(String endpoint, String fileHost, String bucketName, String accessKey, String secretKey) {
        MinIOUtils.endpoint = endpoint;
        MinIOUtils.fileHost = fileHost;
        MinIOUtils.bucketName = bucketName;
        MinIOUtils.accessKey = accessKey;
        MinIOUtils.secretKey = secretKey;
        createMinioClient();
    }

    /**
     * Create a MinioClient based on the Java client.
     */
    public void createMinioClient() {
        if (minioClient == null) {
            try {
                log.info("开始创建 MinioClient...");
                minioClient = MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
                createBucket(bucketName);
                log.info("创建完毕 MinioClient...");
            } catch (Exception e) {
                log.error("MinIO服务器异常：{}", e.getMessage(), e);
            }
        }
    }

    /**
     * Get the base URL for file uploads.
     *
     * @return the base URL including the endpoint and bucket name
     */
    public static String getBaseUrl() {
        return String.format("%s%s%s%s", endpoint, SLASH, bucketName, SLASH); // http://127.0.0.1:9000/wechat/
    }

    /**
     * Get the full URL for accessing a specific file.
     *
     * @param filename the name of the file to access
     * @return the full URL to access the file
     */
    public static String getFileAccessUrl(String filename) {
        return String.format("%s%s%s%s%s", fileHost, SLASH, bucketName, SLASH, filename); // http://127.0.0.1:9000/wechat/face\1813476981327425538\1813476981327425538.jpg
    }

    /**
     * Create the bucket if it doesn't exist.
     *
     * @param bucketName the name of the bucket
     * @throws Exception if an error occurs
     */
    private static void createBucket(String bucketName) throws Exception {
        if (!bucketExists(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * Check if a bucket exists.
     *
     * @param bucketName the name of the bucket
     * @return true if the bucket exists, false otherwise
     * @throws Exception if an error occurs
     */
    public static boolean bucketExists(String bucketName) throws Exception {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }


    /**
     * Get the policy of a bucket.
     *
     * @param bucketName the name of the bucket
     * @return the bucket policy
     * @throws Exception if an error occurs
     */
    public static String getBucketPolicy(String bucketName) throws Exception {
        String bucketPolicy = minioClient.getBucketPolicy(GetBucketPolicyArgs.builder().bucket(bucketName).build());
        return bucketPolicy;
    }


    /**
     * Get a list of all buckets.
     *
     * @return a list of buckets
     * @throws Exception if an error occurs
     */
    public static List<Bucket> getAllBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * Get information about a bucket by its name.
     *
     * @param bucketName the name of the bucket
     * @return an Optional containing the bucket information if found, empty otherwise
     * @throws Exception if an error occurs
     */
    public static Optional<Bucket> getBucket(String bucketName) throws Exception {
        return getAllBuckets().stream().filter(bucket -> bucket.name().equals(bucketName)).findFirst();
    }

    /**
     * Delete a bucket by its name.
     *
     * @param bucketName the name of the bucket
     * @throws Exception if an error occurs
     */
    public static void removeBucket(String bucketName) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * Check if an object exists in a bucket.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the object
     * @return true if the object exists, false otherwise
     */
    public static boolean isObjectExist(String bucketName, String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if a folder exists in a bucket.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the folder
     * @return true if the folder exists, false otherwise
     */
    public static boolean isFolderExist(String bucketName, String objectName) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(objectName).recursive(false).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir() && objectName.equals(item.objectName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("检查文件夹是否存在时出错：{}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * Get a list of all objects in a bucket by prefix.
     *
     * @param bucketName the name of the bucket
     * @param prefix     the prefix of the objects
     * @param recursive  whether to list objects recursively
     * @return a list of items
     * @throws Exception if an error occurs
     */
    public static List<Item> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) throws Exception {
        List<Item> list = new ArrayList<>();
        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build());
        if (objectsIterator != null) {
            for (Result<Item> o : objectsIterator) {
                list.add(o.get());
            }
        }
        return list;
    }

    /**
     * Get an object as an InputStream.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the object
     * @return the InputStream of the object
     * @throws Exception if an error occurs
     */
    public static InputStream getObject(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * Download an object with offset and length.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the object
     * @param offset     the starting byte position
     * @param length     the length of bytes to read
     * @return the InputStream of the object
     * @throws Exception if an error occurs
     */
    public InputStream getObject(String bucketName, String objectName, long offset, long length) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .offset(offset)
                        .length(length)
                        .build());
    }

    /**
     * List objects in a bucket.
     *
     * @param bucketName the name of the bucket
     * @param prefix     the prefix of the objects
     * @param recursive  whether to list objects recursively
     * @return an Iterable of Result<Item>
     */
    public static Iterable<Result<Item>> listObjects(String bucketName, String prefix, boolean recursive) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .recursive(recursive)
                        .build());
    }


    /**
     * Upload a file using MultipartFile.
     *
     * @param bucketName  the name of the bucket
     * @param file        the MultipartFile to upload
     * @param objectName  the name of the object
     * @param contentType the content type of the file
     * @return an ObjectWriteResponse
     * @throws Exception if an error occurs
     */
    public static ObjectWriteResponse uploadFile(String bucketName, MultipartFile file, String objectName, String contentType) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            return minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType(contentType)
                            .stream(inputStream, inputStream.available(), -1)
                            .build());
        }
    }

    /**
     * Upload a local file.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the object
     * @param fileName   the local file path
     * @param needUrl    whether a URL is needed
     * @return the URL of the uploaded file if needed, otherwise an empty string
     * @throws Exception if an error occurs
     */
    public static String uploadFile(String bucketName, String objectName, String fileName, boolean needUrl) throws Exception {
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(fileName)
                        .build());
        if (needUrl) {
            return String.format("%s%s%s%s%s", fileHost, SLASH, bucketName, SLASH, objectName);
        }
        return "";
    }

    /**
     * Upload a file using InputStream.
     *
     * @param bucketName  the name of the bucket
     * @param objectName  the name of the object
     * @param inputStream the InputStream of the file
     * @return an ObjectWriteResponse
     * @throws Exception if an error occurs
     */
    public static ObjectWriteResponse uploadFile(String bucketName, String objectName, InputStream inputStream) throws Exception {
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
    }

    /**
     * Upload a file using InputStream.
     *
     * @param bucketName  the name of the bucket
     * @param objectName  the name of the object
     * @param inputStream the InputStream of the file
     * @param needUrl     whether a URL is needed
     * @return the URL of the uploaded file if needed, otherwise an empty string
     * @throws Exception if an error occurs
     */
    public static String uploadFile(String bucketName, String objectName, InputStream inputStream, boolean needUrl) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
        if (needUrl) {
            return String.format("%s%s%s%s%s", fileHost, SLASH, bucketName, SLASH, objectName);
        }
        return "";
    }

    /**
     * Create a directory.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the directory
     * @return an ObjectWriteResponse
     * @throws Exception if an error occurs
     */
    public static ObjectWriteResponse createDir(String bucketName, String objectName) throws Exception {
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                        .build());
    }

    /**
     * Get the status information of a file.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the object
     * @return the status information of the object
     * @throws Exception if an error occurs
     */
    public static String getFileStatusInfo(String bucketName, String objectName) throws Exception {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()).toString();
    }

    /**
     * Copy a file.
     *
     * @param bucketName    the name of the source bucket
     * @param objectName    the name of the source object
     * @param srcBucketName the name of the destination bucket
     * @param srcObjectName the name of the destination object
     * @return an ObjectWriteResponse
     * @throws Exception if an error occurs
     */
    public static ObjectWriteResponse copyFile(String bucketName, String objectName, String srcBucketName, String srcObjectName) throws Exception {
        return minioClient.copyObject(
                CopyObjectArgs.builder()
                        .source(CopySource.builder().bucket(bucketName).object(objectName).build())
                        .bucket(srcBucketName)
                        .object(srcObjectName)
                        .build());
    }

    /**
     * Delete a file.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the object
     * @throws Exception if an error occurs
     */
    public static void removeFile(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    /**
     * Delete multiple files.
     *
     * @param bucketName the name of the bucket
     * @param keys       the list of object names to delete
     */
    public static void removeFiles(String bucketName, List<String> keys) {
        List<DeleteObject> objects = new LinkedList<>();
        keys.forEach(key -> objects.add(new DeleteObject(key)));
        try {
            minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());
        } catch (Exception e) {
            log.error("批量删除失败！error:{}", e.getMessage(), e);
        }
    }

    /**
     * Get the presigned URL of an object.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the object
     * @param expires    the expiration time in seconds (<= 7)
     * @return the presigned URL
     * @throws Exception if an error occurs
     */
    public static String getPresignedObjectUrl(String bucketName, String objectName, Integer expires) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(expires)
                        .build());
    }

    /**
     * Get the presigned URL of an object.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the object
     * @return the presigned URL
     * @throws Exception if an error occurs
     */
    public static String getPresignedObjectUrl(String bucketName, String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .method(Method.GET)
                        .build());
    }

    /**
     * Decode a URL-encoded string to UTF-8.
     *
     * @param str the URL-encoded string
     * @return the decoded string
     */
    public static String getUtf8ByURLDecoder(String str) {
        String url = str.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }
}
