package com.shotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import com.shotspot.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connection  connection = DatabaseConnection.connect();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM dbo.PERSON");
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                System.out.println(rs.getString(2));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        String yourSasToken = "sp=r&st=2021-05-06T13:06:41Z&se=2021-05-06T21:06:41Z&spr=https&sv=2020-02-10&sr=c&sig=VeYcYWpZ2ubA049beYnOzaiOTkMYlrZa3JvSnuYroDE%3D";
        /* Create a new BlobServiceClient with a SAS Token */
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("https://shotspot.blob.core.windows.net/")
                .sasToken(yourSasToken)
                .buildClient();

        /* Create a new container client */
        BlobContainerClient containerClient= null;
        try {
            containerClient = blobServiceClient.createBlobContainer("imagenes");
        } catch (BlobStorageException ex) {
            // The container may already exist, so don't throw an error
            if (!ex.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
                throw ex;
            }
        }

        /* Upload the file to the container */
        BlobClient blobClient = containerClient.getBlobClient("my-remote-file.jpg");
        blobClient.uploadFromFile("my-local-file.jpg");
    }

}