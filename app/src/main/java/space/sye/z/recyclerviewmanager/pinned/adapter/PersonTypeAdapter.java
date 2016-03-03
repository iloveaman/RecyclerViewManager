package space.sye.z.recyclerviewmanager.pinned.adapter;

import android.content.Context;
import android.view.View;

import com.wangjie.androidbucket.adapter.typeadapter.ABAdapterTypeRender;
import com.wangjie.androidbucket.support.recyclerview.adapter.extra.ABRecyclerViewTypeExtraHolder;
import com.wangjie.androidbucket.support.recyclerview.adapter.extra.ABRecyclerViewTypeExtraViewAdapter;

import java.util.List;

import space.sye.z.recyclerviewmanager.model.Person;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
public class PersonTypeAdapter extends ABRecyclerViewTypeExtraViewAdapter {
    public List<Person> getList() {
        return list;
    }

    private Context context;
    private List<Person> list;

    public PersonTypeAdapter(Context context, List<Person> list, View headerView, View footerView) {
        super(headerView, footerView);
        this.context = context;
        this.list = list;
    }

    @Override
    public ABAdapterTypeRender<ABRecyclerViewTypeExtraHolder> getAdapterTypeRenderExcludeExtraView(int type) {
        ABAdapterTypeRender<ABRecyclerViewTypeExtraHolder> render = null;
        switch (type) {
            case 0:
                render = new PersonTypeARender(context, this);
                break;
            case 1:
                render = new PersonTypeBRender(context, this);
                break;
        }
        return render;
    }

    @Override
    public int getItemCountExcludeExtraView() {
        return list.size();
    }

    @Override
    public int getItemViewTypeExcludeExtraView(int realItemPosition) {
        return list.get(realItemPosition).getType();
    }


}
