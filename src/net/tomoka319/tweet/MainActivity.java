package net.tomoka319.tweet;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

public class MainActivity extends ListActivity {

    private TweetAdapter mAdapter;
    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!net.tomoka319.tweet.util.TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            mAdapter = new TweetAdapter(this);
            setListAdapter(mAdapter);

            mTwitter = net.tomoka319.tweet.util.TwitterUtils.getTwitterInstance(this);
            reloadTimeLine();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reload:
                reloadTimeLine();
                return true;
            case R.id.menu_web:
            	Intent intent = new Intent (this, webActivity.class);
            	startActivity(intent);
            	return true;
            case R.id.menu_tweet:
                Intent intent1 = new Intent(this, TweetActivity.class);
                startActivity(intent1);
                return true;
            case R.id.menu_reload1:
            	reloadTimeLine();
            	return true;
            case R.id.menu_tweet1:
            	Intent intent2 = new Intent(this, TweetActivity.class);
            	startActivity(intent2);
            	return true;
            case R.id.menu_mail:
            	Uri uri = Uri.parse ("mailto:admin@tomoka319.net");  
            	Intent intent3 = new Intent(Intent.ACTION_SENDTO, uri);   
            	intent3.putExtra(Intent.EXTRA_SUBJECT, "見ろ!!人がゴミのようだ!!");  
            	intent3.putExtra(Intent.EXTRA_TEXT, "バルス！");  
            	startActivity(intent3);
            	return true;
            case R.id.menu_gps:
            	Intent intent4 = new Intent(this, GPSActivity.class);
            	startActivity(intent4);
            	return true;
            case R.id.menu_wifi:
            	Intent intent5 = new Intent(this, WifiScanActivity.class);
            	startActivity(intent5);
            	return true;
            case R.id.menu_wifi_setting:
            	Intent intent6 = new Intent(this, WifiActivity.class);
            	startActivity(intent6);
            	return true;
            case R.id.menu_speed:
            	PackageManager pm = getPackageManager();
            	Intent intent7 = pm.getLaunchIntentForPackage("org.zwanoo.android.speedtest");
            	startActivity(intent7);
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class TweetAdapter extends ArrayAdapter<Status> {

        private LayoutInflater mInflater;

        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_tweet, null);
            }
            Status item = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(item.getUser().getName());
            TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
            screenName.setText("@" + item.getUser().getScreenName());
            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText(item.getText());
            SmartImageView icon = (SmartImageView) convertView.findViewById(R.id.icon);
            icon.setImageUrl(item.getUser().getProfileImageURL());
            return convertView;
        }
    }

    private void reloadTimeLine() {
        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    mAdapter.clear();
                    for (twitter4j.Status status : result) {
                        mAdapter.add(status);
                    }
                    getListView().setSelection(0);
                } else {
                    showToast("タイムラインの取得に失敗しました。。。");
                }
            }
        };
        task.execute();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}