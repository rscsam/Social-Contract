package jd7337.socialcontract.view.holder;

import android.graphics.Bitmap;

import jd7337.socialcontract.R;
import jd7337.socialcontract.model.SocialMediaAccount;

/**
 * Created by sam on 11/26/17.
 *
 */

public class AccountListItem {

    private Bitmap profilePicBitmap;
    private String profileName;
    private SocialMediaAccount.AccountType socialMediaType; // "TWITTER" or "INSTAGRAM"
    private String smId;

    private boolean showingExtraSettings = false;
    private boolean showDelete = false;

    public AccountListItem(Bitmap profilePicBitmap, String profileName,
                           String smId, SocialMediaAccount.AccountType socialMediaType) {
        this.profilePicBitmap = profilePicBitmap;
        this.profileName = profileName;
        this.socialMediaType = socialMediaType;
        this.smId = smId;
    }

    public Bitmap getProfilePicBitmap() {
        return profilePicBitmap;
    }

    public void setProfilePicBitmap(Bitmap profilePicBitmap) {
        this.profilePicBitmap = profilePicBitmap;
    }

    public int getSmTypePicId() {
        if (socialMediaType == SocialMediaAccount.AccountType.INSTAGRAM) {
            return R.drawable.instagram_icon;
        } else {
            return R.drawable.twitter_icon;
        }
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public SocialMediaAccount.AccountType getSocialMediaType() {
        return socialMediaType;
    }

    public void setSocialMediaType(SocialMediaAccount.AccountType socialMediaType) {
        this.socialMediaType = socialMediaType;
    }

    public String getSmId() {
        return smId;
    }

    public void setSmId(String smId) {
        this.smId = smId;
    }

    public boolean isShowingExtraSettings() {
        return showingExtraSettings;
    }

    public void setShowingExtraSettings(boolean showingExtraSettings) {
        this.showingExtraSettings = showingExtraSettings;
    }

    public boolean isShowDelete() {
        return showDelete;
    }

    public void setShowDelete(boolean showDelete) {
        this.showDelete = showDelete;
    }
}
