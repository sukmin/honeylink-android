package kr.netty.honeylink.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import kr.netty.honeylink.model.Link;
import kr.netty.honeylink.task.ImageDownloadTask;
import kr.netty.honeylink.view.LinkListItemView;

public class LinkListAdapter extends BaseAdapter {

    private Context context;
    private List<Link> links;

    public LinkListAdapter(Context context){
        this.context = context;
    }

    public void setLinks(List<Link> links){
        this.links = links;
    }

    @Override
    public int getCount() {
        return links.size();
    }

    @Override
    public Link getItem(int position) {
        return links.get(position);
    }

    @Override
    public long getItemId(int position) {
        return links.get(position).getSequence();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinkListItemView itemView;
        if(convertView == null){
            itemView = new LinkListItemView(context);
        }else{
            itemView = (LinkListItemView) convertView;
        }

        Link targetLink = links.get(position);
        itemView.setData(targetLink);

        String thumbUrl = targetLink.getThumbnailUrl();
        if( thumbUrl != null && (!"".equals(thumbUrl)) ){
            new ImageDownloadTask(itemView).execute(thumbUrl);
        }

        return itemView;
    }
}
