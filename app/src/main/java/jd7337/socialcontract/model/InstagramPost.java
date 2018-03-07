package jd7337.socialcontract.model;

import android.graphics.Bitmap;

/**
 * Created by sam on 3/6/18.
 */

public class InstagramPost {
    private String mediaId;
    private String caption;
    private Bitmap bitmap;

    public InstagramPost(String mediaId, String caption, Bitmap bitmap) {
        this.mediaId = mediaId;
        this.caption = caption;
        this.bitmap = bitmap;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
