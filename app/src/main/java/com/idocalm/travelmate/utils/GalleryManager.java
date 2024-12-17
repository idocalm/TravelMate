package com.idocalm.travelmate.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class GalleryManager {

    private final Context context;
    private File photoFile;
    private static final String AUTHORITY = "com.idocalm.travelmate.fileprovider";

    public GalleryManager(Context context) {
        this.context = context;
    }

    public void fromGallery(ActivityResultLauncher<Intent> galleryLauncher,
                            ActivityResultLauncher<Intent> cameraLauncher) {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);

        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
                photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(context, AUTHORITY, photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    cameraLauncher.launch(cameraIntent);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
    public void takePicture(ActivityResultLauncher<Intent> cameraLauncher, ActivityResultLauncher<String> permissionLauncher) {
        // Check if the permission is granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // If permission is granted, proceed with taking a picture
            launchCamera(cameraLauncher);
        } else {
            // Request permission if it's not granted
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    public void launchCamera(ActivityResultLauncher<Intent> cameraLauncher) {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
                // Create a file to store the photo
                photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(context, AUTHORITY, photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    cameraLauncher.launch(cameraIntent);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(); // Handle the error
        }
    }

    public void openDialog(ActivityResultLauncher<Intent> galleryLauncher,
                           ActivityResultLauncher<Intent> cameraLauncher, ActivityResultLauncher<String> permissionLauncher) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose an option")
                .setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Camera option selected
                            takePicture(cameraLauncher, permissionLauncher);
                        } else {
                            // Gallery option selected
                            fromGallery(galleryLauncher, cameraLauncher);
                        }
                    }
                });
        builder.create().show();
    }

    public Uri getCapturedImageUri() {
        if (photoFile != null) {
            return Uri.fromFile(photoFile); // Return captured image URI
        }
        return null;
    }

    public Uri getImageUri(Intent data) {
        if (data != null) {
            return data.getData(); // Return gallery image URI
        } else if (photoFile != null) {
            return Uri.fromFile(photoFile); // Return captured image URI
        }
        return null;
    }

    // Helper method to create a file for the captured image
    private File createImageFile() throws IOException {
        String fileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = context.getExternalFilesDir(null);
        return File.createTempFile(fileName, ".jpg", storageDir);
    }
}
