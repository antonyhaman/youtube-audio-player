package com.github.kotvertolet.youtubeaudioplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivity;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivityContract;
import com.github.kotvertolet.youtubeaudioplayer.adapters.SearchResultsAdapter;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.SearchResultsViewModel;
import com.github.kotvertolet.youtubeaudioplayer.data.models.YoutubeSearchResult;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchResultsFragment extends Fragment {

    private WeakReference<Context> context;
    private MutableLiveData<YoutubeSearchResult> searchResult;
    private SearchResultsAdapter adapter;
    private WeakReference<MainActivityContract.Presenter> presenter;
    private boolean appendData = false;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchResult = SearchResultsViewModel.getInstance().getData();
        return inflater.inflate(R.layout.layout_recommendations_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rvSearchResults = view.findViewById(R.id.rv_vertical_recommendations);
        registerForContextMenu(rvSearchResults);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context.get());
        MainActivity activity = (MainActivity) context.get();
        presenter = new WeakReference<>(activity.getPresenter());
        adapter = new SearchResultsAdapter(presenter);
        rvSearchResults.setLayoutManager(linearLayoutManager);
        rvSearchResults.setAdapter(adapter);
        rvSearchResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (getSearchResult() != null && getSearchResult().getNextPageToken() != null) {
                    if (!isLoading
                            && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        isLoading = true;
                        appendData = true;
                        presenter.get().searchYoutubeNextPage(getSearchResult().getQuery(), getSearchResult().getNextPageToken());
                    }
                }
            }
        });
        searchResult.observe(this, youtubeSongDtos -> {
            if (appendData) {
                adapter.addData(youtubeSongDtos.getSongs());
                isLoading = false;
                appendData = false;
            } else adapter.replaceData(youtubeSongDtos.getSongs());
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.song_item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(getString(R.string.menu_action_add_to_playlist))) {
            YoutubeSongDto youtubeSongDto = getSearchResult().getSongs().get(adapter.getAdapterPosition());
            presenter.get().addToPlaylist(youtubeSongDto);
        }
        //TODO: Implement downloading
//        else if(item.getTitle().equals(getString(R.string.menu_action_download))) {
//           getSearchResult().getSongs().get(adapter.getAdapterPosition()).getStreamUrl();
//        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        this.context = new WeakReference<>(context);
        super.onAttach(context);
    }

    private YoutubeSearchResult getSearchResult() {
        return searchResult.getValue();
    }
}
