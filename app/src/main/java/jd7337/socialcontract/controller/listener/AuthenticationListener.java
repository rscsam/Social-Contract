package jd7337.socialcontract.controller.listener;

/**
 * Created by Ali Khosravi on 1/22/2018.
 */

public interface AuthenticationListener {

    void onCodeReceived(String auth_token);

}
