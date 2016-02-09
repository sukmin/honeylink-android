package kr.netty.honeylink.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.netty.honeylink.R;
import kr.netty.honeylink.model.Link;

public class LinkListItemView extends LinearLayout {

    private ImageView thumbImageView;
    private TextView urlTextView;
    private TextView titleTextView;
    private TextView countTextView;

    public LinkListItemView(Context context) {
        super(context);
        initContentView();
    }

    public LinkListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContentView();
    }

    private void initContentView() {

        View.inflate(getContext(), R.layout.view_link_list_item, this);

        thumbImageView = (ImageView) findViewById(R.id.view_link_list_item_ImageView_Thumb);
        urlTextView = (TextView) findViewById(R.id.view_link_list_item_TextView_Url);
        titleTextView = (TextView) findViewById(R.id.view_link_list_item_TextView_Title);
        countTextView = (TextView) findViewById(R.id.view_link_list_item_TextView_Count);

    }

    public void setData(Link link){

        setUrlText(link.getUrl());
        setTitleText(link.getTitle());
        setCountText(String.valueOf(link.getReadCount()));
    }

    public void setUrlText(String url){
        urlTextView.setText(url);
    }

    public void setTitleText(String title){
        titleTextView.setText(title);
    }

    public void setCountText(String count){
        countTextView.setText(count);
    }

    public void setImage(Bitmap bitmap){
        thumbImageView.setImageBitmap(bitmap);
    }

    public void setImage(int resourceid){
        thumbImageView.setImageResource(resourceid);
    }
}
