package in2chris.calhacks.io.swearjar.photos;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import com.google.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 * Created by ckroetsc on 10/4/14.
 */
public class PhotoUtils {

  @Inject
  Context mContext;

  public InputStream openPhoto(long contactId) {
    Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
    Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    Cursor cursor = mContext.getContentResolver()
                            .query(photoUri, new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
    if (cursor == null) {
      return null;
    }
    try {
      if (cursor.moveToFirst()) {
        byte[] data = cursor.getBlob(0);
        if (data != null) {
          return new ByteArrayInputStream(data);
        }
      }
    } finally {
      cursor.close();
    }
    return null;
  }
}
