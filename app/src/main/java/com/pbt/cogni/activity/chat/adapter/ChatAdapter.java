package com.pbt.cogni.activity.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.pbt.cogni.R;
import com.pbt.cogni.activity.chat.Chat;
import com.pbt.cogni.util.AppUtils;
import com.pbt.cogni.util.MyPreferencesHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends ArrayAdapter<Chat> implements Filterable {

    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1;
    private Activity context;

    public ChatAdapter(Activity context, List<Chat> data) {
        super(context, R.layout.item_my_message, data);
        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Chat item = getItem(position);
        assert item != null;
        if (item.getSender() == Integer.valueOf(MyPreferencesHelper.Companion.getUser(context.getApplication()).getId())) return MY_MESSAGE;
        else return OTHER_MESSAGE;
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        int viewType = getItemViewType(position);
        final Chat chat = getItem(position);
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_my_message, parent, false);
            if (chat.getType().equals("text")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_my_message, parent, false);
                TextView textView = convertView.findViewById(R.id.text);
                textView.setText(getItem(position).getText());
                TextView timestamp = convertView.findViewById(R.id.timestamp);
                TextView txtMsgStatus = convertView.findViewById(R.id.txtMsgStatus);
                int icon = 0;
                if(chat.getRead() == 0)
                    icon = R.drawable.ic_not_deliverd;
                else   if(chat.getRead() == 1)
                    icon = R.drawable.ic_double_check;
                else
                    icon = R.drawable.ic_deliverd;

                txtMsgStatus.setBackground(getContext().getDrawable(icon));

                timestamp.setText(String.valueOf(AppUtils.Companion.getDisplayableTime(chat.getTimestamp())));
            }
//            else if (chat.getType().equals("image")) {
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_image, parent, false);
//                ImageView mine_image = convertView.findViewById(R.id.mine_image);
//                TextView timestamp = convertView.findViewById(R.id.timestamp);
//                timestamp.setText(String.valueOf(getDisplayableTime(chat.getTimestamp())));
//                Glide.with(context)
//                        .load(chat.getUrl())
//                        .apply(RequestOptions
//                                .placeholderOf(R.drawable.ic_user_placeholder)
//                                .dontAnimate().error(R.drawable.ic_user_placeholder))
//                        .into(mine_image);
//            }
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);
            if (chat.getType().equals("text")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);
                TextView textView = convertView.findViewById(R.id.text);
                textView.setText(getItem(position).getText());
                TextView timestamp = convertView.findViewById(R.id.timestamp);
                timestamp.setText(String.valueOf(AppUtils.Companion.getDisplayableTime(chat.getTimestamp())));
            }
//            else if (chat.getType().equals("image")) {
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_image, parent, false);
//                ImageView other_image = convertView.findViewById(R.id.other_image);
//                TextView timestamp = convertView.findViewById(R.id.timestamp);
//                timestamp.setText(String.valueOf(getDisplayableTime(chat.getTimestamp())));
//                Glide.with(context)
//                        .load(chat.getUrl())
//                        .apply(RequestOptions
//                                .placeholderOf(R.drawable.ic_user_placeholder)
//                                .dontAnimate()
//                                .error(R.drawable.ic_user_placeholder))
//                        .into(other_image);
//            }
        }
        return convertView;
    }


}
