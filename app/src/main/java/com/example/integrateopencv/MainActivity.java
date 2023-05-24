package com.example.integrateopencv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends CameraActivity{

    JavaCameraView cameraBridgeViewBase;
    Mat gray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();
        cameraBridgeViewBase = findViewById(R.id.cameraView);
        //set lister
        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2(){
            @Override
            public void onCameraViewStarted(int width, int height) {
                gray = new Mat();
            }

            @Override
            public void onCameraViewStopped() {
                gray.release();
            }

            //this is called when the frame is captured by device's camera
            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                gray = inputFrame.gray();
                //for detecting edges
                Imgproc.Canny(gray, gray, 80, 100);

                //changing background to white
                Core.bitwise_not(gray, gray);

                return gray;
            }
        });

        if(OpenCVLoader.initDebug()) { //to check openCV is successfully loaded
            cameraBridgeViewBase.enableView();

        }
    }

    //resume when the app is in the foreground
    @Override
    protected void onResume() {
        super.onResume();
        cameraBridgeViewBase.enableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
    }

    //pause view when the app is in the background

    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();
    }


    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }

    private void getPermission() {
        if(checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]!=PackageManager.PERMISSION_GRANTED){
            getPermission();
        }
    }
}