package PortalClient.Authorization;

import PortalClient.Status.PortalStatus;
import Preferences.UserPreferences;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;

public class CheckAuthThread extends Thread{

    @Override
    public void run() {

        System.out.println("CheckAuthThread STARTED");

        try{
            while (true){
                if(!PortalStatus.getInstance().isPortalAvailable()) {
                    sleep(1000);
                    continue;
                }

                //System.out.println("CheckAuthThread RUN");
                Authorization.getInstance().sendAccessRequest();


                String accessToken = UserPreferences.getInstance().getAccessToken();
                String refreshToken = UserPreferences.getInstance().getRefreshToken();

                if(accessToken != null && refreshToken != null) {

                    try{
                        byte[] barr1 = Base64.getDecoder().decode(accessToken.split("\\.")[1]);
                        String encodedString1 = new String(barr1);
                        JSONObject jsonObject1 = (JSONObject) new JSONParser().parse(encodedString1);
                        long expTimeAccessToken = ((Long)jsonObject1.get("exp")).longValue();
                        Date expDateAccessToken = new Date(expTimeAccessToken*1000);
                        System.out.println("accessToken exp time = " + expDateAccessToken);

//                        if(expDateAccessToken.getTime() - new Date().getTime() <=60000){
//                            Authorization.getInstance().sendUpdateAccessTokenRequest();
//                        }

                        byte[] barr2 = Base64.getDecoder().decode(refreshToken.split("\\.")[1]);
                        String encodedString2 = new String(barr2);
                        JSONObject jsonObject2 = (JSONObject) new JSONParser().parse(encodedString2);
                        long expTimeRefreshToken = ((Long)jsonObject2.get("exp")).longValue();
                        Date expDateRefreshToken= new Date(expTimeRefreshToken*1000);
                        System.out.println("refreshToken exp time = " + expDateRefreshToken);

                        if(expDateRefreshToken.getTime() - new Date().getTime() <=60000){
                            //ADD UODATE REFRESHTOKEN LOGIC->
                            Authorization.getInstance().sendUpdateRefreshTokenRequest();
                        }

                    }catch(ParseException e){

                    }
                }

                sleep(30000);
            }

        }catch (InterruptedException e){
            System.out.println("CheckAuthThread INTERRUPTED");
        }

        System.out.println("CheckAuthThread STOPPED");
    }


}
