package mil.nga.msi.serializer

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object LatLngSerializer : KSerializer<LatLng> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LatLng =
        decoder.decodeStructure(descriptor) {
            val lat = decodeDoubleElement(descriptor, 0)
            val lon = decodeDoubleElement(descriptor, 1)
            LatLng(lat, lon)
        }


    override fun serialize(encoder: Encoder, value: LatLng) {
        encoder.encodeStructure(descriptor) {
            encodeDoubleElement(descriptor, 0, value.latitude)
            encodeDoubleElement(descriptor, 1, value.longitude)
        }
    }
}