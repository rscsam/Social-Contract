package jd7337.socialcontract.view.holder;

/**
 * Created by sam on 11/26/17.
 */

public class AccountListItem {

    private int profilePicId, smTypePicId;
    private String profileName;

    public AccountListItem(int profilePicId, String profileName, int smTypePicId) {
        this.profilePicId = profilePicId;
        this.smTypePicId = smTypePicId;
        this.profileName = profileName;
    }

    public int getProfilePicId() {
        return profilePicId;
    }

    public void setProfilePicId(int profilePicId) {
        this.profilePicId = profilePicId;
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
}
