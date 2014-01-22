package com.bluetooth.le;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluetooth.le.model.StoreItem;

import java.util.ArrayList;

/**
 * Created by stadiko on 1/21/14.
 */
public class UserListFragment extends ListFragment {

    private ArrayList<StoreItem> userItems;
    private Button showOnMapBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        setListAdapter(new UserListAdapter());
        showOnMapBtn = (Button)view.findViewById(R.id.showListOnMapBtn);
        showOnMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra(MapActivity.EXTRA_STORE_ITEMS, true);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userItems = new ArrayList<StoreItem>();
        for(String key : DummyData.userItems.keySet()) {
            userItems.add(DummyData.userItems.get(key));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private class UserListAdapter extends BaseAdapter {
        private ViewHolder holder;

        @Override
        public int getCount() {
            return userItems.size();
        }

        @Override
        public Object getItem(int i) {
            return userItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View vi = convertView;

            if (vi == null) {
                holder = new ViewHolder();

                vi = View.inflate(getActivity(), R.layout.row_search_result, null);

                vi.setTag(holder);
            }

            final StoreItem item = userItems.get(i);

            holder.priceTxt = (TextView) vi.findViewById(R.id.priceTxt);
            holder.nameTxt = (TextView) vi.findViewById(R.id.nameTxt);
            holder.categoryTxt = (TextView) vi.findViewById(R.id.categoryTxt);
            holder.addBtn = (ImageButton) vi.findViewById(R.id.addToListBtn);
            holder.showOnMap = (ImageButton) vi.findViewById(R.id.showOnMapBtn);

            holder.nameTxt.setText(item.getName());
            holder.categoryTxt.setText(item.getCategory().getCategoryName());
            holder.priceTxt.setText("$" + String.format("%1.2f", item.getPrice()));

            holder.addBtn.setImageResource(R.drawable.ic_content_remove);

            holder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userItems.remove(item);
                    DummyData.userItems.remove(item.getItemId());
                    notifyDataSetChanged();
                }
            });

            holder.showOnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MapActivity.class);
                    intent.putExtra(MapActivity.EXTRA_STORE_ITEM, item.getItemId());
                    startActivity(intent);
                }
            });

            return vi;
        }
    }

    private static class ViewHolder {
        TextView nameTxt;
        TextView categoryTxt;
        TextView priceTxt;
        ImageButton addBtn;
        ImageButton showOnMap;
    }
}
