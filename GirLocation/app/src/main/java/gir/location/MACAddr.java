package gir.location;

import android.content.Context;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MACAddr {
    public static String value;

    public static String getMAC(Context context) {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String hex = Integer.toHexString(b & 0xFF);
                    if (hex.length() == 1)
                        hex = "0".concat(hex);
                    res1.append(hex.concat(":"));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
//                value="48:2c:a0:6e:32:76";//mareMob
//                value="48:27:ea:b7:a8:40";//vikiMob
//                value="ca:2c:4f:5f:48:7c";//indTablet1
//                value="60:45:cb:58:75:a6";//povrsinska
//                value="B0:6F:E0:9D:5E:46";//masinFska
//                value="14:5F:94:B8:D7:34";//pakovanje i okivanje
//                value ="D0:7F:A0:0B:D6:21";//pakovanje i okivanje
//                value="B0:6F:E0:9D:5F:E0";//montjaza
//                value="88:BD:45:F2:FB:92";//krplenje

                value = res1.toString();
                return value;// res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

}
