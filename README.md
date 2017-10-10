# Intent를 통한 Camera, Album 사용


## Camera

#### 1. 권한 설정
```java
<!-- 사진을 저장하기 위한 파일에 대한 권한을 획득하기 위한 설정 -->
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">

    <!-- resource 파일을 res/xml 폴더안에 생성 -->
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_path" />
</provider>
```

```java
<paths>
    <!-- name(논리 구조)       = content:// 로 시작하는 uri 주소체계의 prefix 가 된다. 예를들어 http://Camera 에서 Camera 로시작하는 뜻.-->
    <!-- path(실제 디스크 경로) = /External Storage/CameraN 가 된다 -->
    <external-path name="Camera" path="CameraN" />
</paths>
```

#### 2. 퍼미션 설정
```java
private static String[] permissions = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
```

#### 3. 저장할 파일 객체 생성
```java
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
```

#### 4. 생성한 파일 객체를 외부에서 사용할 수 있도록 설정
```java
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
```

#### 5. 받아와 사용
```java
Uri imageUri = null;
// 롤리팝 미만 버전에서는 data 인텐트에 찍은 사진의 uri 가 담겨온다.
if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
    imageUri = data.getData();
}else{
    imageUri = fileUri;
}
```

