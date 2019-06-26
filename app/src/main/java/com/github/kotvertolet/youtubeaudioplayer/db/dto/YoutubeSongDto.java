package com.github.kotvertolet.youtubeaudioplayer.db.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "yt_data", indices = {@Index(value = "videoId", unique = true)})
public class YoutubeSongDto implements Parcelable {

    @Ignore
    public static final Creator<YoutubeSongDto> CREATOR = new Creator<YoutubeSongDto>() {
        @Override
        public YoutubeSongDto createFromParcel(Parcel in) {
            return new YoutubeSongDto(in);
        }

        @Override
        public YoutubeSongDto[] newArray(int size) {
            return new YoutubeSongDto[size];
        }
    };

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String videoId;
    private String title;
    private String author;
    private String duration;
    private int durationInSeconds;
    private String thumbnail;
    private String streamUrl;
    private String viewCount;
    private String likeCount;
    private String dislikeCount;
    private long lastPlayedTimestamp;

    @Ignore
    public YoutubeSongDto(String videoId, String title, String author, String duration,
                          int durationInSeconds, String thumbnail) {
        this.videoId = videoId;
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.durationInSeconds = durationInSeconds;
        this.thumbnail = thumbnail;
    }

    public YoutubeSongDto(String videoId, String title, String author, String duration,
                          int durationInSeconds, String thumbnail, String viewCount,
                          String likeCount, String dislikeCount) {
        this.videoId = videoId;
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.durationInSeconds = durationInSeconds;
        this.thumbnail = thumbnail;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
    }

    protected YoutubeSongDto(Parcel in) {
        this.id = in.readLong();
        this.videoId = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.duration = in.readString();
        this.durationInSeconds = in.readInt();
        this.thumbnail = in.readString();
        this.viewCount = in.readString();
        this.likeCount = in.readString();
        this.dislikeCount = in.readString();
        this.lastPlayedTimestamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(videoId);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(duration);
        dest.writeInt(durationInSeconds);
        dest.writeString(thumbnail);
        dest.writeString(viewCount);
        dest.writeString(likeCount);
        dest.writeString(dislikeCount);
        dest.writeLong(lastPlayedTimestamp);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(@NonNull String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(String dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public long getLastPlayedTimestamp() {
        return lastPlayedTimestamp;
    }

    public void setLastPlayedTimestamp(long lastPlayedTimestamp) {
        this.lastPlayedTimestamp = lastPlayedTimestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YoutubeSongDto)) return false;

        YoutubeSongDto songDto = (YoutubeSongDto) o;

        if (id != songDto.id) return false;
        if (durationInSeconds != songDto.durationInSeconds) return false;
        if (lastPlayedTimestamp != songDto.lastPlayedTimestamp) return false;
        if (videoId != null ? !videoId.equals(songDto.videoId) : songDto.videoId != null)
            return false;
        if (title != null ? !title.equals(songDto.title) : songDto.title != null) return false;
        if (author != null ? !author.equals(songDto.author) : songDto.author != null) return false;
        if (duration != null ? !duration.equals(songDto.duration) : songDto.duration != null)
            return false;
        if (thumbnail != null ? !thumbnail.equals(songDto.thumbnail) : songDto.thumbnail != null)
            return false;
        if (streamUrl != null ? !streamUrl.equals(songDto.streamUrl) : songDto.streamUrl != null)
            return false;
        if (viewCount != null ? !viewCount.equals(songDto.viewCount) : songDto.viewCount != null)
            return false;
        if (likeCount != null ? !likeCount.equals(songDto.likeCount) : songDto.likeCount != null)
            return false;
        return dislikeCount != null ? dislikeCount.equals(songDto.dislikeCount) : songDto.dislikeCount == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (videoId != null ? videoId.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + durationInSeconds;
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        result = 31 * result + (streamUrl != null ? streamUrl.hashCode() : 0);
        result = 31 * result + (viewCount != null ? viewCount.hashCode() : 0);
        result = 31 * result + (likeCount != null ? likeCount.hashCode() : 0);
        result = 31 * result + (dislikeCount != null ? dislikeCount.hashCode() : 0);
        result = 31 * result + (int) (lastPlayedTimestamp ^ (lastPlayedTimestamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "YoutubeSongDto{" +
                "id=" + id +
                ", videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", duration='" + duration + '\'' +
                ", durationInSeconds=" + durationInSeconds +
                ", thumbnail='" + thumbnail + '\'' +
                ", streamUrl='" + streamUrl + '\'' +
                ", viewCount='" + viewCount + '\'' +
                ", likeCount='" + likeCount + '\'' +
                ", dislikeCount='" + dislikeCount + '\'' +
                ", lastPlayedTimestamp=" + lastPlayedTimestamp +
                '}';
    }
}
