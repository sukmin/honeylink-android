package kr.netty.honeylink.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import kr.netty.honeylink.R;
import kr.netty.honeylink.config.ApiUrl;

import kr.netty.honeylink.manager.NetworkManager;
import kr.netty.honeylink.model.Notice;
import kr.netty.honeylink.util.DLog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IntroActivity extends Activity {

    private OkHttpClient okHttpClient;
    private final Gson gson = new Gson();

    private static final int UI_HANDLER_WHAT_RECEIVE_NOTICE_SUCCESS = 0;
    private static final int UI_HANDLER_WHAT_RECEIVE_NOTICE_OKHTTP_FAIL = 1;
    private static final int UI_HANDLER_WHAT_RECEIVE_NOTICE_NOT_SUCCESS = 2;
    private static final int UI_HANDLER_WHAT_RECEIVE_FINISH = 99;
    private final Handler uiHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UI_HANDLER_WHAT_RECEIVE_NOTICE_SUCCESS:

                    if( msg.obj instanceof  Notice){

                        Notice aNotice = (Notice) msg.obj;

                        Intent aIntent = new Intent(IntroActivity.this,MainActivity.class);

                        aIntent.putExtra("notice",aNotice);

                        IntroActivity.this.startActivity(aIntent);

                        finish();

                    }

                    break;

                case UI_HANDLER_WHAT_RECEIVE_NOTICE_OKHTTP_FAIL: // 서버가 죽어 아예 http코드를 보낼 수 없을 때
                    Toast.makeText(IntroActivity.this,"서버에 문제가 있습니다. 잠시후에 다시 시도해주세요.",Toast.LENGTH_LONG).show();
                    sendEmptyMessageDelayed(UI_HANDLER_WHAT_RECEIVE_FINISH,3000);
                    break;
                case UI_HANDLER_WHAT_RECEIVE_NOTICE_NOT_SUCCESS: //http코드가 성공이 아닐 때
                    Toast.makeText(IntroActivity.this,"서버에 문제가 있습니다. 잠시후에 다시 시도해주세요.",Toast.LENGTH_LONG).show();
                    sendEmptyMessageDelayed(UI_HANDLER_WHAT_RECEIVE_FINISH, 3000);
                    break;
                case UI_HANDLER_WHAT_RECEIVE_FINISH:
                    finish();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // OKHTTP 자동 retry 적용 http://stackoverflow.com/questions/24562716/how-to-retry-http-requests-with-okhttp-retrofit
        okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                // try the request
                Response response = chain.proceed(request);

                int tryCount = 0;
                while (!response.isSuccessful() && tryCount < 3) {

                    DLog.d("intercept", "Notice Request is not successful - " + tryCount);

                    tryCount++;

                    // retry the request
                    response = chain.proceed(request);
                }

                // otherwise just pass the original response on
                return response;
            }
        }).build();


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NetworkManager.isNotAvailable()){

            Toast.makeText(this,"네트워크가 연결되어 있지 않아 사용할 수 없습니다.",Toast.LENGTH_LONG).show();
            uiHandler.sendEmptyMessageDelayed(UI_HANDLER_WHAT_RECEIVE_FINISH,3000);
            return; // 더이상 플로우를 진행하지 않음
        }


        Request request = new Request.Builder()
                .url(ApiUrl.HONEYLINK_API_NOTICE)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // 서버가 응답이 없는 경우
                Message aMessage = uiHandler.obtainMessage(UI_HANDLER_WHAT_RECEIVE_NOTICE_OKHTTP_FAIL);
                aMessage.sendToTarget();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {
                    // HTTP상태코드가 성공이 아닐 경우
                    Message aMessage = uiHandler.obtainMessage(UI_HANDLER_WHAT_RECEIVE_NOTICE_NOT_SUCCESS);
                    aMessage.sendToTarget();
                    return;
                }

                Notice aNotice = gson.fromJson(response.body().string(), Notice.class);

                Message aMessage = uiHandler.obtainMessage(UI_HANDLER_WHAT_RECEIVE_NOTICE_SUCCESS, aNotice);
                uiHandler.sendMessageDelayed(aMessage,500);

            }

        });

    }

}
