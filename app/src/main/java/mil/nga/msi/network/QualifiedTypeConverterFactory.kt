package mil.nga.msi.network

import android.util.Log
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.annotation.Nullable

@Retention(AnnotationRetention.RUNTIME)
annotation class Json

@Retention(AnnotationRetention.RUNTIME)
annotation class Xml

class QualifiedTypeConverterFactory(
   jsonFactory: Converter.Factory,
   xmlFactory: Converter.Factory
) : Converter.Factory() {

   private val jsonFactory: Converter.Factory
   private val xmlFactory: Converter.Factory

   init {
      this.jsonFactory = jsonFactory
      this.xmlFactory = xmlFactory
   }

   @Nullable
   override fun responseBodyConverter(
      type: Type,
      annotations: Array<out Annotation>,
      retrofit: Retrofit
   ): Converter<ResponseBody, *>? {
      for (annotation in annotations) {
         return when (annotation) {
            is Xml -> {
               xmlFactory.responseBodyConverter(type, annotations, retrofit)
            } else -> {
               jsonFactory.responseBodyConverter(type, annotations, retrofit)
            }
         }
      }

      return null
   }

   @Nullable
   override fun requestBodyConverter(
      type: Type,
      parameterAnnotations: Array<out Annotation>,
      methodAnnotations: Array<out Annotation>,
      retrofit: Retrofit
   ): Converter<*, RequestBody>? {
      for (annotation in parameterAnnotations) {
         return when (annotation) {
            is Xml -> {
               xmlFactory.requestBodyConverter(
                  type, parameterAnnotations, methodAnnotations, retrofit
               )
            } else -> {
               jsonFactory.requestBodyConverter(
                  type, parameterAnnotations, methodAnnotations, retrofit
               )
            }
         }
      }

      return null
   }
}