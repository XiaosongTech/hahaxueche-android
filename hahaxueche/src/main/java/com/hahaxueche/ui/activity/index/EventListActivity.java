package com.hahaxueche.ui.activity.index;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hahaxueche.R;
import com.hahaxueche.model.activity.Event;
import com.hahaxueche.ui.adapter.index.EventAdapter;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by wangshirui on 16/7/27.
 */
public class EventListActivity extends IndexBaseActivity {
    private ListView mLvEvents;
    private ImageButton mIbtnBack;
    private EventAdapter mEventAdapter;
    private ArrayList<Event> mEventArrayList;

    private SharedPreferencesUtil spUtil;
    private final MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        spUtil = new SharedPreferencesUtil(EventListActivity.this);
        initViews();
        loadEventList();
        initEvents();
    }

    private void initViews() {
        mLvEvents = Util.instence(this).$(this, R.id.lv_events);
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
    }

    private void loadEventList() {
        mEventArrayList = spUtil.getEvents();
        if (mEventArrayList == null || mEventArrayList.size() < 1) return;
        parseCountDownText();
        mEventAdapter = new EventAdapter(EventListActivity.this, mEventArrayList, R.layout.adapter_event_corner);
        mLvEvents.setAdapter(mEventAdapter);
        mHandler.sendEmptyMessage(1);
    }

    private void initEvents() {
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventListActivity.this.finish();
            }
        });
        mLvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mEventArrayList != null && mEventArrayList.size() > 0 && position > -1 && position < mEventArrayList.size()) {
                    openWebView(mEventArrayList.get(position).getUrl(), mEventArrayList.get(position).getTitle(), true);
                }
            }
        });
    }

    static class MyHandler extends Handler {
        private final WeakReference<EventListActivity> mActivity;

        public MyHandler(EventListActivity activity) {
            mActivity = new WeakReference<EventListActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final EventListActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 1) {
                    activity.parseCountDownText();
                    activity.mEventAdapter.notifyDataSetChanged();
                    activity.mHandler.sendEmptyMessageDelayed(1, 1000);
                }
            }

        }
    }

    private void parseCountDownText() {
        for (Event event : mEventArrayList) {
            event.parseCountDownText();
        }
    }
}
