package com.github.kotvertolet.youtubeaudioplayer.adapters.recommendations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivityContract;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalRecommendationsAdapter extends RecyclerView.Adapter<VerticalRecommendationsAdapter.ViewHolder> {

    private Map<String, LinkedList<YoutubeSongDto>> dataMap;
    private MainActivityContract.Presenter presenter;
    private WeakReference<Context> context;
    private Object[] headersArr;
    private HorizontalRecommendationsAdapter childAdapter;

    public VerticalRecommendationsAdapter(Context context, MainActivityContract.Presenter presenter) {
        this.context = new WeakReference<>(context);
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_horizontal_recommendations, viewGroup, false);
        return new VerticalRecommendationsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String header = (String) headersArr[i];
        viewHolder.recommendationsHeader.setText(header);
        LinearLayoutManager horizontalLayoutManager =
                new LinearLayoutManager(context.get(), LinearLayoutManager.HORIZONTAL, false);
        viewHolder.rvRecommendations.setLayoutManager(horizontalLayoutManager);
        childAdapter = new HorizontalRecommendationsAdapter(presenter);
        childAdapter.setData(dataMap.get(header));
        viewHolder.rvRecommendations.setAdapter(childAdapter);
    }

    @Override
    public int getItemCount() {
        if (dataMap == null) {
            return 0;
        } else return dataMap.size();
    }

    public void replaceData(Map<String, LinkedList<YoutubeSongDto>> dataMap) {
        this.dataMap = dataMap;
        headersArr = dataMap.keySet().toArray();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView recommendationsHeader;
        RecyclerView rvRecommendations;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            recommendationsHeader = itemView.findViewById(R.id.explore_item_header);
            rvRecommendations = itemView.findViewById(R.id.rv_most_popular);
        }
    }
}
