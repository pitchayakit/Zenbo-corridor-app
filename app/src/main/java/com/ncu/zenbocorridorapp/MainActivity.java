package com.ncu.zenbocorridorapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.asus.robotframework.API.MotionControl;
import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.VisionConfig;
import com.asus.robotframework.API.WheelLights;
import com.asus.robotframework.API.results.DetectFaceResult;
import com.asus.robotframework.API.results.DetectPersonResult;
import com.asus.robotframework.API.results.GesturePointResult;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends RobotActivity {    private static RobotAPI mRobotApiStatic;
    public final static String DOMAIN = "E83AF3B5DB0D440095A8C1C785E942CE";
    private static String currentPosition;
    private static String mainPosition = "nlt office";
    private static CountDownTimer showExpression;
    private static Integer countFaceDetect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        showExpression = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                startDetectFace();
            }
        };

        mRobotApiStatic = new RobotAPI(getApplicationContext(), robotCallback);

        Button btOffice = findViewById(R.id.btOffice);
        btOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDetectFace();
                stopDetectFaceDelay();
                startActivity(new Intent(MainActivity.this, OfficeActivity.class));
            }
        });

        Button btMeetingRoom = findViewById(R.id.btMeetingRoom);
        btMeetingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDetectFace();
                stopDetectFaceDelay();
                startActivity(new Intent(MainActivity.this, MeetingRoomActivity.class));
            }
        });

        Button btProfessorOffice = findViewById(R.id.btProfessorOffice);
        btProfessorOffice.setEnabled(false);

        Button btClassroom = findViewById(R.id.btClassroom);
        setupButtons(btClassroom,"classroom b");

        Button btRestroom = findViewById(R.id.btRestroom);
        setupButtons(btRestroom,"restroom b");

        robotAPI.robot.speak("Hello. I am Zenbo. Nice to meet you.");
        startDetectFace();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // jump dialog domain
        robotAPI.robot.jumpToPlan(DOMAIN, "ThisPlanLaunchingThisApp");
        //robotAPI.robot.speak("What are you looking for?");
        //robotAPI.robot.setExpression(RobotFace.HIDEFACE);

        startDetectFaceDelay();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopDetectFace();
        stopDetectFaceDelay();

    }

    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);
            Log.d("ZenboGoToLocationOnResult", String.valueOf(result));
            if(cmd == 3 && currentPosition != mainPosition) {
                mRobotApiStatic.robot.speak("Reached");
                currentPosition = mainPosition;
                mRobotApiStatic.motion.goTo(mainPosition);
            }
            else if(cmd == 3 && currentPosition == mainPosition) {
                startDetectFace();
            }
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);
        }

        @Override
        public void initComplete() {
            super.initComplete();

        }

        @Override
        public void onDetectPersonResult(List<DetectPersonResult> resultList) {
            super.onDetectPersonResult(resultList);

        }

        @Override
        public void onGesturePoint(GesturePointResult result) {
            super.onGesturePoint(result);
        }

        @Override
        public void onDetectFaceResult(List<DetectFaceResult> resultList) {
            super.onDetectFaceResult(resultList);

            Log.d(TAG, "onDetectFaceResult: " + resultList.get(0));

            countFaceDetect++;
            if(countFaceDetect % 2 != 0) {
                mRobotApiStatic.robot.speak("Hello. I am Zenbo. Nice to meet you.");
                mRobotApiStatic.robot.setExpression(RobotFace.HIDEFACE);
                stopDetectFace();
                startDetectFaceDelay();
            }

        }
    };

    public static RobotCallback.Listen robotListenCallback = new RobotCallback.Listen() {
        @Override
        public void onFinishRegister() {

        }

        @Override
        public void onVoiceDetect(JSONObject jsonObject) {

        }

        @Override
        public void onSpeakComplete(String s, String s1) {

        }

        @Override
        public void onEventUserUtterance(JSONObject jsonObject) {

        }

        @Override
        public void onResult(JSONObject jsonObject) {

        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };

    public MainActivity() {
        super(robotCallback, robotListenCallback);
    }

    private void setupButtons (final Button button, final String location){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button == findViewById(R.id.btMeetingRoom)){
                    Intent GroupsIntent = new Intent(MainActivity.this,MeetingRoomActivity.class);
                    startActivity(GroupsIntent);
                }
                robotAPI.motion.goTo(location);
                currentPosition = location;
                stopDetectFace();
                stopDetectFaceDelay();
                mRobotApiStatic.robot.setExpression(RobotFace.DEFAULT_STILL);
            }
        });
    }

    private static void startDetectFace() {
        // start detect face
        VisionConfig.FaceDetectConfig config = new VisionConfig.FaceDetectConfig();
        config.enableDebugPreview = true;  // set to true if you need preview screen
        config.intervalInMS = 2000;
        config.enableDetectHead = false;
        mRobotApiStatic.vision.requestDetectFace(config);
        mRobotApiStatic.motion.remoteControlHead(MotionControl.Direction.Head.UP);
        mRobotApiStatic.robot.setExpression(RobotFace.DEFAULT_STILL);

    }

    public static void stopDetectFace() {
        // stop detect face
        mRobotApiStatic.vision.cancelDetectFace();
    }

    private static void startDetectFaceDelay() {
        mRobotApiStatic.motion.remoteControlHead(MotionControl.Direction.Head.UP);
        showExpression.start();
    }

    public static void stopDetectFaceDelay() {
        showExpression.cancel();
    }
}
