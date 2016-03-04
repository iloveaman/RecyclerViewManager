package space.sye.z.recyclerviewmanager.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cpoopc.scrollablelayoutlib.ScrollableHelper;

import java.util.ArrayList;
import java.util.Random;

import space.sye.z.library.RefreshRecyclerView;
import space.sye.z.library.adapter.RefreshRecyclerViewAdapter;
import space.sye.z.library.listener.OnBothRefreshListener;
import space.sye.z.library.manager.RecyclerMode;
import space.sye.z.library.manager.RecyclerViewManager;
import space.sye.z.recyclerviewmanager.R;
import space.sye.z.recyclerviewmanager.fragment.base.ScrollAbleFragment;

public class ListFragment extends ScrollAbleFragment implements ScrollableHelper.ScrollableContainer {

    private ArrayList<String> mDatas;
    private RefreshRecyclerView recyclerView;
    LayoutInflater mInflater;

    private MyAdapter myAdapter;

    public static ListFragment newInstance() {
        ListFragment listFragment = new ListFragment();
        return listFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mInflater = inflater;
        recyclerView = (RefreshRecyclerView) view.findViewById(R.id.recyclerView);
        myAdapter = new MyAdapter();

        mDatas = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(100) + 31; i++) {
            mDatas.add("Item " + i);
        }

        RecyclerViewManager.with(myAdapter, new LinearLayoutManager(getActivity()))
                .setMode(RecyclerMode.BOTH)
                //.addHeaderView(header)
                //.addHeaderView(header2)
                //.addFooterView(footer)
                .setOnBothRefreshListener(new OnBothRefreshListener() {
                    @Override
                    public void onPullDown() {
                        //模拟网络请求
                        Message msg = new Message();
                        mHandler.sendMessageDelayed(msg, 2000);
                    }

                    @Override
                    public void onLoadMore() {
                        Message msg = new Message();
                        mHandler.sendMessageDelayed(msg, 2000);
                    }
                })
                .setOnItemClickListener(new RefreshRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                        Toast.makeText(getActivity(), "onItemClick item" + position, Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnItemLongClickListener(new RefreshRecyclerViewAdapter.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongCLick(RecyclerView.ViewHolder holder, int position) {
                        Toast.makeText(getActivity(), "onItemLongCLick item" + position, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .into(recyclerView, getActivity());
        return view;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            recyclerView.onRefreshCompleted();
            myAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public View getScrollableView() {
        return recyclerView.getRecyclerView();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_item = (TextView) itemView.findViewById(R.id.tv_item);
        }

    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mInflater.inflate(R.layout.recycler_item, parent, false);
            MyViewHolder holder = new MyViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv_item.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }

//    private ListView mListview;
//
//    public static ListFragment newInstance() {
//        ListFragment listFragment = new ListFragment();
//        return listFragment;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_list, container, false);
//        mListview = (ListView) view.findViewById(R.id.listview);
//        List<String> strlist = new ArrayList<String>();
//        for (int i = 0; i < new Random().nextInt(100) + 31; i++) {
//            strlist.add(String.valueOf(i));
//        }
//        mListview.setAdapter(new MyAdapter(getActivity(), strlist));
//        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), "点击item" + position, Toast.LENGTH_SHORT).show();
//            }
//        });
//        return view;
//    }
//
//    @Override
//    public View getScrollableView() {
//        return mListview;
//    }
}
