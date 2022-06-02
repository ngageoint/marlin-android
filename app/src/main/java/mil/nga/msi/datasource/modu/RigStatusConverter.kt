package mil.nga.msi.datasource.modu

import androidx.room.TypeConverter

class RigStatusConverter {
   @TypeConverter
   fun toRigStatus(value: String) = enumValueOf<RigStatus>(value.uppercase())

   @TypeConverter
   fun fromRigStatus(status: RigStatus) = status.name
}