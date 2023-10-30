package mil.nga.msi.datasource.modu

import androidx.room.TypeConverter

class RigStatusConverter {
   @TypeConverter
   fun toRigStatus(value: String) = enumValueOf<RigStatus>(value.uppercase())
}