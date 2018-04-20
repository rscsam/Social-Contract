package jd7337.socialcontract.model;

/**
 * Created by sam on 3/29/18.
 */

public class QueueItem {
   private String requestId;
   private String requestingUser;
   private String socialMediaId;
   private String mediaId;
   private String interactionType;

    public QueueItem(String requestId, String requestingUser, String socialMediaId, String mediaId, String interactionType) {
        this.requestId = requestId;
        this.requestingUser = requestingUser;
        this.socialMediaId = socialMediaId;
        this.mediaId = mediaId;
        this.interactionType = interactionType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(String requestingUser) {
        this.requestingUser = requestingUser;
    }

    public String getSocialMediaId() {
        return socialMediaId;
    }

    public void setSocialMediaId(String socialMediaId) {
        this.socialMediaId = socialMediaId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }
}
