package com.pbt.cogni.activity.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.pbt.cogni.R;
import com.pbt.cogni.activity.chat.ChatActivity;
import com.pbt.cogni.model.Chat;
import com.pbt.cogni.util.AppUtils;
import com.pbt.cogni.util.MyPreferencesHelper;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


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
        if (item.getSender() == Integer.valueOf(MyPreferencesHelper.Companion.getUser(context.getApplication()).getId()))
            return MY_MESSAGE;
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


            TextView txtMsgStatus = convertView.findViewById(R.id.txtMsgStatus);

            TextView textView = convertView.findViewById(R.id.text);
            TextView timestamp = convertView.findViewById(R.id.timestamp);
            TextView txtTimeStamp = convertView.findViewById(R.id.txtTimeStamp);
            TextView txtFileType = convertView.findViewById(R.id.txtFileType);
            TextView txtFileTypeImage = convertView.findViewById(R.id.txtFileTypeImage);
            TextView txtImageTimeStamp = convertView.findViewById(R.id.txtImageTimeStamp);
            TextView txtFileName = convertView.findViewById(R.id.txtFileName);
            ImageView image = convertView.findViewById(R.id.imgMessage);
            TextView txtFileStatus = convertView.findViewById(R.id.txtFileStatus);
            TextView txtTextStauts = convertView.findViewById(R.id.txtTextStauts);

            LinearLayout llDocs = convertView.findViewById(R.id.llDocs);
            LinearLayout llImageMessage = convertView.findViewById(R.id.llImageMessage);
            LinearLayout llChatTextMessage = convertView.findViewById(R.id.llChatTextMessage);

            llImageMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context,"Click is on "+  chat.getText(),Toast.LENGTH_SHORT).show();
                    chat.getText();
                }
            });


            if (AppUtils.Companion.checkIsimage(chat.getType()).equals("text")) {
                llChatTextMessage.setVisibility(View.VISIBLE);
                textView.setText(getItem(position).getText());
                timestamp.setText(String.valueOf(AppUtils.Companion.getDisplayableTime(chat.getTimestamp())));
            } else if (AppUtils.Companion.checkIsimage(chat.getType()).equals("image")) {
                llImageMessage.setVisibility(View.VISIBLE);
                txtFileTypeImage.setText(chat.getType());
                txtImageTimeStamp.setText(String.valueOf(AppUtils.Companion.getDisplayableTime(chat.getTimestamp())));
                Glide.with(context).load(chat.getText()).placeholder(R.drawable.loading).into(image);
            } else {
                llDocs.setVisibility(View.VISIBLE);
                txtFileName.setText(chat.getFileName().length() > 10 ? chat.getFileName().substring(0,18)+"..." : chat.getFileName() );
                txtFileType.setText(chat.getType());
                txtTimeStamp.setText(String.valueOf(AppUtils.Companion.getDisplayableTime(chat.getTimestamp())));
            }


            //image content id's


            int icon = 0;
            if (chat.getRead() == 0)
                icon = R.drawable.ic_not_deliverd;
            else if (chat.getRead() == 1)
                icon = R.drawable.ic_double_check;
            else
                icon = R.drawable.ic_deliverd;

            txtMsgStatus.setBackground(getContext().getDrawable(icon));
            txtFileStatus.setBackground(getContext().getDrawable(icon));
            txtTextStauts.setBackground(getContext().getDrawable(icon));



        } else {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);

            TextView textView = convertView.findViewById(R.id.text);
            TextView timestamp = convertView.findViewById(R.id.timestamp);
            TextView txtTimeStamp = convertView.findViewById(R.id.txtTimeStamp);
            TextView txtFileType = convertView.findViewById(R.id.txtFileType);
            TextView txtFileTypeImage = convertView.findViewById(R.id.txtFileTypeImage);
            TextView txtImageTimeStamp = convertView.findViewById(R.id.txtImageTimeStamp);
            TextView txtFileName = convertView.findViewById(R.id.txtFileName);
            ImageView image = convertView.findViewById(R.id.imgMessage);
            ImageView imageview = convertView.findViewById(R.id.imgDownload);
//             progressBar = convertView.findViewById(R.id.download_progressbar);
          final    ImageView imgDownload=convertView.findViewById(R.id.imgDownload);
            convertView.callOnClick();

            final ProgressBar progressBar = ((ProgressBar) convertView.findViewById(R.id.download_progressbar));



            LinearLayout llDocs = convertView.findViewById(R.id.llDocs);
            LinearLayout llImageMessage = convertView.findViewById(R.id.llImageMessage);
            LinearLayout llChatTextMessage = convertView.findViewById(R.id.llChatTextMessage);

            llDocs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


//
                    String fileurl=chat.getText();
                    imgDownload.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
//                    progressBar.setTag(position);


              new DownloadFileFromURL(progressBar,imgDownload,chat).execute(fileurl);
//
//                 new ChatActivity().DownloadFile(chat.getText() ,context,chat.getFileName(),imageview);

                }
            });
            llImageMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new ChatActivity().DownloadFile(chat.getText() ,context,chat.getFileName(),imageview);

                }
            });

            if (AppUtils.Companion.checkIsimage(chat.getType()).equals("text")) {
                llChatTextMessage.setVisibility(View.VISIBLE);
                textView.setText(getItem(position).getText());
                timestamp.setText(String.valueOf(AppUtils.Companion.getDisplayableTime(chat.getTimestamp())));
            } else if (AppUtils.Companion.checkIsimage(chat.getType()).equals("image")) {
                llImageMessage.setVisibility(View.VISIBLE);
                txtFileTypeImage.setText(chat.getType());
                txtImageTimeStamp.setText(String.valueOf(AppUtils.Companion.getDisplayableTime(chat.getTimestamp())));
                Glide.with(context).load(chat.getText()).placeholder(R.drawable.loading).into(image);
            } else {
                llDocs.setVisibility(View.VISIBLE);
                txtFileName.setText(chat.getFileName().length() > 10 ? chat.getFileName().substring(0,18)+"..." : chat.getFileName() );
                txtFileType.setText(chat.getType());
                txtTimeStamp.setText(String.valueOf(AppUtils.Companion.getDisplayableTime(chat.getTimestamp())));
            }
        }
        return convertView;
    }




    private class DownloadFileFromURL extends AsyncTask<String, String, String> {
ProgressBar progressBar;
ImageView imaDownload;
Chat chat;

        public DownloadFileFromURL(ProgressBar progressBar, ImageView imgDownload, Chat chat) {
            this.progressBar=progressBar;
            this.imaDownload=imgDownload;
            this.chat=chat;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AppUtils.Companion.logDebug("##DownloadFile","Before Downloading Downloading-----File");
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();


                int lenghtOfFile = connection.getContentLength();


                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                File storage = new File(Environment.getExternalStorageDirectory() + File.separator + "/Download/");
                String FileName = "/" + chat.getFileName();
                FileOutputStream output = new FileOutputStream(storage+FileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
           AppUtils.Companion.logDebug("##DownloadFile","In Downloading-----File"+progress[0].toString());

           progressBar.setProgress(Integer.parseInt(progress[0]));

        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            progressBar.setVisibility(View.GONE);
            imaDownload.setVisibility(View.VISIBLE);

            AppUtils.Companion.logDebug("##DownloadFile","Downloaded-----File");
           Toast.makeText(context,"Download Completed ",Toast.LENGTH_SHORT).show();


        }

    }


}
