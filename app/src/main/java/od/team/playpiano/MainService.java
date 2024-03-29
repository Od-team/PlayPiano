package od.team.playpiano;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

import android.app.Service;

public class MainService extends Service {


    ConnectThread clientSocket;
    Socket socket;

    String TAG = "서비스";

    Context context;
    String get_message;
    String receive_message;


    boolean isSocketAlive;

    Thread sender;
    Thread receiver;

    //로그인 시 서비스 실행되는지 확인
    //액티비티와 서비스가 메시지를 주고받는지 확인


    public MainService(Context context) {
        super();
        this.context = context;
        Log.d("서비스", "MainService.class 생성자");

    }

    public MainService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("서비스", "MainService - onCreate()");
        //클라이언트 소켓 연결
        clientSocket = new ConnectThread();
        clientSocket.start();

        Log.d("서비스", "클라이언트 소켓 연결");

    }


    //startService(intent) 때마다 onStartCommand 가 호출된다
    //액티비티의 onResume()같은 역할
    //서비스 실행중에 startService 가 호출되면, 서비스의 onCreate()가 호출되는게 아니라 onStartCommand()부터 시작한다
    //intent를 통해서 액티비티로부터 데이터를 전달받는다
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("서비스", "MainService - onStartCommand()");
        isSocketAlive = true;

        try {

            //액티비티에서 전달받은 메시지를, 서버에 보낸다
            if (intent.hasExtra("message")) {
                get_message = intent.getStringExtra("message");
                Log.d("서비스", "Received a message from Activity: " + get_message);

                clientSocket.sendServer(get_message);

            }


        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d(TAG, ex);
        }
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //앱이 꺼질 때 소켓을 종료시킨다

        try {
//            if (socket != null) {
//                sendMsg("closeRoom");
            if(this.socket != null){
                Log.d(TAG, "client thread, quit()");
                isSocketAlive = false;
            }

//            }
        } catch (Exception e) {
            Log.d("서비스", "socket closing error: " + e);
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // 송, 수신 ConnectThread 시작
    class ConnectThread extends Thread {

        String str;

        public void sendServer(String msg) {
            this.str = msg;
            // 제일 처음엔 사용자의 아이디가 들어온다.
            Log.d("서비스","메세지 : " + this.str);
        }

        @Override
        public void run() {

            try {
                Log.d("서비스", "소켓 연결 요청 전");
                // 1. 소켓 생성 후 서버 IP에 연결을 요청
                socket = new Socket("54.180.26.72", 10000);
//                socket = new Socket("192.168.0.2", 10000);

                Log.d("서비스", "소켓 연결 요청 후");

                // 2. inout / output에 해당하는 thread를 각각 만들고 start()합니다
                sender = new Thread(new ClientSender(socket));
                receiver = new Thread(new ClientReceiver(socket));

                sender.start();
                receiver.start();

                Log.d("서비스", "'스레드 스타트'");

            } catch (IOException e) {
                e.printStackTrace();
            } // try - catch 끝

        } // run() 끝

    } // ConnectThread끝


    // 송신 클래스
    class ClientSender extends Thread {

        // 클라 소켓
        Socket socket;

        // 내보낼 메세지 객체
        DataOutputStream out;

        // [ 사용자에 할당된 소켓 ] 과 [ 사용자 이름 ] 이 매개변수로 들어옴
        ClientSender(Socket socket) {
            this.socket = socket;

            try {
                out = new DataOutputStream(this.socket.getOutputStream());

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ㄴㄴㄴㄴ", "클라센더 생성자 에러");
            } // try - catch 끝
        } // ClientSender 끝

        @Override
        public void run() {
            try {
                Log.d("ㄴㄴㄴㄴ", "드옴?");
                if (out != null) {
                    try{
                        // 방의 이름 / 사용자 이름을 가지고 서버로가서 방을 생성해달라고 한다.
                        out.writeUTF("user_id@@" + LobbyActivity.user_id);
                    }catch (Exception d){
                        Log.d("ㄴㄴㄴㄴ", "제일 첫번째 메세지 에러 : " + d.getMessage());
                    }
                }

                // 메시지를 서버로 보냅니다.(무한루프 돌면서 OutputStream에서 가져올게 있으면, wrtieUTF()를 실행합니다)
                while (isSocketAlive && out != null) {

                    if (clientSocket.str != null) {
                        Log.d("ㄴㄴㄴㄴ", "6666");
                        // 방 이름, 방 들어오는 사람, 메세지 내용
                        out.writeUTF(clientSocket.str);
                        clientSocket.str = null;
                        Log.d("ㄴㄴㄴㄴ", "7777");
                    }
                }

            } catch (IOException e) {
                Log.d("ㄴㄴㄴㄴ", "송신 연결 끊김");
            }
        }
    }

    class ClientReceiver extends Thread {
        Socket socket;
        DataInputStream in;

        ClientReceiver(Socket socket) {
            try {
                this.socket = socket;
                in = new DataInputStream(this.socket.getInputStream());
                Log.d("ㄴㄴㄴㄴ", "1 : ");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ㄴㄴㄴㄴ", "2 : ");

            }
        }

        @Override
        public void run() {
            while (isSocketAlive && in != null) {
                try {

                    // outputStream으로 온 데이터를 read합니다
                    receive_message = in.readUTF();

                    String[] str = new String[100];
                    str = receive_message.split("@@");

                    Log.d("ㄴㄴㄴㄴ", "3 : " + receive_message.toString());

                    if (str[0].equals("room_create")) {
                        sendMsgToLobby("room_create@@"+str[1]+"@@"+str[2]);
                    }

                    if (str[0].equals("gameRoom")) {
                        Log.d("ㄴㄴㄴㄴ", "이거받음 : " + str[1]);
                        sendMsgToGameRoom("user@@"+str[1]);
                    }
                    if (str[0].equals("ready")) {
                        sendMsgToGameRoom(str[1]);
                    }
                    if (str[0].equals("start")) {
                        sendMsgToGameRoom(str[1]);
                    }
                    if(str[0].equals("play")){
                        sendMsgToCallActivity("play");
                    }
                    if(str[0].equals("stop")){
                        sendMsgToCallActivity("stop");
                    }
                    if(str[0].equals("pause")){
                        sendMsgToCallActivity("pause");
                    }
                    if(str[0].equals("Umm")){
                        sendMsgToCallActivity(""+str[1]);
                    }
                    if(str[0].equals("close")){
                        sendMsgToCallActivity("close");
                    }
                    if(str[0].equals("master_exit")){
                        sendMsgToGameRoom("master_exit");
                    }
                    if (str[0].equals("student_out")) {
                        sendMsgToGameRoom("student_out");
                    }

                } catch (IOException e) {
                    Log.d("ㄴㄴㄴㄴ", "수신 연결 끊김");
                }

            }
        }

        /*액티비티에 메시지를 보낸다. 액티비티마다 브로드캐스트 달아줘야함*/
        private void sendMsgToLobby(String message) {
            Log.d(TAG, "Broadcasting message to lobby activity");
            Intent intent = new Intent("lobby_event");
            intent.putExtra("message", message);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }

        /*액티비티에 메시지를 보낸다. 액티비티마다 브로드캐스트 달아줘야함*/
        private void sendMsgToGameRoom(String message) {
            Log.d(TAG, "Broadcasting message to lobby activity");
            Intent intent = new Intent("gameRoom_event");
            intent.putExtra("message", message);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }

        /*액티비티에 메시지를 보낸다. 액티비티마다 브로드캐스트 달아줘야함*/
        private void sendMsgToCallActivity(String message) {
            Log.d(TAG, "Broadcasting message to call activity");
            Intent intent = new Intent("call_event");
            intent.putExtra("message", message);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }



}
