/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package od.team.playpiano;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.telecom.Call;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Activity for peer connection call setup, call waiting
 * and call view.
 */
public class CallActivity extends Activity implements AppRTCClient.SignalingEvents,
        PeerConnectionClient.PeerConnectionEvents,
        CallFragment.OnCallEvents {
    private static final String TAG = CallActivity.class.getSimpleName();

    public static final String EXTRA_ROOMID = "org.appspot.apprtc.ROOMID";
    public static final String EXTRA_URLPARAMETERS = "org.appspot.apprtc.URLPARAMETERS";
    public static final String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
    public static final String EXTRA_SCREENCAPTURE = "org.appspot.apprtc.SCREENCAPTURE";
    public static final String EXTRA_CAMERA2 = "org.appspot.apprtc.CAMERA2";
    public static final String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";
    public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED =
            "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER";
    public static final String EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE";
    public static final String EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC";
    public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "org.appspot.apprtc.CAPTURETOTEXTURE";
    public static final String EXTRA_FLEXFEC_ENABLED = "org.appspot.apprtc.FLEXFEC";
    public static final String EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED =
            "org.appspot.apprtc.NOAUDIOPROCESSING";
    public static final String EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP";
    public static final String EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = "org.appspot.apprtc.DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = "org.appspot.apprtc.DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = "org.appspot.apprtc.DISABLE_BUILT_IN_NS";
    public static final String EXTRA_ENABLE_LEVEL_CONTROL = "org.appspot.apprtc.ENABLE_LEVEL_CONTROL";
    public static final String EXTRA_DISABLE_WEBRTC_AGC_AND_HPF =
            "org.appspot.apprtc.DISABLE_WEBRTC_GAIN_CONTROL";
    public static final String EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD";
    public static final String EXTRA_TRACING = "org.appspot.apprtc.TRACING";
    public static final String EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE";
    public static final String EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME";
    public static final String EXTRA_VIDEO_FILE_AS_CAMERA = "org.appspot.apprtc.VIDEO_FILE_AS_CAMERA";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";
    public static final String EXTRA_USE_VALUES_FROM_INTENT =
            "org.appspot.apprtc.USE_VALUES_FROM_INTENT";
    public static final String EXTRA_DATA_CHANNEL_ENABLED = "org.appspot.apprtc.DATA_CHANNEL_ENABLED";
    public static final String EXTRA_ORDERED = "org.appspot.apprtc.ORDERED";
    public static final String EXTRA_MAX_RETRANSMITS_MS = "org.appspot.apprtc.MAX_RETRANSMITS_MS";
    public static final String EXTRA_MAX_RETRANSMITS = "org.appspot.apprtc.MAX_RETRANSMITS";
    public static final String EXTRA_PROTOCOL = "org.appspot.apprtc.PROTOCOL";
    public static final String EXTRA_NEGOTIATED = "org.appspot.apprtc.NEGOTIATED";
    public static final String EXTRA_ID = "org.appspot.apprtc.ID";

    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;

    // List of mandatory application permissions.
    private static final String[] MANDATORY_PERMISSIONS = {"android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO", "android.permission.INTERNET"};

    // Peer connection statistics callback period in ms.
    private static final int STAT_CALLBACK_PERIOD = 1000;

    private class ProxyRenderer implements VideoRenderer.Callbacks {
        private VideoRenderer.Callbacks target;

        synchronized public void renderFrame(VideoRenderer.I420Frame frame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.");
                VideoRenderer.renderFrameDone(frame);
                return;
            }

            target.renderFrame(frame);
        }

        synchronized public void setTarget(VideoRenderer.Callbacks target) {
            this.target = target;
        }
    }

    private final ProxyRenderer remoteProxyRenderer = new ProxyRenderer();
    private final ProxyRenderer localProxyRenderer = new ProxyRenderer();
    private PeerConnectionClient peerConnectionClient = null;
    private AppRTCClient appRtcClient;
    private AppRTCClient.SignalingParameters signalingParameters;
    private AppRTCAudioManager audioManager = null;
    private EglBase rootEglBase;
    private SurfaceViewRenderer pipRenderer;
    private SurfaceViewRenderer fullscreenRenderer;
    private VideoFileRenderer videoFileRenderer;
    private final List<VideoRenderer.Callbacks> remoteRenderers =
            new ArrayList<VideoRenderer.Callbacks>();
    private Toast logToast;
    private boolean commandLineRun;
    private int runTimeMs;
    private boolean activityRunning;
    private AppRTCClient.RoomConnectionParameters roomConnectionParameters;
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    private boolean iceConnected;
    private boolean isError;
    private boolean callControlFragmentVisible = true;
    private long callStartedTimeMs = 0;
    private boolean micEnabled = true;
    private boolean screencaptureEnabled = false;
    private static Intent mediaProjectionPermissionResultData;
    private static int mediaProjectionPermissionResultCode;
    // True if local view is in the fullscreen renderer.
    private boolean isSwappedFeeds;

    // Controls
    private CallFragment callFragment;
    private HudFragment hudFragment;
    private CpuMonitor cpuMonitor;

    /**
     * 내가 추가 시작
     **/
    ImageView opponent_imageView;
    ImageView opponent_imageView1;
    boolean isOpoonentClicked = false;
    boolean isOpoonentClicked1 = false;

    MediaPlayer mediaPlayer;
    ImageView play_pause_btn_imageView;
    ImageView stop_imageView;
    boolean isPlaying = false;
    int seek_position = 0;

    ImageView hand_image;     //손모양 이미지, 손가락으로 버튼을 눌러 소리를 낼 때 마다 누른 손가락에 맞는 위치에 파란색 표시된 이미지로 변경
    ImageView hand_left1_image;
    ImageView hand_left2_image;
    ImageView hand_left3_image;
    ImageView hand_left4_image;
    ImageView hand_left5_image;
    ImageView hand_right1_image;
    ImageView hand_right2_image;
    ImageView hand_right3_image;
    ImageView hand_right4_image;
    ImageView hand_right5_image;

    TextView code_text;

    SoundPool piano_sound1, piano_sound2, piano_sound3, piano_sound4, piano_sound5,
            piano_sound6, piano_sound7, piano_sound8, piano_sound9, piano_sound10;
    int piano_soundId1, piano_soundId2, piano_soundId3, piano_soundId4, piano_soundId5,
            piano_soundId6, piano_soundId7, piano_soundId8, piano_soundId9, piano_soundId10;

    boolean isRaspOn = false;
    ArrayList<String> raspDataToServer = new ArrayList<>();

    long startTime;
    double cutTime;
    String left;
    String right;

    Handler handler;

    // 음이름 ㅋㅋ
    TextView Umm_text;

    // 수업종료
    Button button7;

    /**
     * 내가 추가 끝
     **/

    String user_job;

    boolean isShownPlay = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));

        user_job = getIntent().getStringExtra("user_job");
        Log.d("sdffds", "Call " + user_job + "!!!");

        // Set window styles for fullscreen-window size. Needs to be done before
        // adding content.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON
                | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
        setContentView(R.layout.activity_call);


        /** 내가 추가 시작**/
        LoginActivity.soundOn = 1;
        soundPoolInit();
        raspSignalON();

        mediaPlayer = MediaPlayer.create(CallActivity.this, R.raw.bgm);

        hand_image = findViewById(R.id.hand_imageView);
        //왼손용 이미지
        hand_left1_image = findViewById(R.id.hand_left1_imageView);
        hand_left2_image = findViewById(R.id.hand_left2_imageView);
        hand_left3_image = findViewById(R.id.hand_left3_imageView);
        hand_left4_image = findViewById(R.id.hand_left4_imageView);
        hand_left5_image = findViewById(R.id.hand_left5_imageView);
        //오른손용 이미지
        hand_right1_image = findViewById(R.id.hand_right1_imageView);
        hand_right2_image = findViewById(R.id.hand_right2_imageView);
        hand_right3_image = findViewById(R.id.hand_right3_imageView);
        hand_right4_image = findViewById(R.id.hand_right4_imageView);
        hand_right5_image = findViewById(R.id.hand_right5_imageView);

        play_pause_btn_imageView = findViewById(R.id.play_pause_btn_imageView);
        stop_imageView = findViewById(R.id.stop_imageView);

        if (user_job.equals("student")) {
            play_pause_btn_imageView.setVisibility(View.INVISIBLE);
            stop_imageView.setVisibility(View.INVISIBLE);
        }

        if (play_pause_btn_imageView.getVisibility() == View.VISIBLE) {
            play_pause_btn_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlaying == false) {
                        sendMsg("play@@play");
                    } else {
                        sendMsg("pause@@pause");
                    }
                }
            });
        }
        if (stop_imageView.getVisibility() == View.VISIBLE) {
            stop_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMsg("stop@@stop");
                }
            });
        }


        code_text = findViewById(R.id.code_text);

        Umm_text = findViewById(R.id.Umm_text);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        Log.d("핸들러", String.valueOf(msg.what) + "   ppppppppppppp");
