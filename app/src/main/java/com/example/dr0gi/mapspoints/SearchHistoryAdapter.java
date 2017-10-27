package com.example.dr0gi.mapspoints;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryHolder> {
    private List<String> historyList;
    private Context context;
    private HistoryItemClickListener listener;

    interface HistoryItemClickListener {
        void onHistoryItemClicked(int pos);
    }

    SearchHistoryAdapter(Context context, List<String> historyList, HistoryItemClickListener listener) {
        this.historyList = historyList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public SearchHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.search_history_item, parent, false);
        return new SearchHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchHistoryHolder holder, int position) {
        String str = historyList.get(position);
        holder.bindData(str);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class SearchHistoryHolder extends RecyclerView.ViewHolder {
        private TextView historyItem;

        SearchHistoryHolder(View itemView) {
            super(itemView);
            historyItem = (TextView) itemView.findViewById(R.id.history_string);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onHistoryItemClicked(getAdapterPosition());
                }
            });
        }

        void bindData(String str) {
            historyItem.setText(str);
        }
    }
}
