package com.github.kotvertolet.youtubeaudioplayer.utilities.common;

/**
 * Created by ahaman on 3/28/18.
 */

public class Constants {

    //TODO: Put Youtube Data API v3 key here
    public final static String YOUTUBE_API_KEY = "";

    public static final String APP_NAME = "Youtube audio player";
    public static final String ENCODING_UTF_8 = "UTF-8";
    public static final String NOTIFICATION_SERVICE_ID = "yt_audio_notification_channel";
    public static final int FOREGROUND_SERVICE_ID = 505;
    public static final String STREAM_URI = "STREAM_URI";
    public static final int PLAYBACK_PROGRESS_CHANGED = 5;
    public static final int PLAYER_ERROR = 6;

    public static final String PLAYER_ERROR_MESSAGE = "PLAYER_ERROR_MESSAGE";
    public static final String PLAYER_ERROR_THROWABLE = "PLAYER_ERROR_THROWABLE2";

    public static final String EXTRA_TRACK_DURATION = "EXTRA_TRACK_DURATION";
    public static final String EXTRA_PLAYER_STATE_CODE = "EXTRA_PLAYER_STATE_CODE";

    public static final String ACTION_PLAYER_CHANGE_STATE = "ACTION_PLAYER_CHANGE_STATE";

    public static final String ACTION_PLAYLIST_NAVIGATION = "ACTION_PLAYLIST_NAVIGATION";
    public static final String ACTION_PLAYER_STATE_CHANGED = "ACTION_PLAYER_STATE_CHANGED";

    public static final String EXTRA_SONG = "EXTRA_SONG";
    public static final String EXTRA_PLAYBACK_STATUS = "EXTRA_PLAYBACK_STATUS";
    public static final String EXTRA_TRACK_PROGRESS = "EXTRA_TRACK_PROGRESS";

    public static final String NETWORK_TYPE_NOT_CONNECTED = "-";
    public static final String NETWORK_TYPE_WIFI = "WIFI";
    public static final String NETWORK_TYPE_2G = "2G";
    public static final String NETWORK_TYPE_3G = "3G";
    public static final String NETWORK_TYPE_4G = "4G";

    public final static String YOUTUBE_SITE_URL = "https://www.youtube.com/";
    public final static String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/";
    public final static String GOOGLE_SEARCH_SUGGESTIONS = "https://suggestqueries.google.com/";
    public final static String QUERY_PART_SNIPPET = "snippet";
    public final static String QUERY_PART_CONTENT_DETAILS = "contentDetails";
    public final static String QUERY_PART_STATISTICS = "statistics";
    public final static int QUERY_SEARCH_MAX_RESULTS = 25;
    public final static String QUERY_PLAYLIST_MAX_RESULTS = "10";
    public final static String QUERY_ORDER_VIEW_COUNT = "viewCount";
    public final static String QUERY_ORDER_RELEVANCE = "relevance";
    public final static String QUERY_TYPE_VIDEO = "video";
    public final static String QUERY_CHART_MOST_POPULAR = "mostPopular";
    public final static String QUERY_VIDEO_CATEGORY_MUSIC = "10";
    public final static String QUERY_SUGGESTIONS_OUTPUT = "firefox";
    public final static String QUERY_SUGGESTIONS_DS = "yt";

    public final static String BUNDLE_PLAYLISTS = "playlists";
    public final static String BUNDLE_NEW_SONG_FOR_PLAYLIST = "video_data";
    public final static String BUNDLE_SEARCH_RESULTS = "search_results";
    public final static String BUNDLE_RECOMMENDATIONS = "bundle_recommendations";
    public final static String BUNDLE_RECOMMENDATIONS_FAILED = "bundle_recommendations_failed";

    public final static String RECOMMENDATIONS_TOP_TRACKS = "TOP TRACKS";
    public final static String RECOMMENDATIONS_NEW_MUSIC_THIS_WEEK = "NEW MUSIC THIS WEEK";
    public final static String RECOMMENDATIONS_MOST_VIEWED = "MOST VIEWED";
    public final static String RECOMMENDATIONS_MOST_POPULAR_IN_REGION = "POPULAR IN YOUR REGION";
    public final static String RECOMMENDATIONS_RECENT = "RECENT";
    public final static String REGEX_YOUTUBE_PLAYLIST_VIDEOS = "<a class=\"pl-video-title-link yt-uix-tile-link yt-uix-sessionlink  spf-link \" dir=\"ltr\" href=\"\\/watch\\?v=(.*?)&";

    public final static String PLAYLIST_TOP_TRACKS_CHANNEL_ID = "PLFgquLnL59alcyTM2lkWJU34KtfPXQDaX";
    public final static String PLAYLIST_NEW_MUSIC_THIS_WEEK_CHANNEL_ID = "PLFgquLnL59alW3xmYiWRaoz0oM3H17Lth";
    public final static String PLAYLIST_MOST_VIEWED_CHANNEL_ID = "PL8A83124F1D79BD4F";

    public final static int DEFAULT_PLAYLIST_ID = -1;
    public final static String DEFAULT_PLAYLIST_NAME = "Playback queue";

    public final static String APP_PREFERENCES = "app_preferences";
    public final static String PREFERENCE_CACHE_SIZE = "pref_cache_size";
    public final static String PREFERENCE_AUDIO_QUALITY = "pref_audio_quality";
    public final static String PREFERENCE_NO_RECOMMENDATIONS = "pref_no_recommendations";
    public final static String PREFERENCE_RESTRICT_MOBILE_NETWORK_CACHING = "pref_mobile_network_caching";

    public final static String PREFERENCE_REPEAT = "pref_repeat";
    public final static String PREFERENCE_SHUFFLE = "pref_shuffle";

    public final static int REPEAT_MODE_NO_REPEAT = 0;
    public final static int REPEAT_MODE_REPEAT_ALL = 1;
    public final static int REPEAT_MODE_REPEAT_ONE = 2;

    public final static String[] RECOMMENDATIONS_HEADERS_ARR;

    static {
        RECOMMENDATIONS_HEADERS_ARR = new String[3];
        RECOMMENDATIONS_HEADERS_ARR[0] = RECOMMENDATIONS_TOP_TRACKS;
        RECOMMENDATIONS_HEADERS_ARR[1] = RECOMMENDATIONS_MOST_VIEWED;
        RECOMMENDATIONS_HEADERS_ARR[2] = RECOMMENDATIONS_NEW_MUSIC_THIS_WEEK;
    }
}
