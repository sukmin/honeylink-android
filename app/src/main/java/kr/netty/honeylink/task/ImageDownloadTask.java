package kr.netty.honeylink.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.netty.honeylink.R;
import kr.netty.honeylink.model.Link;
import kr.netty.honeylink.util.DLog;
import kr.netty.honeylink.view.LinkListItemView;


// http://javatechig.com/android/loading-image-asynchronously-in-android-listview 를 참고
public class ImageDownloadTask extends AsyncTask<String,Void,Bitmap> {

    private final WeakReference<LinkListItemView> linkItemView;

    public ImageDownloadTask(LinkListItemView linkItemView) {
        this.linkItemView = new WeakReference<LinkListItemView>(linkItemView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(params[0]);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            DLog.w("ImageDownloadTask", "Error downloading image from " + params[0]);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (linkItemView != null) {
            LinkListItemView itemView = linkItemView.get();
            if (itemView != null) {
                if (bitmap != null) {
                    itemView.setImage(bitmap);
                } else {
                    itemView.setImage(R.drawable.honeylink);
                }
            }
        }
    }
}
