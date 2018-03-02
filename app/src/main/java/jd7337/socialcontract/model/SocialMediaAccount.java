package jd7337.socialcontract.model;

/**
 * Created by sam on 2/18/18.
 */

public class SocialMediaAccount {
    private String username;
    private AccountType typeResource;

    public SocialMediaAccount(String username, AccountType typeResource) {
        this.username = username;
        this.typeResource = typeResource;
    }

    public enum AccountType {
        FACEBOOK, TWITTER, INSTAGRAM
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AccountType getTypeResource() {
        return typeResource;
    }

    public void setTypeResource(AccountType typeResource) {
        this.typeResource = typeResource;
    }
}
