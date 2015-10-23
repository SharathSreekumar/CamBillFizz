package com.example.shreekumar.myapplication9;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // for display
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;
    ListView list;
    File imageFile;
    ListAdapter adapter;

    // for storing
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100,SELECT_PICTURE = 1;
    private Uri fileUri;
    private String file_image = "myimage", id;
    private String string,selectedImagePath;
    private SQLiteDatabase dataBase;
    private boolean isUpdate;
    private AlertDialog.Builder build;

    ImageButton camerabtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //display image
        // Check for SD Card
            // Locate the image folder in your SD Card
        imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FizzApp1");
        if (!imageFile.exists()) {
            if (!imageFile.mkdirs()) {
                Log.d("FizzApp1", "failed to create directory");
                Toast.makeText(getBaseContext(), "File directory creation failed", Toast.LENGTH_SHORT).show();
                //return null;
            } else {
                // Create a new folder if no folder named SDImageTutorial exist
                imageFile.mkdirs();
            }
        }

        if (imageFile.isDirectory()) {
            listFile = imageFile.listFiles();
            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = listFile[i].getName();
            }
        }

        // Locate the ListView in activity_main.xml
        list = (ListView) findViewById(R.id.listView1);
        // Pass String arrays to ListAdapter Class
        adapter = new ListAdapter(this, FilePathStrings, FileNameStrings);
        // Set the ListAdapter to the ListView
        list.setAdapter(adapter);
        list.invalidateViews();

        //Bundle data = new Bundle();
        //data.putStringArray(FilePathStrings[0],FileNameStrings);

        //same way you can set other values.......
        //Now set this Bundle value to Intent as you do for primitive type....

        //Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra(data);
        //startActivity(intent);

        // Capture listview item click

        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(MainActivity.this, ViewImage.class);
                // Pass String arrays FilePathStrings
                i.putExtra("filepath", FilePathStrings);
                // Pass String arrays FileNameStrings
                i.putExtra("filename", FileNameStrings);
                // Pass click position
                i.putExtra("position", position);
                startActivity(i);
            }

        });*/

        ImageButton camera_button = (ImageButton) findViewById(R.id.imageButtonAdd);

        camera_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                build = new AlertDialog.Builder(MainActivity.this);
                build.setTitle("Take image from");
                //camera module
                build.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        int MEDIA_TYPE_IMAGE = 1;
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                        // start the image capture Intent
                        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        try {
                            FileOutputStream outputStream_image = openFileOutput(file_image, MODE_WORLD_READABLE);
                            outputStream_image.write(string.getBytes());
                            outputStream_image.close();
                            Toast.makeText(getBaseContext(), "location of image saved", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                    }

                    private Uri getOutputMediaFileUri(int MEDIA_TYPE_IMAGE) {
                        // TODO Auto-generated method stub

                       if(isExternalStorageWritable()) {
                           Toast.makeText(getBaseContext(),"Verifying SD card",Toast.LENGTH_SHORT).show();
                           return Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
                       }

                        return null;
                    }

                    /* Checks if external storage is available for read and write */
                    public boolean isExternalStorageWritable() {
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            return true;
                        }
                        return false;
                    }

                    private File getOutputMediaFile(int type) {
                        // To be safe, you should check that the SDCard is mounted
                        // using Environment.getExternalStorageState() before doing this.

                        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FizzApp1");
                        // This location works best if you want the created images to be shared
                        // between applications and persist after your app has been uninstalled.

                        // Create the storage directory if it does not exist
                        if (!mediaStorageDir.exists()) {
                            if (!mediaStorageDir.mkdirs()) {
                                Log.d("FizzApp1", "failed to create directory");
                                Toast.makeText(getBaseContext(),"File directory creation failed",Toast.LENGTH_LONG).show();
                                return null;
                            }
                        }
                        // Create a media file name
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        File mediaFile;
                        int MEDIA_TYPE_IMAGE = 1;
                        if (type == MEDIA_TYPE_IMAGE){
                            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
                            Toast.makeText(getBaseContext(),"Created file name",Toast.LENGTH_LONG).show();
                        } else {
                            return null;
                        }
                        return mediaFile;
                    }
                });

                //gallery module
                build.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
                        dialog.cancel();
                    }
                });
                AlertDialog alert = build.create();
                alert.show();
                // The 'which' argument contains the index position
                // of the selected item
            }
        });
    }

    //for Gallery -> to select an image & create the folder for the App to store
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                Toast.makeText(getApplication(),selectedImagePath,Toast.LENGTH_SHORT).show();
                File mediaStored = new File(getPath(selectedImageUri));
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FizzApp1");//creates folder FizzApp1 in Pictures directory
                // This location works best if you want the created images to be shared
                // between applications and persist after your app has been uninstalled.

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("FizzApp1", "failed to create directory");
                        Toast.makeText(getBaseContext(),"File directory creation failed",Toast.LENGTH_SHORT).show();
                        //return null;
                    }else{
                        Toast.makeText(getApplication(),"Creating & Transferring",Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplication(),"Entering copyFileorDirect",Toast.LENGTH_SHORT).show();
                        //copyFileOrDirectory(getPath(selectedImageUri), "FizzApp1");
                        try {
                            Toast.makeText(getApplication(),"Entering copyFile",Toast.LENGTH_SHORT).show();
                            copyFile(mediaStored, mediaStorageDir);
                            Toast.makeText(getApplication(),"Transferring",Toast.LENGTH_SHORT).show();
                        }catch (IOException e){
                            e.printStackTrace();
                        }finally {
                            Toast.makeText(getApplication(),"Executed Try-catch",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    try {
                        Toast.makeText(getApplication(),"Entering copyFile",Toast.LENGTH_SHORT).show();
                        copyFile(mediaStored, mediaStorageDir);
                        Toast.makeText(getApplication(),"Transferring",Toast.LENGTH_SHORT).show();
                    }catch (IOException e){
                        e.printStackTrace();
                    }finally {
                        Toast.makeText(getApplication(),"Executed Try-catch",Toast.LENGTH_SHORT).show();
                        //reopening/ redirecting the mainActivity to itself
                        Intent i = new Intent(this,MainActivity.class);
                        startActivity(i);
                        //list.invalidateViews();
                    }
                    //Toast.makeText(getApplication(),"Entering copyFileorDirect",Toast.LENGTH_SHORT).show();
                    //copyFileOrDirectory(getPath(selectedImageUri), "FizzApp1");
                }
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    //for gallery -> retrieving gallery image path
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    /**public static void copyFileOrDirectory(String srcDir, String dstDir) {// not needed

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**public static void copyFile(File sourceFile, File destFile) throws IOException { //transfer not working, problem with FileOutputStream
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }*/

    public void copyFile(File src, File dst) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "IMG_"+ timeStamp + ".jpg";
        File file = new File (dst, fname);
        if (file.exists ())
            file.delete ();

        Toast.makeText(getApplication(),"Stage 1.0",Toast.LENGTH_SHORT).show();
        try {
            Toast.makeText(getApplication(),"Stage 2.0",Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplication(),src.toString(),Toast.LENGTH_SHORT).show();
            in = new FileInputStream(src);
            Toast.makeText(getApplication(),"Stage 2.1",Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplication(),dst.toString(),Toast.LENGTH_SHORT).show();
            //out = new BufferedOutputStream(new FileOutputStream(mediaStorageDir));
            out = new FileOutputStream(file);
            Toast.makeText(getApplication(),"Stage 2.2",Toast.LENGTH_SHORT).show();
            IOUtils.copy(in, out);
        } catch (IOException ioe) {
            Toast.makeText(getApplication(),"Stage catch",Toast.LENGTH_SHORT).show();
            String LOGTAG = "Error";
            Log.e(LOGTAG, "IOException occurred.", ioe);
        } finally {
            Toast.makeText(getApplication(),"Closing Streams", Toast.LENGTH_SHORT).show();
            out.flush();
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.cameraMenu1:
                build = new AlertDialog.Builder(MainActivity.this);
                build.setTitle("Take image from");
                build.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        int MEDIA_TYPE_IMAGE = 1;
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                        // start the image capture Intent
                        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        try {
                            FileOutputStream outputStream_image = openFileOutput(file_image, MODE_WORLD_READABLE);
                            outputStream_image.write(string.getBytes());
                            outputStream_image.close();
                            Toast.makeText(getBaseContext(), "location of image saved", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                    }

                    private Uri getOutputMediaFileUri(int MEDIA_TYPE_IMAGE) {
                        // TODO Auto-generated method stub

                        if(isExternalStorageWritable()) {
                            Toast.makeText(getBaseContext(),"Verifying SD card",Toast.LENGTH_LONG).show();
                            return Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
                        }

                        return null;
                    }

                    /* Checks if external storage is available for read and write */
                    public boolean isExternalStorageWritable() {
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            return true;
                        }
                        return false;
                    }

                    private File getOutputMediaFile(int type) {
                        // To be safe, you should check that the SDCard is mounted
                        // using Environment.getExternalStorageState() before doing this.

                        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FizzApp1");
                        // This location works best if you want the created images to be shared
                        // between applications and persist after your app has been uninstalled.

                        // Create the storage directory if it does not exist
                        if (!mediaStorageDir.exists()) {
                            if (!mediaStorageDir.mkdirs()) {
                                Log.d("FizzApp1", "failed to create directory");
                                Toast.makeText(getBaseContext(),"File directory creation failed",Toast.LENGTH_LONG).show();
                                return null;
                            }
                        }
                        // Create a media file name
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        File mediaFile;
                        int MEDIA_TYPE_IMAGE = 1;
                        if (type == MEDIA_TYPE_IMAGE){
                            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
                            Toast.makeText(getBaseContext(),"Created file name",Toast.LENGTH_LONG).show();
                        } else {
                            return null;
                        }
                        return mediaFile;
                    }
                });

                build.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                        dialog.cancel();
                    }
                });
                // The 'which' argument contains the index position
                // of the selected item
                return true;

            case R.id.action_settings:
                Toast.makeText(getBaseContext(), "settings Click working successfully!!!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
