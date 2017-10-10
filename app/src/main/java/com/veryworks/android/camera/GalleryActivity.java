package com.veryworks.android.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {


    ImageView albumImage;
    Uri afterCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        albumImage = (ImageView) findViewById(R.id.albumImage);
    }

    public File getFilesDir(Context context){

        String state = Environment.getExternalStorageState();
        File fileDir;

        if(Environment.MEDIA_MOUNTED.equals(state)){
            fileDir = context.getExternalFilesDir(null);
        } else {
            fileDir = context.getFilesDir();
        }
        return fileDir;
    }

    private File createFile() throws IOException {
        // 임시파일명 생성
        String tempFilename = "TEMP_"+System.currentTimeMillis();
        // 임시파일 저장용 디렉토리 생성
        File tempDir = new File(Environment.getExternalStorageDirectory() + "/CameraN/");
        if(!tempDir.exists()){
            tempDir.mkdirs();
        }
        // 실제 임시파일을 생성
        File tempFile = File.createTempFile(tempFilename, ".jpg", tempDir);
        return tempFile;
    }

    public void cropAlbum(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, Const.PICK_FROM_ALBUM);
    }
    private void refreshMedia(File photoFile) {
        MediaScannerConnection.scanFile(this,
                new String[] {photoFile.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener(){
                    public void onScanCompleted(String path, Uri uri){

                    }
                });
    }

    public Intent cropAlbumIntent(Uri uri){
        File file = null;
        try {
            file = createFile();
            refreshMedia(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        afterCrop = Uri.fromFile(file);
//        afterCrop = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID+".provider", file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");  // 뭐를 자를 것인지
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, afterCrop);  // 어디에 저장할 것인지
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        return intent;
    }

    public void cropImageFromAlbum(Uri uri){
        Intent intent = cropAlbumIntent(uri);
        startActivityForResult(intent, Const.CROP_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case Const.PICK_FROM_ALBUM:
                    cropImageFromAlbum(data.getData());
                    break;
                case Const.CROP_FROM_ALBUM:
                    albumImage.setImageURI(afterCrop);
                    break;
            }
        }
    }
}
