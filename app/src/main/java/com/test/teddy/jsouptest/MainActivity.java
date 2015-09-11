package com.test.teddy.jsouptest;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.test.teddy.obj.RateItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this,"onCreate0",Toast.LENGTH_SHORT).show();
        mListView = (ListView)findViewById(R.id.listView);
        new Thread(runnable).start();

    }
    private static final String url ="http://rate.bot.com.tw/Pages/Static/UIP003.zh-TW.htm";
    private String UpdateTime;
    private List<RateItem> RateList;
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
          try {
                Document doc = Jsoup.connect(url).get();
                RateList = new ArrayList<RateItem>();
                int i = 0;
                for(Element title : doc.select("td.titleLeft")){
                    RateItem mRateItem = new RateItem();
                    mRateItem.setCurrency(title.text());
                    if (i < doc.select("td.decimal").size()){

                        mRateItem.setCashBuyRate(doc.select("td.decimal").eq(i).text());
                        mRateItem.setCashSoldRate(doc.select("td.decimal").eq(i+1).text());
                        mRateItem.setSpotBuyRate(doc.select("td.decimal").eq(i+2).text());
                        mRateItem.setSpotSoldRate(doc.select("td.decimal").eq(i+3).text());
                        i+=4;
                    }

                    RateList.add(mRateItem);
                }
               String Temp = doc.select("td[style=width:326px;text-align:left;vertical-align:top;color:#0000FF;font-size:11pt;font-weight:bold;]").text();
               UpdateTime = Temp.substring(12);

                Log.v("testTime","in"+doc.select("td[style=width:326px;text-align:left;vertical-align:top;color:#0000FF;font-size:11pt;font-weight:bold;]").text());

            } catch (IOException e) {
                e.printStackTrace();
            }
            //利用handler去更新View
            handler.sendEmptyMessage(0);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setListViewAdapter();

        }
    };
    private TextView UpdateTimeText;
    public void  setListViewAdapter(){
        LayoutInflater inflater = LayoutInflater.from(this);

        UpdateTimeText = (TextView)findViewById(R.id.UpdateTimeHeader);
        UpdateTimeText.setText("更新時間：" + UpdateTime);
        // View header = inflater.inflate(R.layout.listview_header, mListView, false);
        //mListView.addHeaderView(header, null, false);
        mListView.setAdapter(new ListViewAdapter(this, RateList));
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
