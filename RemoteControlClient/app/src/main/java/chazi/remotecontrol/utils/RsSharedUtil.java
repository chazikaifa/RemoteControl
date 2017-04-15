package chazi.remotecontrol.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

//TODO: Auto-generated Javadoc

/**
 * 
 * @author milk
 * @version 1.0
 * @date-2016-1-25
 */
public class RsSharedUtil {

	private static final String SHARED_PATH = "app_share";
  
	public static SharedPreferences getDefaultSharedPreferences(Context context) {
		return context.getSharedPreferences(SHARED_PATH, Context.MODE_PRIVATE);
	}
	public static void clear(Context context){
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.clear();
		edit.commit();
	}
	public static void remove(Context context, String key){
	    	SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
			Editor edit = sharedPreferences.edit();
			edit.remove(key);
			edit.commit();
	    }
	public static void putInt(Context context, String key, int value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	public static int getInt(Context context, String key) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getInt(key, -5);
	}

	public static void putLong(Context context, String key, Long value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putLong(key, value);
		edit.commit();
	}

	public static Long getLong(Context context, String key) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getLong(key, 0);
	}

	public static void putString(Context context, String key, String value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putString(key, value);
		edit.commit();
	}

	public static String getString(Context context, String key) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getString(key, "");
	}

	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	public static boolean getBoolean(Context context, String key,
			boolean defValue) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(key, defValue);
	}

}
