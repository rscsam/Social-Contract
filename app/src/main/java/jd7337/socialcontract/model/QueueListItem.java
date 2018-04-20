package jd7337.socialcontract.model;

import android.graphics.Bitmap;

import jd7337.socialcontract.R;

/**
 * Created by Ryan on 3/7/2018.
 */

public class QueueListItem {

    private Bitmap profilePic;
    private String username;
    private int progress;
    private int goal;
    private String requestType;
    private SocialMediaAccount.AccountType socialMediaType;

    public QueueListItem(Bitmap profilePic, String username, int progress, int goal, String requestType,
                         SocialMediaAccount.AccountType socialMediaType) {
        this.profilePic = profilePic;
        this.username = username;
        this.progress = progress;
        this.goal = goal;
        this.requestType = requestType;
        this.socialMediaType = socialMediaType;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bitmap getBitmap() {
        return profilePic;
    }

    public String getUsername() {
        return username;
    }

    public int getProgressToGoal() {
        if (goal > 0) {
            double value = (double) progress / (double) goal;
            return (int)(value * 100);
        } else {
            return 0;
        }

    }

    public String getNum() {
        String text = "";
        text += progress + " / " + goal;

        return text;

    }

    public String getText() {
        String properRequestType = requestType.substring(0,1);
        properRequestType += requestType.substring(1).toLowerCase();

        if (goal > 1) {
            properRequestType += "s";
        }

        return properRequestType;

    }

    public int getImageResource() {
        if (socialMediaType == SocialMediaAccount.AccountType.TWITTER) {
            return R.drawable.twitter_icon;
        } else {
            return R.drawable.instagram_icon;
        }
    }
}
