/*
@actor Ricardo Adalberto Iraheta Amaya
 */
package com.rica.parcial2c2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DetactarInternet {

    private Context _context;

    public DetactarInternet(Context _context) {
        this._context = _context;
    }

    public boolean hayConexionInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if( connectivityManager!=null ){
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if(networkInfos!=null){
                for(int i=0; i<networkInfos.length; i++){
                    if(networkInfos[i].getState()==NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
}

}
