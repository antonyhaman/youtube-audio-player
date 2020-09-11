package com.github.kotvertolet.youtubeaudioplayer.listeners;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.CommonUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class SlidingPanelListener implements SlidingUpPanelLayout.PanelSlideListener {

    private final float multiplier;
    private CommonUtils utils;
    private WeakReference<Context> context;
    private ImageView ivChevron, ivPlayerThumb;
    private LinearLayout llControls;
    private TextView tvPlayerSongTitle, tvPlayerSongChannel, tvPlayerSecondarySongTitle, tvPlayerSecondarySongChannel;

    public SlidingPanelListener(Context context, ImageView ivChevron, LinearLayout llControls,
                                ImageView ivPlayerThumb, TextView tvPlayerSongTitle, TextView tvPlayerSongChannel,
                                TextView tvPlayerSecondarySongTitle, TextView tvPlayerSecondarySongChannel) {
        utils = new CommonUtils();
        this.context = new WeakReference<>(context);
        multiplier = context.getResources().getDisplayMetrics().densityDpi / 100;
        this.ivChevron = ivChevron;
        this.ivPlayerThumb = ivPlayerThumb;
        this.llControls = llControls;
        this.tvPlayerSongTitle = tvPlayerSongTitle;
        this.tvPlayerSongChannel = tvPlayerSongChannel;
        this.tvPlayerSecondarySongTitle = tvPlayerSecondarySongTitle;
        this.tvPlayerSecondarySongChannel = tvPlayerSecondarySongChannel;
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        transformThumbnail(slideOffset, multiplier);
        transformInfo(slideOffset);
        transformControls(slideOffset, multiplier);
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
    }

    public void refreshTransformableElements() {
        transformThumbnail(1, multiplier);
        transformControls(1, multiplier);
    }

    private void transformChevron(float slideOffset) {
        ivChevron.setRotation(slideOffset * 90);

    }

    private void transformThumbnail(float slideOffset, float multiplier) {
        int initialDimensionsInPx = (int) utils.dpInPx(50, context.get());
        int initialLeftMargin = (int) (utils.dpInPx(40, context.get()));
        int initialTopMargin = (int) utils.dpInPx(5, context.get());

        int newDimen = (int) Math.ceil((slideOffset) * initialDimensionsInPx + initialDimensionsInPx);
        int newLeftMargin = (int) Math.ceil((slideOffset) * initialLeftMargin);
        int newMarginTop = (int) Math.ceil((slideOffset) * initialTopMargin + initialTopMargin);

        if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            newLeftMargin = (int) Math.ceil(newLeftMargin * multiplier);
        }

        RelativeLayout.LayoutParams thumbnailParams = new RelativeLayout.LayoutParams(newDimen, newDimen);
        thumbnailParams.setMargins(newLeftMargin, newMarginTop, 0, 0);
        ivPlayerThumb.setLayoutParams(thumbnailParams);
    }

    private void transformControls(float slideOffset, float multiplier) {
        int initialRightMargin = (int) (utils.dpInPx(36, context.get()));
        int topMarginCoef = (int) (utils.dpInPx(35, context.get()));
        int initialTopMargin = (int) (utils.dpInPx(10, context.get()));

        //TODO: For controls resizing
        //int initialDimensionsInPx = (int) utils.dpInPx(40, context.get());
        //int newDimen = (int) Math.ceil((slideOffset) * initialDimensionsInPx + initialDimensionsInPx);

        int newTopMargin = (int) Math.ceil((slideOffset) * topMarginCoef + initialTopMargin);
        int newRightMargin;
        if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            newRightMargin = (int) Math.ceil(((slideOffset) * initialRightMargin) * multiplier);
        } else {
            newRightMargin = (int) Math.ceil((slideOffset) * initialRightMargin);
        }

        RelativeLayout.LayoutParams controlParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        controlParams.setMargins(0, newTopMargin, newRightMargin, 0);
        controlParams.addRule(RelativeLayout.LEFT_OF, R.id.iv_chevron);
        llControls.setLayoutParams(controlParams);
    }

    private void transformInfo(float offset) {
        float mainAlpha = (float) (1.0 - offset * 2);
        float secondaryAlpha = (float) (offset * 1.4);
        tvPlayerSongTitle.setAlpha(mainAlpha);
        tvPlayerSongChannel.setAlpha(mainAlpha);
        tvPlayerSecondarySongTitle.setAlpha(secondaryAlpha);
        tvPlayerSecondarySongChannel.setAlpha(secondaryAlpha);
    }

    private int getOrientation() {
        return context.get().getResources().getConfiguration().orientation;
    }
}