//                        Umm_text.setText("1번");
                        Umm_text.setVisibility(View.VISIBLE);
                        sendMsg("Umm@@p1");
                        changeHandImage(1);
                        break;
                    case 2:
                        Log.d("핸들러", String.valueOf(msg.what) + "   ppppppppppppp");
//                        Umm_text.setText("2번");
                        Umm_text.setVisibility(View.VISIBLE);
                        sendMsg("Umm@@p2");
                        changeHandImage(2);
                        break;
                    case 3:
                        Log.d("핸들러", String.valueOf(msg.what) + "   ppppppppppppp");
//                        Umm_text.setText("3번");
                        Umm_text.setVisibility(View.VISIBLE);
                        sendMsg("Umm@@p3");
                        changeHandImage(3);
                        break;
                    case 4:
                        Log.d("핸들러", String.valueOf(msg.what) + "   ppppppppppppp");
//                        Umm_text.setText("4번");
                        Umm_text.setVisibility(View.VISIBLE);
                        sendMsg("Umm@@p4");
                        changeHandImage(4);
                        break;
                    case 5:
                        Log.d("핸들러", String.valueOf(msg.what) + "   ppppppppppppp");
//                        Umm_text.setText("5번");
                        Umm_text.setVisibility(View.VISIBLE);
                        sendMsg("Umm@@p5");
                        changeHandImage(5);
                        break;
                    case 11:
                        Log.d("핸들러", String.valueOf(msg.what) + "   ppppppppppppp");
