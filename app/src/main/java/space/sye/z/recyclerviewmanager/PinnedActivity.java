package space.sye.z.recyclerviewmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.wangjie.androidbucket.support.recyclerview.pinnedlayout.PinnedRecyclerViewLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import space.sye.z.library.RefreshRecyclerView;
import space.sye.z.library.manager.RecyclerMode;
import space.sye.z.library.manager.RecyclerViewManager;
import space.sye.z.recyclerviewmanager.model.Person;
import space.sye.z.recyclerviewmanager.pinned.adapter.PersonTypeAdapter;

public class PinnedActivity extends AppCompatActivity implements PinnedRecyclerViewLayout.OnRecyclerViewPinnedViewListener {

    RefreshRecyclerView recyclerView;
    private List<Person> personList = new ArrayList<>();
    private int i;
    private PersonTypeAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RefreshRecyclerView) findViewById(R.id.recyclerView);
        //
        initData();
        adapter = new PersonTypeAdapter(this, personList, null, null);
        //recyclerView.setAdapter(adapter);

        RecyclerViewManager.with(adapter, new LinearLayoutManager(this))
                .setMode(RecyclerMode.BOTH)
                .into(recyclerView, this);

        //final ABaseLinearLayoutManager layoutManager = new ABaseLinearLayoutManager(this);
        //recyclerView.initRecyclerPinned(layoutManager, LayoutInflater.from(this).inflate(R.layout.recycler_view_item_float, null));
    }

    private void initData() {
        Random random = new Random();
        for (; i < 30; ++i) {
            Person person = new Person(i, "WangJie_" + i, 10 + i, random.nextInt(2));
            personList.add(person);
        }
    }

    // 渲染pinnedView数据
    @Override
    public void onPinnedViewRender(PinnedRecyclerViewLayout pinnedRecyclerViewLayout, View pinnedView, int position) {
        switch (pinnedRecyclerViewLayout.getId()) {
            case R.id.recyclerView:
                TextView nameTv = (TextView) pinnedView.findViewById(R.id.recycler_view_item_float_name_tv);
                nameTv.setText(personList.get(position).getName());
                TextView ageTv = (TextView) pinnedView.findViewById(R.id.recycler_view_item_float_age_tv);
                ageTv.setText(personList.get(position).getAge() + "岁");
                break;
        }
    }

}
