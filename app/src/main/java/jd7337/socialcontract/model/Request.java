package jd7337.socialcontract.model;

/**
 * Created by Ali Khosravi on 2/28/2018.
 *
 */

public class Request {
    private int follows, likes, shares;
    public Request(int num, String type) {
        switch (type) {
            case "Like":
                likes = num;
                break;
            case "Follow":
                follows = num;
                break;
            default:
                shares = num;
        }
    }
    public void setNumFollows(int f) { follows = f;}
    public void setNumLikes(int l) { likes = l;}
    public void setNumShares(int s) { shares = s;}
    public int getNumFollows() { return this.follows;}
    public int getNumLikes() { return this.likes;}
    public int getNumShares() { return this.shares;}
}
