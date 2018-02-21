package jd7337.socialcontract.view.holder;

import android.graphics.Bitmap;

/**
 * Created by sam on 11/26/17.
 */

public class AccountListItem {

    private Bitmap profilePicBitmap;
    private int smTypePicId;
    private String profileName;

    private boolean showingExtraSettings = false;

    public AccountListItem(Bitmap profilePicBitmap, String profileName, int smTypePicId) {
        this.profilePicBitmap = profilePicBitmap;
        this.smTypePicId = smTypePicId;
        this.profileName = profileName;
    }

    public Bitmap getProfilePicBitmap() {
        return profilePicBitmap;
    }

    public void setProfilePicBitmap(Bitmap profilePicBitmap) {
        this.profilePicBitmap = profilePicBitmap;
    }

    public int getSmTypePicId() {
        return smTypePicId;
    }

    public void setSmTypePicId(int smTypePicId) {
        this.smTypePicId = smTypePicId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public boolean isShowingExtraSettings() {
        return showingExtraSettings;
    }

    public void setShowingExtraSettings(boolean showingExtraSettings) {
        this.showingExtraSettings = showingExtraSettings;
    }
}
