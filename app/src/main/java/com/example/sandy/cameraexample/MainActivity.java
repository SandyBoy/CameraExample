package com.example.sandy.cameraexample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private Camera mcamera;
    private SurfaceView mPreView;
    private SurfaceHolder mholder;
    private Camera.PictureCallback mpictureCallback=new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {//将拍摄数据写入临时文件中
            File temFile=new File("/sdcard/tem.png");
            try {
                FileOutputStream fos=new FileOutputStream(temFile);
                fos.write(data);
                fos.close();
                Intent intent=new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("picPath",temFile.getAbsolutePath());
                startActivity(intent);
                MainActivity.this.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreView=(SurfaceView)findViewById(R.id.preview);
        mholder=mPreView.getHolder();
        mholder.addCallback(this);
        mPreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcamera.autoFocus(null);
            }
        });
    }

    public  void capture(View view){
        Camera.Parameters param=mcamera.getParameters();
        param.setPictureFormat(ImageFormat.JPEG);
        param.setPreviewSize(800,400);
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mcamera.autoFocus(new Camera.AutoFocusCallback(){


            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success){
                    mcamera.takePicture(null,null,mpictureCallback);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mcamera==null);
        mcamera=getCamera();
        if (mholder!=null){
            setStartPreView(mcamera,mholder);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();

    }

    /*
            *获取Camera对象
             */
    private Camera getCamera(){
        Camera camear;
        camear=Camera.open();
        return camear;
    }

    /*
    *开始预览相机
     */
    private  void setStartPreView(Camera camera,SurfaceHolder holder){
        try {
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /*
     *释放资源
      */
    private  void releaseCamera(){
        if (mcamera!=null){
            mcamera.setPreviewCallback(null);
            mcamera.stopPreview();
            mcamera.release();
            mcamera=null;
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
            setStartPreView(mcamera,mholder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    mcamera.stopPreview();
        setStartPreView(mcamera,mholder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    releaseCamera();
    }
}
