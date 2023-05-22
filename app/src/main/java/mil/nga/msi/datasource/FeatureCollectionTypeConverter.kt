package mil.nga.msi.datasource

import androidx.room.TypeConverter
import mil.nga.sf.geojson.FeatureCollection
import mil.nga.sf.geojson.FeatureConverter

class FeatureCollectionTypeConverter {
   @TypeConverter
   fun fromGeoJson(value: String?): FeatureCollection? {
      return value?.let { FeatureConverter.toFeatureCollection(value) }
   }

   @TypeConverter
   fun toGeoJson(features: FeatureCollection?): String? {
      return FeatureConverter.toStringValue(features)
   }
}