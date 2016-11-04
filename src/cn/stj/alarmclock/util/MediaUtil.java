package cn.stj.alarmclock.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class MediaUtil {

	public static String[] CURSOR_COLS = new String[] {
			MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.TRACK };

	public static boolean isSongExist(Context context, String name) {
		boolean isSongExist = false;
		Cursor c = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, CURSOR_COLS, null,
				null, null);
		if (c != null) {
			while (c.moveToNext()) {
				String songname = c.getString(c
						.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				String dispalyname = songname.substring(0,
						songname.lastIndexOf(".")).toLowerCase();

				if (name.equals(dispalyname)) {
					isSongExist = true;
					if (c != null)
						c.close();
					return isSongExist;

				} else {
					isSongExist = false;
				}
			}
		}
		if (c != null)
			c.close();
		return isSongExist;
	}
}
