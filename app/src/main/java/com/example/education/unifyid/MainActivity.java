package com.example.education.unifyid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;


public class MainActivity extends ActionBarActivity {

    private static final String CAMERA_APP_TAG = "UnifyId camera APP";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout preview;
    private Uri fileUri;
    private CountDownTimer timer;
    static int photoCount = 0;
    private TextView picNo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView picNo = (TextView) findViewById(R.id.picNum);

        Button captureButton = (Button) findViewById(R.id.capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initializeTimer();
                        timer.start();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final int MILLISECONDS = 500;
                                try {
                                    mCamera.takePicture(null, null, mPicture);

                                    Thread.sleep(MILLISECONDS);
                                    mCamera.takePicture(null, null, mPicture);

                                    Thread.sleep(MILLISECONDS);
                                    mCamera.takePicture(null, null, mPicture);

                                    Thread.sleep(MILLISECONDS);
                                    mCamera.takePicture(null, null, mPicture);

                                    Thread.sleep(MILLISECONDS);
                                    mCamera.takePicture(null, null, mPicture);

                                    Thread.sleep(MILLISECONDS);
                                    mCamera.takePicture(null, null, mPicture);

                                    Thread.sleep(MILLISECONDS);
                                    mCamera.takePicture(null, null, mPicture);

                                    Thread.sleep(MILLISECONDS);
                                    mCamera.takePicture(null, null, mPicture);

                                    Thread.sleep(MILLISECONDS);
                                    mCamera.takePicture(null, null, mPicture);

                                    Thread.sleep(MILLISECONDS);
                                    mCamera.takePicture(null, null, mPicture);

                                    Thread.sleep(MILLISECONDS);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }
        );

        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);

        FrameLayout preview = (FrameLayout) findViewById(R.id.preview);
        preview.addView(mPreview);
    }

    public void incrementCounter(){
        photoCount++;
        TextView picNum = (TextView) findViewById(R.id.picNum);
        picNum.setText(String.valueOf(photoCount));
    }

    public static Camera getCameraInstance(){
            int cameraCount = 0;
            Camera cam = null;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        cam = Camera.open(camIdx);
                    } catch (RuntimeException e) {
                        Log.e("UNIFYID", "Camera failed to open;", e);
                    }
                }
            }

            return cam;

    }


    @Override
    protected void onPause(){
        super.onPause();
        releaseCamera();
        if(mPreview != null && preview !=null) {

            preview.removeView(mPreview);
            mPreview = null;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(mCamera == null){
            mCamera = getCameraInstance();
        }

        if(mPreview == null && preview !=null){
            mPreview = new CameraPreview(getApplicationContext(),mCamera);
            preview.addView(mPreview);
        }


    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.stopPreview();
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            if (pictureFile == null){
                Log.d(CAMERA_APP_TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                saveCapture(data);
            } catch (FileNotFoundException e) {
                Log.d(CAMERA_APP_TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(CAMERA_APP_TAG, "Error accessing file: " + e.getMessage());
            }
            mCamera.startPreview();
            incrementCounter();


        }
    };

    private void saveCapture(byte[] data) {

        SaveSharedPreferences prefs = SaveSharedPreferences.getInstance(this,"Picture_pref"); //provide context & preferences name.

        //Storing the image  inside shared preferences
        prefs.putString("userpic" + photoCount, String.valueOf(data));
        prefs.commit();
        //Retrieving image from encrypted shared preferences
        String image = prefs.getString("userpic" + photoCount, "default_username");
        Log.d("pictures", image);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }


    private void initializeTimer() {
        final TextView timerView=(TextView)findViewById(R.id.timer);
        timer = new CountDownTimer(6000, 1000) {

            public void onTick(long millisUntilFinished) {
                if(photoCount <= 10) {
                    timerView.setText("" + millisUntilFinished / 1000);
                }
                else{
                    timerView.setText("");
                }
            }

            public void onFinish() {
                    timer.cancel();
            }
        };
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        Camera.Parameters parameters = camera.getParameters();
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
        parameters.setRotation(result);
        camera.setParameters(parameters);
    }


    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }


    private static File getOutputMediaFile(int type){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DCIM), "Camera");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Camera", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public SurfaceHolder getSurfaceHolder(){
            return mHolder;
        }

        public void myStopPreview(){
            if(mCamera != null) {
                mCamera.stopPreview();
                mHolder.removeCallback(this);
                mCamera.release();
                mCamera = null;
            }
        }

        public void myStartPreview(){
            try {

                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                mCamera.release();
                mCamera = null;
                Log.d(CAMERA_APP_TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public CameraPreview(Context context, Camera camera) {

            super(context);
            mCamera = camera;

            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {

            myStartPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {

        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {


            if (mHolder.getSurface() == null){

                return;
            }

            try {
                mCamera.stopPreview();
            } catch (Exception e){

            }


            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                setCameraDisplayOrientation(MainActivity.this, Camera.CameraInfo.CAMERA_FACING_FRONT, mCamera);

            } catch (Exception e){
                Log.d(CAMERA_APP_TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }


}