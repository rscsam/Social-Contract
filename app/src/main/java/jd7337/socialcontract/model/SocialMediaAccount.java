package jd7337.socialcontract.model;

/**
 * Created by sam on 2/18/18.
 */

public class SocialMediaAccount {
    private String username;
    private int typeResource;

    public SocialMediaAccount(String username, int typeResource) {
        this.username = username;
        this.typeResource = typeResource;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTypeResource() {
        return typeResource;
    }

    public void setTypeResource(int typeResource) {
        this.typeResource = typeResource;
    }
}
