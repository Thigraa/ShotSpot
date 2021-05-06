package com.shotspot.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.LinkedList;

public class ImageManager {
    public static final String storageConnectionString =
            "https://shotspot.blob.core.windows.net/" +
                    "AccountName=imagenes;" +
                    "AccountKey=sp=r&st=2021-05-06T13:26:15Z&se=2021-05-06T21:26:15Z&spr=https&sv=2020-02-10&sr=c&sig=2ZbdaKu%2BOwWii3LFvLKL0WF%2BVwT05aLZWhaMowYFDhA%3D";

    private static CloudBlobContainer getContainer() throws Exception{
        // Retrieve storage account from connection-string.

        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(storageConnectionString);

        // Create the blob client.
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        // Get a reference to a container.
        // The container name must be lower case
        CloudBlobContainer container = blobClient.getContainerReference("imagenes");

        return container;
    }
    public static String UploadImage(InputStream image, int imageLength) throws Exception {
        CloudBlobContainer container = getContainer();

        container.createIfNotExists();

        String imageName = randomString(10);

        CloudBlockBlob imageBlob = container.getBlockBlobReference(imageName);
        imageBlob.upload(image, imageLength);

        return imageName;

    }

    public static String[] ListImages() throws Exception{
        CloudBlobContainer container = getContainer();

        Iterable<ListBlobItem> blobs = container.listBlobs();

        LinkedList<String> blobNames = new LinkedList<>();
        for(ListBlobItem blob: blobs) {
            blobNames.add(((CloudBlockBlob) blob).getName());
        }

        return blobNames.toArray(new String[blobNames.size()]);
    }

    public static void GetImage(String name, OutputStream imageStream, long imageLength) throws Exception {
        CloudBlobContainer container = getContainer();

        CloudBlockBlob blob = container.getBlockBlobReference(name);

        if(blob.exists()){
            blob.downloadAttributes();

            imageLength = blob.getProperties().getLength();

            blob.download(imageStream);
        }
    }

    static final String validChars = "abcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( validChars.charAt( rnd.nextInt(validChars.length()) ) );
        return sb.toString();
    }
}
