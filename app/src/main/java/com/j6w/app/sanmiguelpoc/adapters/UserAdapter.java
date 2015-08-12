package com.j6w.app.sanmiguelpoc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.j6w.app.sanmiguelpoc.R;
import com.j6w.app.sanmiguelpoc.objects.User;
import com.j6w.app.sanmiguelpoc.utils.ImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ceosilvajr on 8/12/15.
 */
public class UserAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private List<User> mHistories;

    public UserAdapter(Context context, List<User> objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.mHistories = objects;
    }

    static class ViewHolder {

        @Bind(R.id.iv_user_image)
        ImageView mIVUserImage;

        @Bind(R.id.tv_user_name)
        TextView mTVUserName;

        @Bind(R.id.tv_date_created)
        TextView mTVDateCreated;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.container_user,
                    parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        User user = mHistories.get(position);
        viewHolder.mIVUserImage.setRotation(ImageUtil.neededRotation(new File(user.imagePath)));
        ImageLoader.getInstance().displayImage("file:///" + user.imagePath,
                viewHolder.mIVUserImage);
        viewHolder.mTVDateCreated.setText(user.getHumanReadableTime());
        viewHolder.mTVUserName.setText(user.name);

        return view;
    }

    @Override
    public int getCount() {
        return mHistories.size();
    }

    @Override
    public User getItem(int position) {
        return mHistories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
