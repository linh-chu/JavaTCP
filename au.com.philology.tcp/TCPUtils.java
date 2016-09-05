package au.com.philology.tcp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

public class TCPUtils
{
	public static Context theContext;

	@SuppressWarnings("deprecation")
	public static String getLocalAddress()
	{
		String iIPv4 = "";

		WifiManager wm = (WifiManager) theContext.getSystemService(Context.WIFI_SERVICE);

		iIPv4 = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

		return iIPv4;
	}
}
