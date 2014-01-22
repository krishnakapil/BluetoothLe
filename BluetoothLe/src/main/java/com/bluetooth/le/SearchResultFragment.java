package com.bluetooth.le;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluetooth.le.model.StoreItem;

import java.util.ArrayList;

/**
 * Created by stadiko on 1/21/14.
 */
public class SearchResultFragment extends ListFragment {

    public interface SearchResultFragmentInterface {
        public void OnListButtonClicked();
    }

    private SearchResultFragmentInterface callBack;

    private static final String EXTRA_QUERY = "extra_query";

    private ArrayList<StoreItem> storeItems;

    public static SearchResultFragment newInstance(String query) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    public String getQueryString() {
        if (getArguments() != null) {
            return getArguments().getString(EXTRA_QUERY);
        }

        return "";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try
        {
            callBack = (SearchResultFragmentInterface) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        setListAdapter(new SearchResultAdapter());
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        storeItems = new ArrayList<StoreItem>();

        for (String key : DummyData.items.keySet()) {
            StoreItem si = DummyData.items.get(key);
            if (si.getName().toLowerCase().contains(getQueryString().toLowerCase())) {
                storeItems.add(si);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private class SearchResultAdapter extends BaseAdapter {
        private ViewHolder holder;

        @Override
        public int getCount() {
            return storeItems.size();
        }

        @Override
        public Object getItem(int i) {
            return storeItems.get(i);
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

            final StoreItem item = storeItems.get(i);

            holder.priceTxt = (TextView) vi.findViewById(R.id.priceTxt);
            holder.nameTxt = (TextView) vi.findViewById(R.id.nameTxt);
            holder.categoryTxt = (TextView) vi.findViewById(R.id.categoryTxt);
            holder.addBtn = (ImageButton) vi.findViewById(R.id.addToListBtn);
            holder.showOnMap = (ImageButton) vi.findViewById(R.id.showOnMapBtn);

            holder.nameTxt.setText(item.getName());
            holder.categoryTxt.setText(item.getCategory().getCategoryName());
            holder.priceTxt.setText("$" + String.format("%1.2f", item.getPrice()));

            if(DummyData.userItems.containsKey(item.getItemId())) {
                holder.addBtn.setImageResource(R.drawable.ic_content_remove);
            } else {
                holder.addBtn.setImageResource(R.drawable.ic_content_new);
            }

            holder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!DummyData.userItems.containsKey(item.getItemId())) {
                        DummyData.userItems.put(item.getItemId(), item);
                    } else {
                        DummyData.userItems.remove(item.getItemId());
                    } 
                    notifyDataSetChanged();
                    getActivity().invalidateOptionsMenu();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.list_item).getActionView();
        badgeLayout.findViewById(R.id.list_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.OnListButtonClicked();
            }
        });
        TextView tv = (TextView) badgeLayout.findViewById(R.id.actionbar_notifcation_textview);
        tv.setText(DummyData.userItems.size() + "");
        super.onPrepareOptionsMenu(menu);
    }

    private static class ViewHolder {
        TextView nameTxt;
        TextView categoryTxt;
        TextView priceTxt;
        ImageButton addBtn;
        ImageButton showOnMap;
    }
}
