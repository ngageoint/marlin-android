package mil.nga.msi.resource

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri

fun Resources.uri(resourceId: Int): Uri? {
   return Uri.parse(
      ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
              getResourcePackageName(resourceId) + '/' +
              getResourceTypeName(resourceId) + '/' +
              getResourceEntryName(resourceId)
   )
}