package mil.nga.msi.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken

fun JsonReader.nextStringOrNull(): String? {
   return if (peek() == JsonToken.NULL) {
      nextNull()
      null
   } else {
      nextString()
   }
}

fun JsonReader.nextLongOrNull(): Long? {
   return if (peek() == JsonToken.NULL) {
      nextNull()
      null
   } else {
      nextLong()
   }
}

fun JsonReader.nextDoubleOrNull(): Double? {
   return if (peek() == JsonToken.NULL) {
      nextNull()
      null
   } else {
      nextDouble()
   }
}

fun JsonReader.nextBooleanOrNull(): Boolean? {
   return if (peek() == JsonToken.NULL) {
      nextNull()
      null
   } else {
      nextBoolean()
   }
}

fun JsonReader.nextNumberOrNull(): Number? {
   return if (peek() == JsonToken.NULL) {
      nextNull()
      null
   } else {
      val value = nextString()
      try {
         value.toLong()
      } catch (e: NumberFormatException) {
         try {
            val asDouble = value.toDouble()
            if ((asDouble.isInfinite() || asDouble.isNaN()) && !isLenient) {
               null
            } else asDouble
         } catch(e: NumberFormatException) {
            null
         }
      }
   }
}

fun JsonElement.asJsonObjectOrNull(): JsonObject? {
   return if (isJsonObject) asJsonObject else null
}

fun JsonElement.asStringOrNull(): String? {
   return if (isJsonNull) null else asString
}

fun JsonElement.asIntOrNull(): Int? {
   return if (isJsonNull) null else {
      if (asJsonPrimitive.isNumber) asInt else null
   }
}

fun JsonElement.asLongOrNull(): Long? {
   return if (isJsonNull) null else {
      if (asJsonPrimitive.isNumber) asLong else null
   }
}

fun JsonElement.asDoubleOrNull(): Double? {
   return if (isJsonNull) null else {
      if (asJsonPrimitive.isNumber) asDouble else null
   }
}

fun JsonElement.asFloatOrNull(): Float? {
   return if (isJsonNull) null else {
      if (asJsonPrimitive.isNumber) asFloat else null
   }
}

fun JsonElement.asBooleanOrNull(): Boolean? {
   return if (isJsonNull) null else {
      if (asJsonPrimitive.isBoolean) asBoolean else null
   }
}