//                        Umm_text.setText("5번");
                        Umm_text.setVisibility(View.VISIBLE);
                        sendMsg("Umm@@p6");
                        changeHandImage(6);
                        break;
                    case 12:
                        Log.d("핸들러", String.valueOf(msg.what) + "   ppppppppppppp");
//                        Umm_text.setText("5번");
                        Umm_text.setVisibility(View.VISIBLE);
                        sendMsg("Umm@@p7");
                        changeHandImage(7);
                        break;
                    case 13:
                        Log.d("핸들러", String.valueOf(msg.what) + "   ppppppppppppp");
//                        Umm_text.setText("5번");
                        Umm_text.setVisibility(View.VISIBLE);
                        sendMsg("Umm@@p8");
                        changeHandImage(8);
                        break;
                    case 14:
                        Log.d("핸들러", String.valueOf(msg.what) + "   ppppppppppppp");
//                        Umm_text.setText("5번");
                        Umm_text.setVisibility(View.VISIBLE);
                        sendMsg("Umm@@p9");
                        changeHandImage(9);
                        break;
                    case 15:
                        Log.d("핸들러", String.valueOf(msg.what) + "   ppppppppppppp");
//                        Umm_text.setText("5번");
                        Umm_text.setVisibility(View.VISIBLE);
                        sendMsg("Umm@@p10");
                        changeHandImage(10);
                        break;

                }

            }
        };
        button7 = findViewById(R.id.button7);

        if (user_job.equals("student")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button7.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }).start();
        }

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg("close@@close");
            }
        });
        /** 내가 추가 끝**/

        iceConnected = false;
        signalingParameters = null;

        // Create UI controls.
        pipRenderer = (SurfaceViewRenderer) findViewById(R.id.pip_video_view);
        fullscreenRenderer = (SurfaceViewRenderer) findViewById(R.id.fullscreen_video_view);
        callFragment = new CallFragment();
        hudFragment = new HudFragment();


        // Show/hide call control fragment on view click.
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCallControlFragmentVisibility();
            }
        };

        // Swap feeds on pip view click.
        pipRenderer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwappedFeeds(!isSwappedFeeds);
            }
        });

        fullscreenRenderer.setOnClickListener(listener);
        remoteRenderers.add(remoteProxyRenderer);

        final Intent intent = getIntent();


        // Create video renderers.
        rootEglBase = EglBase.create();
        pipRenderer.init(rootEglBase.getEglBaseContext(), null);
        pipRenderer.setScalingType(ScalingType.SCALE_ASPECT_FIT);
        String saveRemoteVideoToFile = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);

        // When saveRemoteVideoToFile is set we save the video from the remote to a file.
        if (saveRemoteVideoToFile != null) {
            int videoOutWidth = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
            int videoOutHeight = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
            try {
                videoFileRenderer = new VideoFileRenderer(
                        saveRemoteVideoToFile, videoOutWidth, videoOutHeight, rootEglBase.getEglBaseContext());
                remoteRenderers.add(videoFileRenderer);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to open video file for output: " + saveRemoteVideoToFile, e);
            }
        }
        fullscreenRenderer.init(rootEglBase.getEglBaseContext(), null);
        fullscreenRenderer.setScalingType(ScalingType.SCALE_ASPECT_FILL);

        pipRenderer.setZOrderMediaOverlay(true);
        pipRenderer.setEnableHardwareScaler(true /* enabled */);
        fullscreenRenderer.setEnableHardwareScaler(true /* enabled */);
        // Start with local feed in fullscreen and swap it to the pip when the call is connected.
        if (user_job.equals("teacher")) {
            setSwappedFeeds(false /* isSwappedFeeds */);
            Log.d("sdsdf", "선생님은 안바뀜");
        }

        // Check for mandatory permissions.
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                logAndToast("Permission " + permission + " is not granted");
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }

        Uri roomUri = intent.getData();
        if (roomUri == null) {
            logAndToast(getString(R.string.missing_url));
            Log.e(TAG, "Didn't get any URL in intent!");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // Get Intent parameters.
        String roomId = intent.getStringExtra(EXTRA_ROOMID);
        Log.d(TAG, "Room ID: " + roomId);
        if (roomId == null || roomId.length() == 0) {
            logAndToast(getString(R.string.missing_url));
            Log.e(TAG, "Incorrect room ID in intent!");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
        boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false);

        int videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0);
        int videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0);

        screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREENCAPTURE, false);
        // If capturing format is not specified for screencapture, use screen resolution.
        if (screencaptureEnabled && videoWidth == 0 && videoHeight == 0) {
            DisplayMetrics displayMetrics = getDisplayMetrics();
            videoWidth = displayMetrics.widthPixels;
            videoHeight = displayMetrics.heightPixels;
        }
        PeerConnectionClient.DataChannelParameters dataChannelParameters = null;
        if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, false)) {
            dataChannelParameters = new PeerConnectionClient.DataChannelParameters(intent.getBooleanExtra(EXTRA_ORDERED, true),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(EXTRA_PROTOCOL),
                    intent.getBooleanExtra(EXTRA_NEGOTIATED, false), intent.getIntExtra(EXTRA_ID, -1));
        }
        peerConnectionParameters =
                new PeerConnectionClient.PeerConnectionParameters(intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), loopback,
                        tracing, videoWidth, videoHeight, intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
                        intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0), intent.getStringExtra(EXTRA_VIDEOCODEC),
                        intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true),
                        intent.getBooleanExtra(EXTRA_FLEXFEC_ENABLED, false),
                        intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0), intent.getStringExtra(EXTRA_AUDIOCODEC),
                        intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false),
                        intent.getBooleanExtra(EXTRA_ENABLE_LEVEL_CONTROL, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false), dataChannelParameters);
        commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);
        runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0);

        Log.d(TAG, "VIDEO_FILE: '" + intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA) + "'");

        // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the
        // standard WebSocketRTCClient.
        if (loopback || !DirectRTCClient.IP_PATTERN.matcher(roomId).matches()) {
            appRtcClient = new WebSocketRTCClient(this);
        } else {
            Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.");
            appRtcClient = new DirectRTCClient(this);
        }
        // Create connection parameters.
        String urlParameters = intent.getStringExtra(EXTRA_URLPARAMETERS);
        roomConnectionParameters =
                new AppRTCClient.RoomConnectionParameters(roomUri.toString(), roomId, loopback, urlParameters);

        // Create CPU monitor
        cpuMonitor = new CpuMonitor(this);
        hudFragment.setCpuMonitor(cpuMonitor);

        // Send intent arguments to fragments.
        callFragment.setArguments(intent.getExtras());
        hudFragment.setArguments(intent.getExtras());
        // Activate call and HUD fragments and start the call.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.call_fragment_container, callFragment);
