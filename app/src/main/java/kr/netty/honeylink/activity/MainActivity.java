package kr.netty.honeylink.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.netty.honeylink.R;
import kr.netty.honeylink.adapter.LinkListAdapter;
import kr.netty.honeylink.config.ApiUrl;
import kr.netty.honeylink.model.Link;
import kr.netty.honeylink.model.Notice;
import kr.netty.honeylink.util.DLog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class MainActivity extends Activity {

    private ListView linkListView;
    private LinkListAdapter linkListAdapter;

    private OkHttpClient okHttpClient;
    private final Gson gson = new Gson();

    private static final int UI_HANDLER_WHAT_RECEIVE_LINKS_SUCCESS = 0;
    private static final int UI_HANDLER_WHAT_RECEIVE_LINKS_OKHTTP_FAIL = 1;
    private static final int UI_HANDLER_WHAT_RECEIVE_LINKS_NOT_SUCCESS = 2;

    private boolean waitBackKey = false;
    private static final int UI_HANDLER_WHAT_RECEIVE_BACK_PRESS = 99;

    private final Handler uiHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {

            switch(msg.what){

                case UI_HANDLER_WHAT_RECEIVE_LINKS_SUCCESS:


                    if( msg.obj instanceof List<?> ){

                        linkListAdapter.setLinks((List<Link>) msg.obj);

                        linkListView.setAdapter(linkListAdapter);

                    }

                    break;
                case UI_HANDLER_WHAT_RECEIVE_BACK_PRESS:
                    waitBackKey = false;
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 공지 보이기
        Intent recivedIntent = getIntent();
        if(recivedIntent != null){
            Notice aNotice = recivedIntent.getParcelableExtra("notice");
            if(aNotice != null){
                AlertDialog dialog = aNotice.createDialog(this);
                dialog.show();
            }
        }

        initView();

        // OKHTTP 자동 retry 적용 http://stackoverflow.com/questions/24562716/how-to-retry-http-requests-with-okhttp-retrofit
        okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                // try the request
                Response response = chain.proceed(request);

                int tryCount = 0;
                while (!response.isSuccessful() && tryCount < 3) {

                    DLog.d("intercept", "Links Request is not successful - " + tryCount);

                    tryCount++;

                    // retry the request
                    response = chain.proceed(request);
                }

                // otherwise just pass the original response on
                return response;
            }
        }).build();

        linkListAdapter = new LinkListAdapter(this);

        linkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Request request = new Request.Builder()
                        .url(ApiUrl.HONEYLINK_API_LINKS_COUNT)
                        .post(new FormBody.Builder()
                                .add("sequence", String.valueOf(id))
                                .build())
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });

                Link targetLink = (Link)parent.getItemAtPosition(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(targetLink.getUrl());
                intent.setData(uri);
                MainActivity.this.startActivity(intent);
            }
        });


    }

    private void initView() {

        linkListView = (ListView) findViewById(R.id.activity_main_ListView);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Request request = new Request.Builder()
                .url(ApiUrl.HONEYLINK_API_LINKS)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {
                    //TODO 성공이 아니라면 여기서 처리
                }

                List<Link> aLinks = gson.fromJson(response.body().string(), new TypeToken<List<Link>>() {
                }.getType());

                Message aMessage = uiHandler.obtainMessage(UI_HANDLER_WHAT_RECEIVE_LINKS_SUCCESS, aLinks);
                aMessage.sendToTarget();

            }
        });

    }


    @Override
    public void onBackPressed() {
        if (waitBackKey == false) {
            uiHandler.sendEmptyMessageDelayed(UI_HANDLER_WHAT_RECEIVE_BACK_PRESS, 3000);
            waitBackKey = true;
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }
}
