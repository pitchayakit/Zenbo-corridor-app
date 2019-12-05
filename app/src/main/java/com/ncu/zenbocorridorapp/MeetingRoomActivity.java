package com.ncu.zenbocorridorapp;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.asus.robotframework.API.MotionControl;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

public class MeetingRoomActivity extends RobotActivity {
    private CountDownTimer backToMainActivity;
    private Intent mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_room);

        robotAPI.motion.remoteControlHead(MotionControl.Direction.Head.UP);

        mainActivity = new Intent(MeetingRoomActivity.this, MainActivity.class);
        backToMainActivity = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                startActivity(mainActivity);
            }
        }.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //robotAPI.robot.setExpression(RobotFace.HIDEFACE);
    }

    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);
        }

        @Override
        public void initComplete() {
            super.initComplete();

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

    public MeetingRoomActivity() {
        super(robotCallback, robotListenCallback);
    }
}
