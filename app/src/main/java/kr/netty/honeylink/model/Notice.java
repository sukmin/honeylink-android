package kr.netty.honeylink.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import kr.netty.honeylink.HoneylinkApplication;
import kr.netty.honeylink.manager.HoneylinkPreferenceManager;

public class Notice implements Parcelable {

    public static final String TYPE_ONCE = "once";
    public static final String TYPE_FORCE = "force";
    public static final String TYPE_SHUTDOWN = "shutdown";
    public static final String TYPE_NORMAL = "normal";
    public static final String TYPE_UNDER = "under";
    public static final String TYPE_NONE = "none";


    private long sequence;
    /**
     * once : 한번만 보이는 공지<br>
     * force : 한번보아도 강제로 무조건 보는 공지<br>
     * shutdown : 서버에 작업 및 이상이 있는 경우 사용를 아예 못하도록 하기 위한 공지<br>
     * normal : 사용자가 확인을 누르면 다음부터 보이지 않는 공지<br>
     * under : lastVersion보다 낮은 버전의 앱에서만 보이는 공지
     * none : 무공지
     */
    private String type; // once,force,shutdown,normal,under
    private String content;
    private String url;
    private String lastVersionName;

    public Notice() {
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLastVersionName() {
        return lastVersionName;
    }

    public void setLastVersionName(String lastVersionName) {
        this.lastVersionName = lastVersionName;
    }

    public boolean isShutdown(){
        return Notice.TYPE_SHUTDOWN.equals(this.getType());
    }

    public AlertDialog createDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("공지사항").setMessage(content);

        if ( url != null ) {
            builder.setNeutralButton("바로가기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            });
        }

        DialogInterface.OnClickListener okClickListener = null;

        switch(type){
            case TYPE_NONE:
                return null;
            case TYPE_ONCE:
                if (HoneylinkPreferenceManager.isOverSavedNoticeSequence(sequence)) {
                    return null;
                }
                HoneylinkPreferenceManager.setLastNoticeSequence(sequence);
                break;
            case TYPE_FORCE:
                break;
            case TYPE_SHUTDOWN:
                okClickListener = new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if( context instanceof  Activity ){
                            ((Activity)context).finish();
                        }
                    }
                };
                break;
            case TYPE_UNDER:
                if( HoneylinkApplication.appVersionName.equals(lastVersionName) ){
                    return null;
                }
                break;
            default:
                if (HoneylinkPreferenceManager.isOverSavedNoticeSequence(sequence)) {
                    return null;
                }

                builder.setNegativeButton("다시 보지 않기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HoneylinkPreferenceManager.setLastNoticeSequence(sequence);
                    }
                });
                break;

        }


        AlertDialog dialog = builder.setPositiveButton(android.R.string.ok, okClickListener).create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(sequence);
        dest.writeString(type);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(lastVersionName);
    }

    public static final Parcelable.Creator<Notice> CREATOR = new Creator<Notice>() {
        @Override
        public Notice createFromParcel(Parcel source) {
            Notice newNotice = new Notice();

            newNotice.sequence = source.readLong();
            newNotice.type = source.readString();
            newNotice.content = source.readString();
            newNotice.url = source.readString();
            newNotice.lastVersionName = source.readString();

            return newNotice;
        }

        @Override
        public Notice[] newArray(int size) {
            return new Notice[size];
        }
    };
}
