package mil.nga.msi.packagemanager

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

// Temporary compat extension until google adds to SDK
// https://issuetracker.google.com/issues/246845196
fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
   } else {
      getPackageInfo(packageName, flags)
   }