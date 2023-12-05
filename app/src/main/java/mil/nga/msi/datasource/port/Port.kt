package mil.nga.msi.datasource.port

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.port.types.Decision
import mil.nga.msi.datasource.port.types.HarborType
import mil.nga.msi.datasource.port.types.HarborUse
import mil.nga.msi.datasource.port.types.RepairCode
import mil.nga.msi.datasource.port.types.Shelter
import mil.nga.msi.datasource.port.types.Size
import mil.nga.msi.datasource.port.types.UnderkeelClearance
import mil.nga.msi.ui.port.detail.asNonZeroOrNull

@Serializable
@Entity(tableName = "ports")
data class Port(
   @PrimaryKey
   @ColumnInfo(name = "port_number")
   val portNumber: Int,

   @ColumnInfo(name = "port_name")
   val portName: String,

   @ColumnInfo(name = "latitude")
   val latitude: Double,

   @ColumnInfo(name = "longitude")
   val longitude: Double
) {
   @ColumnInfo(name = "region_number")
   var regionNumber: Int? = null

   @ColumnInfo(name = "region_name")
   var regionName: String? = null

   @ColumnInfo(name = "country_code")
   var countryCode: String? = null

   @ColumnInfo(name = "country_name")
   var countryName: String? = null

   @ColumnInfo(name = "publication_number")
   var publicationNumber: String? = null

   @ColumnInfo(name = "chart_number")
   var chartNumber: String? = null

   @ColumnInfo(name = "navigation_area")
   var navigationArea: String? = null

   @ColumnInfo(name = "harbor_size")
   var harborSize: Size? = null

   @ColumnInfo(name = "harbor_type")
   var harborType: HarborType? = null

   @ColumnInfo(name = "shelter")
   var shelter: Shelter? = null

   @ColumnInfo(name = "entrance_restriction_tide")
   var entranceRestrictionTide: Decision? = null

   @ColumnInfo(name = "entrance_restriction_swell")
   var entranceRestrictionSwell: Decision? = null

   @ColumnInfo(name = "entrance_restriction_ice")
   var entranceRestrictionIce: Decision? = null

   @ColumnInfo(name = "entrance_restriction_other")
   var entranceRestrictionOther: Decision? = null

   @ColumnInfo(name = "overhead_limits")
   var overheadLimits: Decision? = null

   @ColumnInfo(name = "channel_depth")
   var channelDepth: Int? = null

   @ColumnInfo(name = "anchorage_depth")
   var anchorageDepth: Int? = null

   @ColumnInfo(name = "cargo_pier_depth")
   var cargoPierDepth: Int? = null

   @ColumnInfo(name = "oil_terminal_depth")
   var oilTerminalDepth: Int? = null

   @ColumnInfo(name = "tide")
   var tide: Int? = null

   @ColumnInfo(name = "max_vessel_length")
   var maxVesselLength: Int? = null

   @ColumnInfo(name = "max_vessel_beam")
   var maxVesselBeam: Int? = null

   @ColumnInfo(name = "max_vessel_draft")
   var maxVesselDraft: Int? = null

   @ColumnInfo(name = "good_holding_ground")
   var goodHoldingGround: Decision? = null

   @ColumnInfo(name = "turning_area")
   var turningArea: Decision? = null

   @ColumnInfo(name = "first_port_of_entry")
   var firstPortOfEntry: Decision? = null

   @ColumnInfo(name = "us_representative")
   var usRepresentative: String? = null

   @ColumnInfo(name = "pilotage_compulsory")
   var pilotageCompulsory: Decision? = null

   @ColumnInfo(name = "pilotage_available")
   var pilotageAvailable: Decision? = null

   @ColumnInfo(name = "pilotage_local_assist")
   var pilotageLocalAssist: Decision? = null

   @ColumnInfo(name = "pilotage_advisable")
   var pilotageAdvisable: Decision? = null

   @ColumnInfo(name = "tugs_salvage")
   var tugsSalvage: Decision? = null

   @ColumnInfo(name = "tugs_assist")
   var tugsAssist: Decision? = null

   @ColumnInfo(name = "quarantine_pratique")
   var quarantinePratique: Decision? = null

   @ColumnInfo(name = "quarantine_other")
   var quarantineOther: Decision? = null

   @ColumnInfo(name = "communications_telephone")
   var communicationsTelephone: Decision? = null

   @ColumnInfo(name = "communications_telegraph")
   var communicationsTelegraph: Decision? = null

   @ColumnInfo(name = "communications_radio")
   var communicationsRadio: Decision? = null

   @ColumnInfo(name = "communications_radio_telephone")
   var communicationsRadioTelephone: Decision? = null

   @ColumnInfo(name = "communications_air")
   var communicationsAir: Decision? = null

   @ColumnInfo(name = "communications_rail")
   var communicationsRail: Decision? = null

   @ColumnInfo(name = "medical_facilities")
   var medicalFacilities: Decision? = null

   @ColumnInfo(name = "garbage_disposal")
   var garbageDisposal: Decision? = null

   @ColumnInfo(name = "degauss")
   var degauss: Decision? = null

   @ColumnInfo(name = "dirty_ballast")
   var dirtyBallast: Decision? = null

   @ColumnInfo(name = "cranes_fixed")
   var cranesFixed: Decision? = null

   @ColumnInfo(name = "cranes_mobile")
   var cranesMobile: Decision? = null

   @ColumnInfo(name = "cranes_floating")
   var cranesFloating: Decision? = null

   @ColumnInfo(name = "cranes_container")
   var cranesContainer: Decision? = null

   @ColumnInfo(name = "lifts_100")
   var lifts100: Decision? = null

   @ColumnInfo(name = "lifts_50")
   var lifts50: Decision? = null

   @ColumnInfo(name = "lifts_25")
   var lifts25: Decision? = null

   @ColumnInfo(name = "lifts_0")
   var lifts0: Decision? = null

   @ColumnInfo(name = "services_longshore")
   var servicesLongshore: Decision? = null

   @ColumnInfo(name = "services_electrical")
   var servicesElectrical: Decision? = null

   @ColumnInfo(name = "services_steam")
   var servicesSteam: Decision? = null

   @ColumnInfo(name = "services_navigational_equipment")
   var servicesNavigationalEquipment: Decision? = null

   @ColumnInfo(name = "services_electrical_repair")
   var servicesElectricalRepair: Decision? = null

   @ColumnInfo(name = "services_ice_breaking")
   var servicesIceBreaking: Decision? = null

   @ColumnInfo(name = "services_diving")
   var servicesDiving: Decision? = null

   @ColumnInfo(name = "supplies_provisions")
   var suppliesProvisions: Decision? = null

   @ColumnInfo(name = "supplies_water")
   var suppliesWater: Decision? = null

   @ColumnInfo(name = "supplies_fuel")
   var suppliesFuel: Decision? = null

   @ColumnInfo(name = "supplies_diesel")
   var suppliesDiesel: Decision? = null

   @ColumnInfo(name = "supplies_deck")
   var suppliesDeck: Decision? = null

   @ColumnInfo(name = "supplies_engine")
   var suppliesEngine: Decision? = null

   @ColumnInfo(name = "supplies_aviation_fuel")
   var suppliesAviationFuel: Decision? = null

   @ColumnInfo(name = "repair_code")
   var repairCode: RepairCode? = null

   @ColumnInfo(name = "dry_dock")
   var dryDock: Size? = null

   @ColumnInfo(name = "railway")
   var railway: Size? = null

   @ColumnInfo(name = "quarantine_sanitation")
   var quarantineSanitation: Decision? = null

   @ColumnInfo(name = "harbor_use")
   var harborUse: HarborUse? = null

   @ColumnInfo(name = "ukc_management_system")
   var ukcManagementSystem: UnderkeelClearance? = null

   @ColumnInfo(name = "port_security")
   var portSecurity: Decision? = null

   @ColumnInfo(name = "eta_message")
   var etaMessage: Decision? = null

   @ColumnInfo(name = "search_and_rescue")
   var searchAndRescue: Decision? = null

   @ColumnInfo(name = "traffic_separation_scheme")
   var trafficSeparationScheme: Decision? = null

   @ColumnInfo(name = "vessel_traffic_service")
   var vesselTrafficService: Decision? = null

   @ColumnInfo(name = "chemical_holding_tank_disposal")
   var chemicalHoldingTankDisposal: Decision? = null

   @ColumnInfo(name = "global_id")
   var globalId: String? = null

   @ColumnInfo(name = "facilities_roro")
   var facilitiesRoro: Decision? = null

   @ColumnInfo(name = "facilities_solid_bulk")
   var facilitiesSolidBulk: Decision? = null

   @ColumnInfo(name = "facilities_container")
   var facilitiesContainer: Decision? = null

   @ColumnInfo(name = "facilities_break_bulk")
   var facilitiesBreakBulk: Decision? = null

   @ColumnInfo(name = "facilities_oil_terminal")
   var facilitiesOilTerminal: Decision? = null

   @ColumnInfo(name = "facilities_long_terminal")
   var facilitiesLongTerminal: Decision? = null

   @ColumnInfo(name = "facilities_other")
   var facilitiesOther: Decision? = null

   @ColumnInfo(name = "facilities_dangerous_cargo")
   var facilitiesDangerousCargo: Decision? = null

   @ColumnInfo(name = "facilities_liquid_bulk")
   var facilitiesLiquidBulk: Decision? = null

   @ColumnInfo(name = "facilities_wharves")
   var facilitiesWharves: Decision? = null

   @ColumnInfo(name = "facilities_anchor")
   var facilitiesAnchor: Decision? = null

   @ColumnInfo(name = "facilities_med_moor")
   var facilitiesMedMoor: Decision? = null

   @ColumnInfo(name = "facilities_beach_moor")
   var facilitiesBeachMoor: Decision? = null

   @ColumnInfo(name = "facilities_ice_moor")
   var facilitiesIceMoor: Decision? = null

   @ColumnInfo(name = "unlo_code")
   var unloCode: String? = null

   @ColumnInfo(name = "dnc")
   var dnc: String? = null

   @ColumnInfo(name = "s_121_water_body")
   var s121WaterBody: String? = null

   @ColumnInfo(name = "s_57_enc")
   var s57Enc: String? = null

   @ColumnInfo(name = "s_101_enc")
   var s101Enc: String? = null

   @ColumnInfo(name = "dod_water_body")
   var dodWaterBody: String? = null

   @ColumnInfo(name = "alternate_name")
   var alternateName: String? = null

   @ColumnInfo(name = "entrance_width")
   var entranceWidth: Int? = null

   @ColumnInfo(name = "liquified_natural_gas_terminal_depth")
   var liquifiedNaturalGasTerminalDepth: Int? = null

   @ColumnInfo(name = "offshore_max_vessel_length")
   var offshoreMaxVesselLength: Int? = null

   @ColumnInfo(name = "offshore_max_vessel_beam")
   var offshoreMaxVesselBeam: Int? = null

   @ColumnInfo(name = "offshore_max_vessel_draft")
   var offshoreMaxVesselDraft: Int? = null

   @kotlinx.serialization.Transient
   @Transient
   val latLng = LatLng(latitude, longitude)

   fun nameSection() = mapOf(
      "World Port Index Number" to portNumber.toString(),
      "Region Name" to "${regionName.orEmpty()} - ${regionNumber?.toString().orEmpty()}",
      "Main Port Name" to portName,
      "Alternate Port Name" to alternateName,
      "UN/LOCODE" to unloCode,
      "Country" to countryName,
      "World Water Body" to dodWaterBody,
      "Sailing Directions or Publication" to publicationNumber,
      "Standard Nautical Chart" to chartNumber,
      "IHO S-57 Electronic Navigational Chart" to s57Enc,
      "IHO S-101 Electronic Navigational Chart" to s101Enc,
      "Digital Nautical Chart" to dnc
   )

   fun depthSection() = mapOf(
      "Tidal Range (m)" to tide?.asNonZeroOrNull()?.toString(),
      "Entrance Width (m)" to entranceWidth?.asNonZeroOrNull()?.toString(),
      "Channel Depth (m)" to channelDepth?.asNonZeroOrNull()?.toString(),
      "Anchorage Depth (m)" to anchorageDepth?.asNonZeroOrNull()?.toString(),
      "Cargo Pier Depth (m)" to cargoPierDepth?.asNonZeroOrNull()?.toString(),
      "Oil Terminal Depth (m)" to oilTerminalDepth?.asNonZeroOrNull()?.toString(),
      "Liquefied Natural Gas Terminal Depth (m)" to liquifiedNaturalGasTerminalDepth?.asNonZeroOrNull()?.toString()
   )

   fun vesselSection() = mapOf(
      "Maximum Vessel Length (m)" to maxVesselLength?.asNonZeroOrNull()?.toString(),
      "Maximum Vessel Beam (m)" to maxVesselBeam?.asNonZeroOrNull()?.toString(),
      "Maximum Vessel Draft (m)" to maxVesselDraft?.asNonZeroOrNull()?.toString(),
      "Offshore Maximum Vessel Length (m)" to offshoreMaxVesselLength?.asNonZeroOrNull()?.toString(),
      "Offshore Maximum Vessel Beam (m)" to offshoreMaxVesselBeam?.asNonZeroOrNull()?.toString(),
      "Offshore Maximum Vessel Draft (m)" to offshoreMaxVesselDraft?.asNonZeroOrNull()?.toString()
   )

   fun environmentSection() = mapOf(
      "Harbor Size" to harborSize?.title,
      "Harbor Type" to harborType?.title,
      "Harbor Use" to harborUse?.title,
      "Shelter" to shelter?.title,
      "Entrance Restriction - Tide" to entranceRestrictionTide?.title,
      "Entrance Restriction - Heavy Swell" to entranceRestrictionSwell?.title,
      "Entrance Restriction - Ice" to entranceRestrictionIce?.title,
      "Entrance Restriction - Other" to entranceRestrictionOther?.title,
      "Overhead Limits" to overheadLimits?.title,
      "Overhead Limits" to overheadLimits?.title,
      "Underkeel Clearance Management System" to ukcManagementSystem?.title,
      "Good Holding Ground" to goodHoldingGround?.title,
      "Turning Area" to turningArea?.title
   )

   fun approachSection() = mapOf(
      "Port Security" to portSecurity?.title,
      "Estimated Time Of Arrival Message" to etaMessage?.title,
      "Quarantine - Pratique" to quarantinePratique?.title,
      "Quarantine - Sanitation" to quarantineSanitation?.title,
      "Quarantine - Other" to quarantineOther?.title,
      "Traffic Separation Scheme" to trafficSeparationScheme?.title,
      "Vessel Traffic Service" to vesselTrafficService?.title,
      "First Port Of Entry" to firstPortOfEntry?.title
   )

   fun pilotSection() = mapOf(
      "Pilotage - Compulsory" to pilotageCompulsory?.title,
      "Pilotage - Available" to pilotageAvailable?.title,
      "Pilotage - Local Assistance" to pilotageLocalAssist?.title,
      "Pilotage - Advisable" to pilotageAdvisable?.title,
      "Tugs - Salvage" to tugsSalvage?.title,
      "Tugs - Assistance" to tugsAssist?.title,
      "Communications - Telephone" to communicationsTelephone?.title,
      "Communications - Telefax" to communicationsTelegraph?.title,
      "Communications - Radio" to communicationsRadio?.title,
      "Communications - Radiotelephone" to communicationsRadioTelephone?.title,
      "Communications - Airport" to communicationsAir?.title,
      "Communications - Rail" to communicationsRail?.title,
      "Search and Rescue" to searchAndRescue?.title,
      "Navigation Area" to navigationArea
   )

   fun facilitySection() = mapOf(
      "Facilities - Wharves" to facilitiesWharves?.title,
      "Facilities - Anchorage" to facilitiesAnchor?.title,
      "Facilities - Dangerous Cargo Anchorage" to facilitiesDangerousCargo?.title,
      "Facilities - Med Mooring" to facilitiesMedMoor?.title,
      "Facilities - Beach Mooring" to facilitiesBeachMoor?.title,
      "Facilities - Ice Mooring" to facilitiesIceMoor?.title,
      "Facilities - RoRo" to facilitiesRoro?.title,
      "Facilities - Solid Bulk" to facilitiesSolidBulk?.title,
      "Facilities - Liquid Bulk" to facilitiesLiquidBulk?.title,
      "Facilities - Container" to facilitiesContainer?.title,
      "Facilities - Breakbulk" to facilitiesBreakBulk?.title,
      "Facilities - Oil Terminal" to facilitiesOilTerminal?.title,
      "Facilities - LNG Terminal" to facilitiesLongTerminal?.title,
      "Facilities - Other" to facilitiesOther?.title,
      "Medical Facilities" to medicalFacilities?.title,
      "Garbage Disposal" to garbageDisposal?.title,
      "Chemical Holding Tank Disposal" to chemicalHoldingTankDisposal?.title,
      "Degaussing" to degauss?.title,
      "Dirty Ballast Disposal" to dirtyBallast?.title
   )

   fun craneSection() = mapOf(
      "Cranes - Fixed" to cranesFixed?.title,
      "Cranes - Mobile" to cranesMobile?.title,
      "Cranes - Floating" to cranesFloating?.title,
      "Cranes - Container" to cranesContainer?.title,
      "Lifts - 100+ Tons" to lifts100?.title,
      "Lifts - 50-100 Tons" to lifts50?.title,
      "Lifts - 25-49 Tons" to lifts25?.title,
      "Lifts - 0-24 Tons" to lifts0?.title
   )

   fun serviceSection() = mapOf(
      "Services - Longshoremen" to servicesLongshore?.title,
      "Services - Electricity" to servicesElectrical?.title,
      "Services - Steam" to servicesSteam?.title,
      "Services - Navigational Equipment" to servicesNavigationalEquipment?.title,
      "Services - Electrical Repair" to servicesElectricalRepair?.title,
      "Services - Ice Breaking" to servicesIceBreaking?.title,
      "Services - Diving" to servicesDiving?.title,
      "Supplies - Provisions" to suppliesProvisions?.title,
      "Supplies - Potable Water" to suppliesWater?.title,
      "Supplies - Fuel Oil" to suppliesFuel?.title,
      "Supplies - Diesel Oil" to suppliesDiesel?.title,
      "Supplies - Aviation Fuel" to suppliesAviationFuel?.title,
      "Supplies - Deck" to suppliesDeck?.title,
      "Supplies - Engine" to suppliesEngine?.title,
      "Repair Code" to repairCode?.title,
      "Dry Dock" to dryDock?.title,
      "Railway" to railway?.title
   )

   override fun toString(): String {
      var port = "Port\n\n" +
              "Latitude: $latitude\n" +
              "Longitude: $longitude\n\n"

      sectionDescription(
         "Name and Location",
         nameSection()
      )?.let { port += "${it}\n\n" }

      sectionDescription(
         "Depths",
         depthSection()
      )?.let { port += "${it}\n\n" }

      sectionDescription(
         "Maximum Vessel Size",
         vesselSection()
      )?.let { port += "${it}\n\n" }

      sectionDescription(
         "Physical Environment",
         environmentSection()
      )?.let { port += "${it}\n\n" }

      sectionDescription(
         "Approach",
         approachSection()
      )?.let { port += "${it}\n\n" }

      sectionDescription(
         "Pilots, Tugs, Communications",
         pilotSection()
      )?.let { port += "${it}\n\n" }

      sectionDescription(
         "Facilities",
         facilitySection()
      )?.let { port += "${it}\n\n" }

      sectionDescription(
         "Cranes",
         craneSection()
      )?.let { port += "${it}\n\n" }

      sectionDescription(
         "Services and Supplies",
         serviceSection()
      )?.let { port += "${it}\n\n" }

      return port
   }

   private fun sectionDescription(
      title: String,
      sections: Map<String, String?>
   ): String? {
      var section: String? = null
      if (sections.any { entry -> entry.value?.isNotEmpty() == true }) {
         section = "${title}:\n"
         sections.forEach { entry ->
            if (entry.value?.isNotBlank() == true) {
               section += "${entry.key}: ${entry.value}\n"
            }
         }
      }

      return section
   }
}