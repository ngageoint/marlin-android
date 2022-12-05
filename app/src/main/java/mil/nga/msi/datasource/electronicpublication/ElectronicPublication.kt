package mil.nga.msi.datasource.electronicpublication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

enum class ElectronicPublicationType(
    val typeCode: Int,
    val label: String,
) {
    ListOfLights(9, "List of Lights"),
    AtlasOfPilotCharts(30, "Atlas of Pilot Charts"),
    ChartNo1(3, "Chart No. 1"),
    AmericanPracticalNavigator(2, "American Practical Navigator"),
    RadioNavigationAids(11, "Radio Navigation Aids"),
    RadarNavigationAndManeuveringBoardManual(10, "Radar Navigation and Maneuvering Board Manual"),
    SightReductionTablesForAirNavigation(13, "Sight Reduction Tables for Air Navigation"),
    WorldPortIndex(17, "World Port Index"),
    SightReductionTablesForMarineNavigation(14, "Sight Reduction Tables for Marine Navigation"),
    SailingDirectionsEnroute(22, "Sailing Directions Enroute"),
    UscgLightList(16, "USCG Light List"),
    SailingDirectionsPlanningGuides(21, "Sailing Directions Planning Guides"),
    InternationalCodeOfSignals(7, "International Code of Signals"),
    NoticeToMarinersAndCorrections(15, "Notice To Mariners and Corrections"),
    DistanceBetweenPorts(5, "Distances Between Ports"),
    FleetGuides(6, "Fleet Guides"),
    NoaaTidalCurrentTables(27, "NOAA Tidal Current Tables"),
    Random(40, "Random"),
    TideTables(26, "Tide Tables"),
    Unknown(-1, "Electronic Publications");

    override fun toString(): String {
        return this.label
    }

    companion object {
        val typeCodeToType = buildMap {
            putAll(values().map { x -> x.typeCode to x })
        }
        fun fromTypeCode(x: Int?): ElectronicPublicationType = typeCodeToType[x ?: Unknown.typeCode] ?: Unknown
    }
}

@Entity(tableName = "epubs")
data class ElectronicPublication(
    @PrimaryKey
    @ColumnInfo(name = "s3_key")
    val s3Key: String,
    @ColumnInfo(name = "content_id")
    val contentId: Int? = null,
    @ColumnInfo(name = "downloaded_bytes")
    val downloadedBytes: Int = 0,
    @ColumnInfo(name = "file_ext")
    val fileExtension: String? = null,
    @ColumnInfo(name = "file_name_base")
    val filenameBase: String? = null,
    @ColumnInfo(name = "file_size")
    val fileSize: Int? = null,
    @ColumnInfo(name = "full_filename")
    val fullFilename: String? = null,
    @ColumnInfo(name = "full_pub_flag")
    val fullPubFlag: Boolean? = null,
    @ColumnInfo(name = "internal_path")
    val internalPath: String? = null,
    @ColumnInfo(name = "is_downloaded")
    val isDownloaded: Boolean = false,
    @ColumnInfo(name = "is_downloading")
    val isDownloading: Boolean = false,
    @ColumnInfo(name = "ods_entry_id")
    val odsEntryId: Int? = null,
    @ColumnInfo(name = "pub_download_display_name")
    val pubDownloadDisplayName: String? = null,
    @ColumnInfo(name = "pub_download_id")
    val pubDownloadId: Int? = null,
    @ColumnInfo(name = "pub_download_order")
    val pubDownloadOrder: Int? = null,
    @ColumnInfo(name = "pubsec_id")
    val pubsecId: Int? = null,
    @ColumnInfo(name = "pubsec_last_modified")
    val pubsecLastModified: Instant? = null,
    /**
     * This does not use a `TypeConverter` with the enum type so that if the server returns a type
     * code the enum does not have, the app will not overwrite the type code of the record to
     * `Unknown`.  Unaccounted type codes can be added to the enum in future releases.
     */
    @ColumnInfo(name = "pub_type_id")
    val pubTypeId: Int = ElectronicPublicationType.Unknown.typeCode,
    @ColumnInfo(name = "section_display_name")
    val sectionDisplayName: String? = null,
    @ColumnInfo(name = "section_last_modified")
    val sectionLastModified: Instant? = null,
    @ColumnInfo(name = "section_name")
    val sectionName: String? = null,
    @ColumnInfo(name = "section_order")
    val sectionOrder: Int? = null,
    @ColumnInfo(name = "upload_time")
    val uploadTime: Instant? = null,
) {

    @Transient
    val pubType: ElectronicPublicationType = ElectronicPublicationType.fromTypeCode(pubTypeId)

    override fun equals(other: Any?): Boolean {
        return super.equals(other) ||
                (other is ElectronicPublication && other.s3Key == s3Key)
    }

    override fun hashCode(): Int {
        return s3Key.hashCode()
    }

    override fun toString(): String {
        return "${this::class.simpleName}:$s3Key"
    }
}

data class ElectronicPublicationListItem(
    @ColumnInfo(name = "s3_key")
    val s3Key: String,
    @ColumnInfo(name = "pub_type_id")
    val pubTypeId: Int = ElectronicPublicationType.Unknown.typeCode,
    @ColumnInfo(name = "full_filename")
    val fullFilename: String?,
) {
    @Transient
    val pubType: ElectronicPublicationType = ElectronicPublicationType.fromTypeCode(pubTypeId)
}