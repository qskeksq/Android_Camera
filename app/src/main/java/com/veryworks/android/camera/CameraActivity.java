package com.veryworks.android.camera;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView imageView;
    Button btnCapture;
    Uri fileUri = null;
    int REQ_CAMERA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setView();
        setListener();
    }
    private void setView(){
        imageView = (ImageView) findViewById(R.id.cameraImage);
        btnCapture = (Button) findViewById(R.id.btnCapture);
    }
    private void setListener(){
        btnCapture.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        takePhoto();
    }

    /**
     * 마치 컨텐트 프로바이더를 통해서 앱 내부의 데이터에 접근할 수 있는 것처럼 누가부터는 파일도 생성한 곳 밖에서
     * 접근할 수 없도록 했다. 따라서 FileProvider (contentProvider 처럼) 를 통해서 접근하도록 한 것이다.
     * 또한 안드로이드가 그 주소를 알고 있기 때문에 실제 경로는 알 필요가 없고 Uri 만 알면 접근할 수 있다.
     */

    private void takePhoto(){
        // 1. 인텐트 만들기
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 2. 호환성 처리 버전체크 - 롤리팝 이상
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            // 3. 실제 파링리 저장되는 객체
            File photoFile = null;
                try {
                photoFile = createFile();
                refreshMedia(photoFile);
                if(photoFile != null){
                    // 3.1 실제 파일이저장되는곳에 권한이 부여되어 있어야 한다. 롤리팝 이상부터는 FileProvider 를 선언해 줘야 한다
                    // 마시멜로 이상 버전은 파일 프로바이더를 통해 권한을 획득(Uri.fromFile 이 방식이 조금 달라진 것일 뿐이다)
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){         // 권한을 어디서 획득할 것인지          // 저장할 곳
                        fileUri = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID+".provider", photoFile);
                    // 롤리팝 버전은 권한 없이 획득
                    } else {
                        fileUri = Uri.fromFile(photoFile);
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, REQ_CAMERA);
                }
            }catch(Exception e){
                Toast.makeText(getBaseContext(), "사진파일 저장을 위한 임시파일을 생성할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }else { // 롤리팝 미만 버전에서만 바로 실행
            startActivityForResult(intent, REQ_CAMERA);
        }
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

    private File createFile() throws IOException{
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 요청코드 구분
        if(requestCode == REQ_CAMERA){
            // 결과처리 상태 구분
            if (resultCode == RESULT_OK) {
                Uri imageUri = null;
                // 롤리팝 미만 버전에서는 data 인텐트에 찍은 사진의 uri 가 담겨온다.
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    imageUri = data.getData();
                }else{
                    imageUri = fileUri;
                }
                Log.i("Camera","fileUri========================"+fileUri);
                Log.i("Camera","imageUri========================"+imageUri);

                imageView.setImageURI(imageUri);
            }
        }
    }
}
