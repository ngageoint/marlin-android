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
        fun fromTypeCode(x: Int): ElectronicPublicationType = typeCodeToType[x] ?: Unknown
    }
}

@Entity(tableName = "epubs")
data class ElectronicPublication(
    @PrimaryKey
    @ColumnInfo(name = "s3_key")
    val s3Key: String
) {
    @ColumnInfo(name = "content_id")
    var contentId: Int? = null
        private set
    @ColumnInfo(name = "downloaded_bytes")
    var downloadedBytes: Int = 0
        private set
    @ColumnInfo(name = "file_ext")
    var fileExtension: String? = null
        private set
    @ColumnInfo(name = "file_name_base")
    var filenameBase: String? = null
        private set
    @ColumnInfo(name = "file_size")
    var fileSize: Int? = null
        private set
    @ColumnInfo(name = "full_filename")
    var fullFilename: String? = null
        private set
    @ColumnInfo(name = "full_pub_flag")
    var fullPubFlag: Boolean? = null
        private set
    @ColumnInfo(name = "internal_path")
    var internalPath: String? = null
        private set
    @ColumnInfo(name = "is_downloaded")
    var isDownloaded: Boolean = false
        private set
    @ColumnInfo(name = "is_downloading")
    var isDownloading: Boolean = false
        private set
    @ColumnInfo(name = "ods_entry_id")
    var odsEntryId: Int? = null
        private set
    @ColumnInfo(name = "pub_download_display_name")
    var pubDownloadDisplayName: String? = null
        private set
    @ColumnInfo(name = "pub_download_id")
    var pubDownloadId: Int? = null
        private set
    @ColumnInfo(name = "pub_download_order")
    var pubDownloadOrder: Int? = null
        private set
    @ColumnInfo(name = "pubsec_id")
    var pubsecId: Int? = null
        private set
    @ColumnInfo(name = "pubsec_last_modified")
    var pubsecLastModified: Instant? = null
        private set
    /**
     * This does not use a `TypeConverter` with the enum type so that if the server returns a type
     * code the enum does not have, the app will not overwrite the type code of the record to
     * `Unknown`.  Unaccounted type codes can be added to the enum in future releases.
     */
    @ColumnInfo(name = "pub_type_id")
    var pubTypeId: Int = ElectronicPublicationType.Unknown.typeCode
        private set
    @ColumnInfo(name = "section_display_name")
    var sectionDisplayName: String? = null
        private set
    @ColumnInfo(name = "section_last_modified")
    var sectionLastModified: Instant? = null
        private set
    @ColumnInfo(name = "section_name")
    var sectionName: String? = null
        private set
    @ColumnInfo(name = "section_order")
    var sectionOrder: Int? = null
        private set
    @ColumnInfo(name = "upload_time")
    var uploadTime: Instant? = null
        private set

    constructor(
        s3Key: String,
        contentId: Int? = null,
        downloadedBytes: Int = 0,
        fileExtension: String? = null,
        filenameBase: String? = null,
        fileSize: Int? = null,
        fullFilename: String? = null,
        fullPubFlag: Boolean? = null,
        internalPath: String? = null,
        isDownloaded: Boolean = false,
        isDownloading: Boolean = false,
        odsEntryId: Int? = null,
        pubDownloadDisplayName: String? = null,
        pubDownloadId: Int? = null,
        pubDownloadOrder: Int? = null,
        pubsecId: Int? = null,
        pubsecLastModified: Instant? = null,
        pubTypeId: Int = ElectronicPublicationType.Unknown.typeCode,
        sectionDisplayName: String? = null,
        sectionLastModified: Instant? = null,
        sectionName: String? = null,
        sectionOrder: Int? = null,
        uploadTime: Instant? = null
    ) : this(s3Key) {
        this.contentId = contentId
        this.downloadedBytes = downloadedBytes
        this.fileExtension = fileExtension
        this.filenameBase = filenameBase
        this.fileSize = fileSize
        this.fullFilename = fullFilename
        this.fullPubFlag = fullPubFlag
        this.internalPath = internalPath
        this.isDownloaded = isDownloaded
        this.isDownloading = isDownloading
        this.odsEntryId = odsEntryId
        this.pubDownloadDisplayName = pubDownloadDisplayName
        this.pubDownloadId = pubDownloadId
        this.pubDownloadOrder = pubDownloadOrder
        this.pubsecId = pubsecId
        this.pubsecLastModified = pubsecLastModified
        this.pubTypeId = pubTypeId
        this.sectionDisplayName = sectionDisplayName
        this.sectionLastModified = sectionLastModified
        this.sectionName = sectionName
        this.sectionOrder = sectionOrder
        this.uploadTime = uploadTime
    }

    constructor(
        from: ElectronicPublication,
        contentId: Int? = from.contentId,
        downloadedBytes: Int = from.downloadedBytes,
        fileExtension: String? = from.fileExtension,
        filenameBase: String? = from.filenameBase,
        fileSize: Int? = from.fileSize,
        fullFilename: String? = from.fullFilename,
        fullPubFlag: Boolean? = from.fullPubFlag,
        internalPath: String? = from.internalPath,
        isDownloaded: Boolean = from.isDownloaded,
        isDownloading: Boolean = from.isDownloading,
        odsEntryId: Int? = from.odsEntryId,
        pubDownloadDisplayName: String? = from.pubDownloadDisplayName,
        pubDownloadId: Int? = from.pubDownloadId,
        pubDownloadOrder: Int? = from.pubDownloadOrder,
        pubsecId: Int? = from.pubsecId,
        pubsecLastModified: Instant? = from.pubsecLastModified,
        pubTypeId: Int = from.pubTypeId,
        sectionDisplayName: String? = from.sectionDisplayName,
        sectionLastModified: Instant? = from.sectionLastModified,
        sectionName: String? = from.sectionName,
        sectionOrder: Int? = from.sectionOrder,
        uploadTime: Instant? = from.uploadTime,
    ) : this(
        s3Key = from.s3Key,
        contentId = contentId,
        downloadedBytes = downloadedBytes,
        fileExtension = fileExtension,
        filenameBase = filenameBase,
        fileSize = fileSize,
        fullFilename = fullFilename,
        fullPubFlag = fullPubFlag,
        internalPath = internalPath,
        isDownloaded = isDownloaded,
        isDownloading = isDownloading,
        odsEntryId = odsEntryId,
        pubDownloadDisplayName = pubDownloadDisplayName,
        pubDownloadId = pubDownloadId,
        pubDownloadOrder = pubDownloadOrder,
        pubsecId = pubsecId,
        pubsecLastModified = pubsecLastModified,
        pubTypeId = pubTypeId,
        sectionDisplayName = sectionDisplayName,
        sectionLastModified = sectionLastModified,
        sectionName = sectionName,
        sectionOrder = sectionOrder,
        uploadTime = uploadTime
    )

    @Transient
    val pubType: ElectronicPublicationType = ElectronicPublicationType.fromTypeCode(pubTypeId)
}

data class ElectronicPublicationListItem(
    @ColumnInfo(name = "s3_key")
    val s3Key: String,
    @ColumnInfo(name = "pub_type_id")
    val pubTypeId: Int,
    @ColumnInfo(name = "full_filename")
    val fullFilename: String?,
    @Transient
    val pubType: ElectronicPublicationType = ElectronicPublicationType.fromTypeCode(pubTypeId),
)