//    ft.add(R.id.hud_fragment_container, hudFragment);
        ft.commit();

        // For command line execution run connection for <runTimeMs> and exit.
        if (commandLineRun && runTimeMs > 0) {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    disconnect();
                }
            }, runTimeMs);
        }

        peerConnectionClient = PeerConnectionClient.getInstance();
        if (loopback) {
            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
            options.networkIgnoreMask = 0;
            peerConnectionClient.setPeerConnectionFactoryOptions(options);
        }
        peerConnectionClient.createPeerConnectionFactory(
                getApplicationContext(), peerConnectionParameters, CallActivity.this);

        if (screencaptureEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startScreenCapture();
        } else {
            startCall();
        }
    }

    @TargetApi(17)
    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = null;
        try {
            displayMetrics = new DisplayMetrics();
            WindowManager windowManager =
                    (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        } catch (Exception e) {

        }

        return displayMetrics;
    }

    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }

    @TargetApi(21)
    private void startScreenCapture() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getApplication().getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
            return;
        mediaProjectionPermissionResultCode = resultCode;
        mediaProjectionPermissionResultData = data;
        startCall();
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && getIntent().getBooleanExtra(EXTRA_CAMERA2, true);
    }

    private boolean captureToTexture() {
        return getIntent().getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false);
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    @TargetApi(21)
    private VideoCapturer createScreenCapturer() {
        if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
            reportError("User didn't give permission to capture the screen.");
            return null;
        }
        return new ScreenCapturerAndroid(
                mediaProjectionPermissionResultData, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                reportError("User revoked permission to capture the screen.");
            }
        });
    }

    // Activity interfaces
    @Override
    public void onStop() {
        super.onStop();
        activityRunning = false;
        // Don't stop the video when using screencapture to allow user to show other apps to the remote
        // end.
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient.stopVideoSource();
        }
        cpuMonitor.pause();
        Log.d("신동휘", "씨발");
    }

    @Override
    public void onStart() {
        super.onStart();
        activityRunning = true;
        // Video is not paused for screencapture. See onPause.
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient.startVideoSource();
        }
        cpuMonitor.resume();
    }

    @Override
    protected void onDestroy() {
        Thread.setDefaultUncaughtExceptionHandler(null);
        disconnect();
        if (logToast != null) {
            logToast.cancel();
        }
        activityRunning = false;
        rootEglBase.release();
        raspSignalOFF();

        super.onDestroy();
    }

    // CallFragment.OnCallEvents interface implementation.
    @Override
    public void onCallHangUp() {
        disconnect();
    }

    @Override
    public void onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
        }
    }

    @Override
    public void onVideoScalingSwitch(ScalingType scalingType) {
        fullscreenRenderer.setScalingType(scalingType);
    }

    @Override
    public void onCaptureFormatChange(int width, int height, int framerate) {
        if (peerConnectionClient != null) {
            peerConnectionClient.changeCaptureFormat(width, height, framerate);
        }
    }

    @Override
    public boolean onToggleMic() {
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled;
            peerConnectionClient.setAudioEnabled(micEnabled);
        }
        return micEnabled;
    }

    // Helper functions.
    private void toggleCallControlFragmentVisibility() {
        if (!iceConnected || !callFragment.isAdded()) {
            return;
        }
        // Show/hide call control fragment
        callControlFragmentVisible = !callControlFragmentVisible;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (callControlFragmentVisible) {
            ft.show(callFragment);
            ft.show(hudFragment);
        } else {
            ft.hide(callFragment);
            ft.hide(hudFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void startCall() {
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }
        callStartedTimeMs = System.currentTimeMillis();

        // Start room connection.
        logAndToast(getString(R.string.connecting_to, roomConnectionParameters.roomUrl));
        appRtcClient.connectToRoom(roomConnectionParameters);

        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        audioManager = AppRTCAudioManager.create(getApplicationContext());
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d(TAG, "Starting the audio manager...");
        audioManager.start(new AppRTCAudioManager.AudioManagerEvents() {
            // This method will be called each time the number of available audio
            // devices has changed.
            @Override
            public void onAudioDeviceChanged(
                    AppRTCAudioManager.AudioDevice audioDevice, Set<AppRTCAudioManager.AudioDevice> availableAudioDevices) {
                onAudioManagerDevicesChanged(audioDevice, availableAudioDevices);
            }
        });
    }

    // Should be called from UI thread
    private void callConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        Log.i(TAG, "Call connected: delay=" + delta + "ms");
        if (peerConnectionClient == null || isError) {
            Log.w(TAG, "Call is connected in closed or error state");
            return;
        }
        // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
        if (user_job.equals("teacher"))
            setSwappedFeeds(true/* isSwappedFeeds */);
        else
            setSwappedFeeds(false/* isSwappedFeeds */);
        Log.d("sdsdf", "연결이 끊어짐");
    }

    // This method is called when the audio manager reports audio device change,
    // e.g. from wired headset to speakerphone.
    private void onAudioManagerDevicesChanged(
            final AppRTCAudioManager.AudioDevice device, final Set<AppRTCAudioManager.AudioDevice> availableDevices) {
        Log.d(TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
                + "selected: " + device);
        // TODO(henrika): add callback handler.
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private void disconnect() {
        activityRunning = false;
        remoteProxyRenderer.setTarget(null);
        localProxyRenderer.setTarget(null);
        if (appRtcClient != null) {
            appRtcClient.disconnectFromRoom();
            appRtcClient = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }
        if (pipRenderer != null) {
            pipRenderer.release();
            pipRenderer = null;
        }
        if (videoFileRenderer != null) {
            videoFileRenderer.release();
            videoFileRenderer = null;
        }
        if (fullscreenRenderer != null) {
            fullscreenRenderer.release();
            fullscreenRenderer = null;
        }
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        if (iceConnected && !isError) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void disconnectWithErrorMessage(final String errorMessage) {
        if (commandLineRun || !activityRunning) {
            Log.e(TAG, "Critical error: " + errorMessage);
            disconnect();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getText(R.string.channel_error_title))
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setNeutralButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    disconnect();
                                }
                            })
                    .create()
                    .show();
        }
    }

    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    private void reportError(final String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError) {
                    isError = true;
                    disconnectWithErrorMessage(description);
                }
            }
        });
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer = null;
        String videoFileAsCamera = getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);
        if (videoFileAsCamera != null) {
            try {
                videoCapturer = new FileVideoCapturer(videoFileAsCamera);
            } catch (IOException e) {
                reportError("Failed to open video file for emulated camera");
                return null;
            }
        } else if (screencaptureEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createScreenCapturer();
        } else if (useCamera2()) {
            if (!captureToTexture()) {
                reportError(getString(R.string.camera2_texture_only_error));
                return null;
            }

            Logging.d(TAG, "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            Logging.d(TAG, "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            reportError("Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    private void setSwappedFeeds(boolean isSwappedFeeds) {
        Logging.d(TAG, "setSwappedFeeds: " + isSwappedFeeds);
        this.isSwappedFeeds = isSwappedFeeds;
        localProxyRenderer.setTarget(isSwappedFeeds ? fullscreenRenderer : pipRenderer);
        remoteProxyRenderer.setTarget(isSwappedFeeds ? pipRenderer : fullscreenRenderer);
        fullscreenRenderer.setMirror(isSwappedFeeds);
        pipRenderer.setMirror(!isSwappedFeeds);
    }

    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and
    // are routed to UI thread.
    private void onConnectedToRoomInternal(final AppRTCClient.SignalingParameters params) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;

        signalingParameters = params;
        logAndToast("Creating peer connection, delay=" + delta + "ms");
        VideoCapturer videoCapturer = null;
        if (peerConnectionParameters.videoCallEnabled) {
            videoCapturer = createVideoCapturer();
        }
        peerConnectionClient.createPeerConnection(rootEglBase.getEglBaseContext(), localProxyRenderer,
                remoteRenderers, videoCapturer, signalingParameters);

        if (signalingParameters.initiator) {
            logAndToast("Creating OFFER...");
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createOffer();
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(params.offerSdp);
                logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    @Override
    public void onConnectedToRoom(final AppRTCClient.SignalingParameters params) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onConnectedToRoomInternal(params);
            }
        });
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                    return;
                }
                logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms");
                peerConnectionClient.setRemoteDescription(sdp);
                if (!signalingParameters.initiator) {
                    logAndToast("Creating ANSWER...");
                    // Create answer. Answer SDP will be sent to offering client in
                    // PeerConnectionEvents.onLocalDescription event.
                    peerConnectionClient.createAnswer();
                }
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.addRemoteIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.removeRemoteIceCandidates(candidates);
            }
        });
    }

    @Override
    public void onChannelClose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Remote end hung up; dropping PeerConnection");
                disconnect();
            }
        });
    }

    @Override
    public void onChannelError(final String description) {
        reportError(description);
    }

    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    logAndToast("Sending " + sdp.type + ", delay=" + delta + "ms");
                    if (signalingParameters.initiator) {
                        appRtcClient.sendOfferSdp(sdp);
                    } else {
                        appRtcClient.sendAnswerSdp(sdp);
                    }
                }
                if (peerConnectionParameters.videoMaxBitrate > 0) {
                    Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
                    peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
                }
            }
        });
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidate(candidate);
                }
            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidateRemovals(candidates);
                }
            }
        });
    }

    @Override
    public void onIceConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("ICE connected, delay=" + delta + "ms");
                iceConnected = true;
                callConnected();
            }
        });
    }

    @Override
    public void onIceDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("ICE disconnected");
                iceConnected = false;
                disconnect();
            }
        });
    }

    @Override
    public void onPeerConnectionClosed() {
    }

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError && iceConnected) {
                    hudFragment.updateEncoderStatistics(reports);
                }
            }
        });
    }

    @Override
    public void onPeerConnectionError(final String description) {
        reportError(description);
    }


    public void soundPoolInit() {
        piano_sound1 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);// maxStreams, streamType, srcQuality
        piano_sound2 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);// maxStreams, streamType, srcQuality
        piano_sound3 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);// maxStreams, streamType, srcQuality
        piano_sound4 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);// maxStreams, streamType, srcQuality
        piano_sound5 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);// maxStreams, streamType, srcQuality
        piano_sound6 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);// maxStreams, streamType, srcQuality
        piano_sound7 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);// maxStreams, streamType, srcQuality
        piano_sound8 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);// maxStreams, streamType, srcQuality
        piano_sound9 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);// maxStreams, streamType, srcQuality
        piano_sound10 = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);// maxStreams, streamType, srcQuality

        piano_soundId1 = piano_sound1.load(CallActivity.this, R.raw.p1, 1);
        piano_soundId2 = piano_sound2.load(CallActivity.this, R.raw.p2, 1);
        piano_soundId3 = piano_sound3.load(CallActivity.this, R.raw.p3, 1);
        piano_soundId4 = piano_sound4.load(CallActivity.this, R.raw.p4, 1);
        piano_soundId5 = piano_sound5.load(CallActivity.this, R.raw.p5, 1);
        piano_soundId6 = piano_sound6.load(CallActivity.this, R.raw.p6, 1);
        piano_soundId7 = piano_sound7.load(CallActivity.this, R.raw.p7, 1);
        piano_soundId8 = piano_sound8.load(CallActivity.this, R.raw.p8, 1);
        piano_soundId9 = piano_sound9.load(CallActivity.this, R.raw.p9, 1);
        piano_soundId10 = piano_sound10.load(CallActivity.this, R.raw.p10, 1);
    }

    public void raspSignalON() {

        /** 여기 고쳐야함 / 연주화면에서 선생님 손 이미지가 안바뀜 1245번줄을 1268번줄 조건에 합쳐야함
         *
         *  if(a == '1' && !LobbyActivity.user_id.equals("teacher")) 이걸로 고치자
         *
         * **/


        isRaspOn = true;

        raspDataToServer.clear();

        /** 왼손 **/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    while (isRaspOn) {
//                                Log.d("ㅇㅇㅇ","메세지 기다림");

                        if (LobbyActivity.left_in == null) {
                            Log.d("sdffsd", "sfdfsd");
                        }
                        left = String.valueOf(LobbyActivity.left_in.readByte());
                        if (LoginActivity.soundOn == 1) {

                            char a = (char) Integer.parseInt(left);
                            Log.d("tutorialㅇㅇㅇ", "메세지 " + a);

                            if (LobbyActivity.CurrentInstrument == LobbyActivity.PIANO_FLAG) {
                                if (a == '1') {

                                    if (user_job.equals("student")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound1.play(piano_soundId1, 1.0f, 1.0f, 0, 0, 1.0f);
                                                Log.d("tutorialㅇㅇㅇ", "메세지 1번 실ㄹ행함");

                                            }
                                        }).start();

                                    }

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 1;
                                    handler.sendMessage(messageId);

                                } else if (a == '2') {
                                    if (user_job.equals("student")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound2.play(piano_soundId2, 1.0f, 1.0f, 0, 0, 1.0f);

                                            }
                                        }).start();
                                    }
                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 2;
                                    handler.sendMessage(messageId);

                                } else if (a == '3') {
                                    if (user_job.equals("student")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound3.play(piano_soundId3, 1.0f, 1.0f, 0, 0, 1.0f);

                                            }
                                        }).start();
                                    }


                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 3;
                                    handler.sendMessage(messageId);

                                } else if (a == '4') {
                                    if (user_job.equals("student")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound4.play(piano_soundId4, 1.0f, 1.0f, 0, 0, 1.0f);

                                            }
                                        }).start();
                                    }


                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 4;
                                    handler.sendMessage(messageId);

                                } else if (a == '5') {
                                    if (user_job.equals("student")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound5.play(piano_soundId5, 1.0f, 1.0f, 0, 0, 1.0f);

                                            }
                                        }).start();

                                    }

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 5;
                                    handler.sendMessage(messageId);
                                }
                            }

                        }


                    }
                } catch (Exception e) {
                    Log.d("sdfsdf", "왼손 에러: " + e.getMessage());
                    e.printStackTrace();
                }


            }
        }).start();

        /** 오른손 **/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    while (isRaspOn) {
//                                Log.d("ㅇㅇㅇ","메세지 기다림");

                        if (LobbyActivity.right_in == null) {
                            Log.d("sdffsd", "sfdfsd");
                        }
                        right = String.valueOf(LobbyActivity.right_in.readByte());
                        if (LoginActivity.soundOn == 1) {

                            char a = (char) Integer.parseInt(right);
                            Log.d("tutorialㅇㅇㅇ", "메세지 " + a);

                            if (LobbyActivity.CurrentInstrument == LobbyActivity.PIANO_FLAG) {
                                if (a == '1') {

                                    if (user_job.equals("student")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound6.play(piano_soundId6, 1.0f, 1.0f, 0, 0, 1.0f);
                                            }
                                        }).start();
                                    }

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 11;
                                    handler.sendMessage(messageId);

                                } else if (a == '2') {
                                    if (user_job.equals("student")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound7.play(piano_soundId7, 1.0f, 1.0f, 0, 0, 1.0f);

                                            }
                                        }).start();
                                    }
                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 12;
                                    handler.sendMessage(messageId);

                                } else if (a == '3') {

                                    if (user_job.equals("student")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound8.play(piano_soundId8, 1.0f, 1.0f, 0, 0, 1.0f);

                                            }
                                        }).start();
                                    }


                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 13;
                                    handler.sendMessage(messageId);

                                } else if (a == '4') {
                                    if (user_job.equals("student")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound9.play(piano_soundId9, 1.0f, 1.0f, 0, 0, 1.0f);

                                            }
                                        }).start();
                                    }


                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 14;
                                    handler.sendMessage(messageId);

                                } else if (a == '5') {
                                    if (user_job.equals("student")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound10.play(piano_soundId10, 1.0f, 1.0f, 0, 0, 1.0f);

                                            }
                                        }).start();

                                    }

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 15;
                                    handler.sendMessage(messageId);
                                }
                            }


                        }


                    }
                } catch (Exception e) {
                    Log.d("sdfsdf", "오른손 에러: " + e.getMessage());
                    e.printStackTrace();
                }


            }
        }).start();

    }

    public void raspSignalOFF() {
        isRaspOn = false;
        LoginActivity.soundOn = 0;
    }

    private void changeHandImage(int index) {
        hand_image.setVisibility(View.GONE);
        hand_left1_image.setVisibility(View.GONE);
        hand_left2_image.setVisibility(View.GONE);
        hand_left3_image.setVisibility(View.GONE);
        hand_left4_image.setVisibility(View.GONE);
        hand_left5_image.setVisibility(View.GONE);
        hand_right1_image.setVisibility(View.GONE);
        hand_right2_image.setVisibility(View.GONE);
        hand_right3_image.setVisibility(View.GONE);
        hand_right4_image.setVisibility(View.GONE);
        hand_right5_image.setVisibility(View.GONE);

        switch (index) {
            case 1:
                hand_left1_image.setVisibility(View.VISIBLE);
                break;
            case 2:
                hand_left2_image.setVisibility(View.VISIBLE);
                break;
            case 3:
                hand_left3_image.setVisibility(View.VISIBLE);
                break;
            case 4:
                hand_left4_image.setVisibility(View.VISIBLE);
                break;
            case 5:
                hand_left5_image.setVisibility(View.VISIBLE);
                break;
            case 6:
                hand_right1_image.setVisibility(View.VISIBLE);
                break;
            case 7:
                hand_right2_image.setVisibility(View.VISIBLE);
                break;
            case 8:
                hand_right3_image.setVisibility(View.VISIBLE);
                break;
            case 9:
                hand_right4_image.setVisibility(View.VISIBLE);
                break;
            case 10:
                hand_right5_image.setVisibility(View.VISIBLE);
                break;
        }

    }


    //악기 바꾸기 메서드.
    private void changeInstrument() {
        if (LobbyActivity.CurrentInstrument == LobbyActivity.PIANO_FLAG) {
            LobbyActivity.CurrentInstrument = LobbyActivity.DRUM_FLAG;
            hand_image.setVisibility(View.GONE);
            hand_left1_image.setVisibility(View.GONE);
            hand_left2_image.setVisibility(View.GONE);
            hand_left3_image.setVisibility(View.GONE);
            hand_left4_image.setVisibility(View.GONE);
            hand_left5_image.setVisibility(View.GONE);
            hand_right1_image.setVisibility(View.GONE);
            hand_right2_image.setVisibility(View.GONE);
            hand_right3_image.setVisibility(View.GONE);
            hand_right4_image.setVisibility(View.GONE);
            hand_right5_image.setVisibility(View.GONE);

        } else if (LobbyActivity.CurrentInstrument == LobbyActivity.DRUM_FLAG) {
            LobbyActivity.CurrentInstrument = LobbyActivity.PIANO_FLAG;
            hand_image.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("call_event"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    //서비스에서 보낸 메시지를 받는 메소드
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String message = intent.getStringExtra("message");
            Log.d("서비스", "11111111Receiver) message received: " + message);

            if (message.equals("play")) {
                if (isPlaying == false) {
//                    if (user_job.equals("student")) {
                    if (mediaPlayer.getCurrentPosition() == 0) {
                        mediaPlayer.seekTo(0);
                    } else {
                        mediaPlayer.seekTo(seek_position);
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    play_pause_btn_imageView.setImageResource(R.drawable.pause_btn_image);
                                }
                            });
                        }
                    }).start();

                    if (seek_position == 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                for (int i = 3; i > 0; i--) {
                                    final int finalI = i;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Umm_text.setText(finalI + "");
                                        }
                                    });
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Umm_text.setText("시작");
                                    }
                                });
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.start();
                            }
                        }).start();
                    } else {
                        mediaPlayer.start();
                    }

                    isPlaying = true;
                }
            }

            if (message.equals("stop")) {
                play_pause_btn_imageView.setImageResource(R.drawable.play_btn_image);
//                if (user_job.equals("student")) {
                mediaPlayer.seekTo(0);
                seek_position = 0;
                mediaPlayer.pause();
//                }
                isPlaying = false;
                Toast.makeText(CallActivity.this, "음악이 정지되었습니다.", Toast.LENGTH_SHORT).show();
            }

            if (message.equals("pause")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (user_job.equals("teacher"))
                                    play_pause_btn_imageView.setImageResource(R.drawable.play_btn_image);
                            }
                        });
                    }
                }).start();
                mediaPlayer.pause();
                seek_position = mediaPlayer.getCurrentPosition();
                isPlaying = false;
            }

            if (message.equals("close")) {
                try {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            mediaPlayer.stop();
                        }
                    }
                } catch (Exception e) {

                }
                if (user_job.equals("student"))
                    finish();
                else if (user_job.equals("teacher")) {
                    try {
                        String url = "http://54.180.26.72/get_student_name.php";
                        ContentValues values = new ContentValues();
                        values.put("master_id", LobbyActivity.user_id);

                        NetWorkAsync netWorkAsync = new NetWorkAsync(url, values);

                        String get_student_name = netWorkAsync.execute().get();
                        Log.d("SDFds", get_student_name + "@!");
                        Intent intent1 = new Intent(CallActivity.this, FeedbackWriteActivity.class);
                        intent1.putExtra("student_name", get_student_name);
                        intent1.putExtra("master_name", LobbyActivity.user_id);
                        startActivity(intent1);
                        finish();

                    } catch (Exception e) {

                    }


                }
            }

            if (message.equals("p1")) {
                updateUIThread("도");
                updateColorThread(Color.parseColor("#CC6B00"));
            }
            if (message.equals("p2")) {
                updateUIThread("레");
                updateColorThread(Color.parseColor("#CC13C0"));
            }
            if (message.equals("p3")) {
                updateUIThread("미");
                updateColorThread(Color.parseColor("#76CC00"));
            }
            if (message.equals("p4")) {
                updateUIThread("파");
                updateColorThread(Color.parseColor("#CC13C0"));
            }
            if (message.equals("p5")) {
                updateUIThread("솔");
                updateColorThread(Color.parseColor("#CC0801"));
            }
            if (message.equals("p6")) {
                updateUIThread("라");
                updateColorThread(Color.parseColor("#CC6B00"));
            }
            if (message.equals("p7")) {
                updateUIThread("시");
                updateColorThread(Color.parseColor("#CCAF00"));
            }
            if (message.equals("p8")) {
                updateUIThread("도");
                updateColorThread(Color.parseColor("#76CC00"));
            }
            if (message.equals("p9")) {
                updateUIThread("레");
                updateColorThread(Color.parseColor("#FF1DEC"));
            }
            if (message.equals("p10")) {
                updateUIThread("미");
                updateColorThread(Color.parseColor("#FF0069"));
            }
        }
    };

    //서비스로 메시지를 보낸다
    void sendMsg(String msg) {
        Intent intent = new Intent(getApplicationContext(), MainService.class);
        intent.putExtra("message", msg);
        startService(intent);
    }

    public void updateUIThread(final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Umm_text.setText("" + msg);
                    }
                });
            }
        }).start();
    }

    public void updateColorThread(final int dd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Umm_text.setTextColor(dd);
                    }
                });
            }
        }).start();
    }


}
