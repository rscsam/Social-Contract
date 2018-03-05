package jd7337.socialcontract.model;

/**
 * Created by sam on 2/18/18.
 */

public class SocialMediaAccount {
    private String username;
    private String id;
    private AccountType typeResource;

    public SocialMediaAccount(String id, String username, AccountType typeResource) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
