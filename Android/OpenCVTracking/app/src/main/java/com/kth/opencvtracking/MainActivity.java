package com.kth.opencvtracking;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/*
** http://blog.vumy.kr/74
** http://bluebead38.blogspot.kr/2017/06/android-studio-opencv-tessseract-ocr.html
*/
public class MainActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "opencv";
    private static int countNum = 0;

    private int trackObject = 0;
    private boolean selectObject = false;
    private boolean leftFrameFlag = false;
    private boolean rightFrameFlag = false;
    private boolean startFlag = false;

    private Mat matCamera;
    private Mat hsv, mask, hue, backproj;
    private Mat hist;
    private Rect leftFrame, rightFrame;
    private Rect trackWindow;
    private Rect selection = null;
    private Point origin;

    private TextView count;
    private Spinner spinnerLine;
    private Button buttonReset;
    private ToggleButton buttonToggle;
    private LinearLayout cameraLayout;
    private CameraBridgeViewBase mOpenCvCameraView;

    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS  = {"android.permission.CAMERA"};


    static {
        System.loadLibrary("opencv_java3");
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        cameraLayout = (LinearLayout)findViewById(R.id.camera_preview);

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        count = (TextView)findViewById(R.id.count_press);
        spinnerLine = (Spinner)findViewById(R.id.spinner_line);
        buttonReset = (Button)findViewById(R.id.button_reset);
        buttonToggle = (ToggleButton)findViewById(R.id.button_switch);

        count.setText(Integer.toString(countNum));

        // Spinner list initialize
        ArrayAdapter<CharSequence> adapterLine = ArrayAdapter.createFromResource(this,
                R.array.line_array, android.R.layout.simple_spinner_item);
        adapterLine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLine.setAdapter(adapterLine);

        // Reset button listener
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countNum = 0;
                count.setText(Integer.toString(countNum));
            }
        });

        // Start/Stop ToggleButton listener
        buttonToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(getApplicationContext(), "Counting Started", Toast.LENGTH_LONG).show();
                    startFlag = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Counting Stopped", Toast.LENGTH_LONG).show();
                    startFlag = false;
                }
            }
        });

        // Spinner listener
        spinnerLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "Line " + (i+1) + " Selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResume :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        matCamera = new Mat();
        
        hsv = new Mat();
        hue = new Mat();
        mask = new Mat();

        hist = new Mat();
        backproj = new Mat();
        trackWindow = new Rect();
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matCamera = inputFrame.rgba();

        // Drawing Center line
        Imgproc.line(matCamera,
                new Point(matCamera.width() / 2, 0),
                new Point(matCamera.width() / 2, matCamera.height()),
                new Scalar(255, 0, 0), 10);

        // Dividing frames
        leftFrame = new Rect(new Point(0, 0),
                new Point(matCamera.width() / 2, matCamera.height()));
        rightFrame = new Rect(new Point(matCamera.width() / 2, 0),
                new Point(matCamera.width(), matCamera.height()));

        Imgproc.cvtColor(matCamera, hsv, Imgproc.COLOR_BGR2HSV);

        if(trackObject != 0) {
            int vmin = 10, vmax = 256, smin = 30;

            Core.inRange(hsv, new Scalar(0, smin, Math.min(vmin, vmax)),
                    new Scalar(180, 256, Math.max(vmin, vmax)), mask);

            hue.create(hsv.size(), hsv.depth());

            List<Mat> hueList = new LinkedList<Mat>();
            List<Mat> hsvList = new LinkedList<Mat>();
            hueList.add(hue);
            hsvList.add(hsv);

            MatOfInt ch = new MatOfInt(0, 0);

            Core.mixChannels(hsvList, hueList, ch);

            MatOfFloat histRange = new MatOfFloat(0, 180);

            if(trackObject < 0) {
                Mat subHue = hue.submat(selection);

                Imgproc.calcHist(Arrays.asList(subHue), new MatOfInt(0), new Mat(), hist, new MatOfInt(16), histRange);
                Core.normalize(hist, hist, 0, 255, Core.NORM_MINMAX);
                trackWindow = selection;
                trackObject = 1;
            }

            MatOfInt ch2 = new MatOfInt(0, 1);
            Imgproc.calcBackProject(Arrays.asList(hue), ch2, hist, backproj, histRange, 1);

            Core.bitwise_and(backproj, mask, backproj);

            // Tracking by Meanshift
            int trackBox = Video.meanShift(backproj, trackWindow,
                    new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 10, 1));

            if(trackWindow.area() <= 1) {
                trackObject = 0;
            }

            // Counting
            if(rightFrame.contains(trackWindow.tl()) && rightFrame.contains(trackWindow.br())) {
                rightFrameFlag = true;
            } else if(leftFrame.contains(trackWindow.tl()) && leftFrame.contains(trackWindow.br())) {
                leftFrameFlag = true;
            }

            if(leftFrameFlag && rightFrameFlag && startFlag) {
                countNum++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        count.setText(Integer.toString(countNum / 2));
                    }
                });
                leftFrameFlag = false;
                rightFrameFlag = false;
            }
        }

        if(selection != null)
            Imgproc.rectangle(matCamera, selection.tl(), selection.br(), new Scalar(0, 0, 255), 2);

        return matCamera;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(event.getX() > mOpenCvCameraView.getWidth()) {
            return false;
        }

        // Drawing rectangle
        if(selectObject) {
            selection.x = (int)Math.min(event.getX(), origin.x);
            selection.y = (int)Math.min(event.getY(), origin.y);
            selection.width = (int)Math.abs(event.getX() - origin.x);
            selection.height = (int)Math.abs(event.getY() - origin.y);
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            origin = new Point(event.getX(), event.getY());
            selection = new Rect((int)event.getX(), (int)event.getY(), 0, 0);
            selectObject = true;
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            selectObject = false;
            if(selection.width>0 && selection.height>0)
                trackObject = -1;
        }

        return super.onTouchEvent(event);
    }


    private boolean hasPermissions(String[] permissions) {
        int result;

        for (String perms : permissions){
            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED)
                return false;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}