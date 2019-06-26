package com.github.kotvertolet.youtubeaudioplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivity;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivityContract;
import com.github.kotvertolet.youtubeaudioplayer.adapters.recommendations.VerticalRecommendationsAdapter;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.RecommendationsViewModel;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import org.reactivestreams.Subscription;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.RECOMMENDATIONS_RECENT;

public class RecommendationsFragment extends Fragment {

    private WeakReference<Context> context;
    private MutableLiveData<Map<String, LinkedList<YoutubeSongDto>>> adapterData;
    private VerticalRecommendationsAdapter verticalRecommendationsAdapter;
    private WeakReference<MainActivityContract.Presenter> presenter;
    private CompositeDisposable disposables;

    private MutableLiveData<Map<String, LinkedList<YoutubeSongDto>>> recommendationsViewModel;

    public RecommendationsFragment() {
        recommendationsViewModel = RecommendationsViewModel.getInstance().getData();
        disposables = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_recommendations_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rvRecommendations = view.findViewById(R.id.rv_vertical_recommendations);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context.get());
        MainActivity activity = (MainActivity) context.get();
        presenter = new WeakReference<>(activity.getPresenter());
        // Setting up the adapter
        verticalRecommendationsAdapter = new VerticalRecommendationsAdapter(activity, activity.getPresenter());
        rvRecommendations.setLayoutManager(linearLayoutManager);
        rvRecommendations.setAdapter(verticalRecommendationsAdapter);
        registerForContextMenu(rvRecommendations);

        // Observing recommendations
        recommendationsViewModel.observe(this, verticalRecommendationsAdapter::replaceData);

        disposables.add(App.getInstance().getDatabase().youtubeSongDao().getLastPlayedRx(20)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeSongDtos -> {
                    Map<String, LinkedList<YoutubeSongDto>> map = recommendationsViewModel.getValue();
                    LinkedList<YoutubeSongDto> linkedList = map.get(RECOMMENDATIONS_RECENT);
                    if (linkedList != null && linkedList.size() > 0 && !linkedList.equals(youtubeSongDtos)) {
                        map.put(RECOMMENDATIONS_RECENT, new LinkedList<>(youtubeSongDtos));
                    }
                }));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = new WeakReference<>(context);
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}
