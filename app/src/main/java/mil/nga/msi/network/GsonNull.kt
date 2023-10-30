package mil.nga.msi.network

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

fun JsonReader.nextIntOrNull(): Int? {
   return if (peek() == JsonToken.NULL) {
      nextNull()
      null
   } else {
      nextInt()
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