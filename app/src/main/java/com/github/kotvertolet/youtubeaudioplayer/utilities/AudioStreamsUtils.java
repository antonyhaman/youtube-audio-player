package com.github.kotvertolet.youtubeaudioplayer.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.custom.exceptions.UserFriendlyException;
import com.github.kotvertolet.youtubeaudioplayer.data.NetworkType;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor;
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
import com.github.kotvertolet.youtubejextractor.models.AudioStreamItem;
import com.github.kotvertolet.youtubejextractor.models.StreamItem;
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;

import java.util.Collections;
import java.util.List;

public class AudioStreamsUtils {

    public YoutubeVideoData extractYoutubeVideoData(String videoId) throws UserFriendlyException {
        YoutubeVideoData youtubeVideoData;
        try {
            youtubeVideoData = new YoutubeJExtractor().extract(videoId);
        }
        // TODO: Do this in a good way
        catch (ExtractionException e) {
            throw new UserFriendlyException(R.string.generic_error_message,
                    e.getMessage());
        } catch (YoutubeRequestException e) {
            throw new UserFriendlyException(R.string.generic_error_message,
                    e.getMessage());
        }
        return youtubeVideoData;
    }

    public StreamItem getAudioStreamForVideo(YoutubeVideoData videoData) throws UserFriendlyException {
        return getBestStreamForInternetType(videoData);
    }

    public CacheUtil.CachingCounters getCachingCountersForSong(YoutubeSongDto songData) {
        Uri uri = Uri.parse(songData.getStreamUrl());
        return getCachingCountersForSong(uri);
    }

    public CacheUtil.CachingCounters getCachingCountersForSong(Uri uri) {
        CacheUtil.CachingCounters cachingCounters = new CacheUtil.CachingCounters();
        CacheUtil.getCached(new DataSpec(uri), App.getInstance().getPlayerCache(), cachingCounters);
        return cachingCounters;
    }

    public boolean isSongFullyCached(YoutubeSongDto songData) {
        CacheUtil.CachingCounters cachingCounters = getCachingCountersForSong(songData);
        App.getInstance().getPlayerCache().isCached(songData.getStreamUrl(), 0, cachingCounters.contentLength);
        return cachingCounters.alreadyCachedBytes == cachingCounters.contentLength;
    }

    public boolean isSongFullyCached(Uri uri) {
        CacheUtil.CachingCounters cachingCounters = getCachingCountersForSong(uri);
        return cachingCounters.alreadyCachedBytes == cachingCounters.contentLength;
    }

    private StreamItem getBestStreamForInternetType(YoutubeVideoData youtubeVideoData) throws UserFriendlyException {
        List<AudioStreamItem> audioStreamsList = youtubeVideoData.getStreamingData().getAudioStreamItems();
        if (audioStreamsList.size() > 0) {
            // Sorting streams by bitrate, from lowest to highest
            Collections.sort(audioStreamsList, (o1, o2) -> Integer.compare(o1.getBitrate(), o2.getBitrate()));

            SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
            int audioQualitySetting = sharedPreferences.getInt(Constants.PREFERENCE_AUDIO_QUALITY, 0);

            switch (audioQualitySetting) {
                case 0:
                    return getBestStreamByInternetType(audioStreamsList);
                case 1:
                    return audioStreamsList.get(audioStreamsList.size() - 1);
                case 2:
                    for (AudioStreamItem streamItem : audioStreamsList) {
                        int bitrate = streamItem.getBitrate();
                        if (bitrate > 95000 || streamItem.getBitrate() < 193000) {
                            return streamItem;
                        }
                    }
                case 3:
                default:
                    return audioStreamsList.get(0);
            }
        } else
            throw new UserFriendlyException(R.string.error_no_stream_for_video,
                    "There is no audio stream for the video with id: " + youtubeVideoData.getVideoDetails().getVideoId());
    }


    private StreamItem getBestStreamByInternetType(List<AudioStreamItem> audioStreamsList) {
        NetworkType networkType = App.getInstance().getCommonUtils().getNetworkClass();
        // I've discovered that old devices has problems with playing high bitrate streams for some reason. SDK 18 threshold was selected blindly
        if (App.getInstance().getCommonUtils().getAndroidVersion() <= 18) {
            return audioStreamsList.get(0);
        } else {
            switch (networkType) {
                case TYPE_WIFI:
                    return audioStreamsList.get(audioStreamsList.size() - 1);
                case TYPE_4G:
                case TYPE_3G:
                    for (AudioStreamItem streamItem : audioStreamsList) {
                        int bitrate = streamItem.getBitrate();
                        if (bitrate > 95000 || streamItem.getBitrate() < 193000) {
                            return streamItem;
                        }
                    }
                case TYPE_2G:
                    return audioStreamsList.get(0);
                // If there is no stream with required bitrate we're using the one with the lowest bitrate
                default:
                    return audioStreamsList.get(0);
            }
        }
    }
}
