package mil.nga.msi.datasource.port.types

import mil.nga.msi.datasource.navigationwarning.NavigationArea

interface EnumerationType {
   val name: String
   val title: String

   companion object {
      private const val DECISION_TYPE_NAME = "DECISION"
      private const val HARBOR_TYPE_NAME = "HARBOR"
      private const val HARBOR_USE_TYPE_NAME = "HARBOR_USE"
      private const val REPAIR_CODE_TYPE_NAME = "REPAIR_CODE"
      private const val SHELTER_TYPE_NAME = "SHELTER"
      private const val SIZE_TYPE_NAME = "SIZE"
      private const val UNDERKEEL_CLEARANCE_TYPE_NAME = "UNDERKEEL_CLEARANCE"
      private const val NAVIGATION_AREA_TYPE_NAME = "NAVIGATION_AREA"

      fun fromString(valueString: String?): EnumerationType? {
         val values = valueString?.split(",")
         val type = values?.getOrNull(0)
         val value = values?.getOrNull(1)

         return when(type) {
            DECISION_TYPE_NAME -> Decision.fromValue(value)
            HARBOR_TYPE_NAME -> HarborType.fromValue(value)
            HARBOR_USE_TYPE_NAME -> HarborUse.fromValue(value)
            REPAIR_CODE_TYPE_NAME -> RepairCode.fromValue(value)
            SHELTER_TYPE_NAME -> Shelter.fromValue(value)
            SIZE_TYPE_NAME -> Size.fromValue(value)
            UNDERKEEL_CLEARANCE_TYPE_NAME -> UnderkeelClearance.fromValue(value)
            NAVIGATION_AREA_TYPE_NAME -> NavigationArea.fromValue(value)
            else -> null
         }
      }

      fun toString(type: EnumerationType): String? {
         return when (type) {
            is Decision -> "${DECISION_TYPE_NAME},${type.name}"
            is HarborType -> "${HARBOR_TYPE_NAME},${type.name}"
            is HarborUse -> "${HARBOR_USE_TYPE_NAME},${type.name}"
            is RepairCode -> "${REPAIR_CODE_TYPE_NAME},${type.name}"
            is Shelter -> "${SHELTER_TYPE_NAME},${type.name}"
            is Size -> "${SIZE_TYPE_NAME},${type.name}"
            is UnderkeelClearance -> "${UNDERKEEL_CLEARANCE_TYPE_NAME},${type.name}"
            is NavigationArea -> "${NAVIGATION_AREA_TYPE_NAME},${type.name}"
            else -> null
         }
      }
   }
}