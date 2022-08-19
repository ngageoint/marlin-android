package mil.nga.msi.datasource.port

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
   var harborSize: String? = null

   @ColumnInfo(name = "harbor_type")
   var harborType: String? = null

   @ColumnInfo(name = "shelter")
   var shelter: String? = null

   @ColumnInfo(name = "entrance_restriction_tide")
   var entranceRestrictionTide: String? = null

   @ColumnInfo(name = "entrance_restriction_swell")
   var entranceRestrictionSwell: String? = null

   @ColumnInfo(name = "entrance_restriction_ice")
   var entranceRestrictionIce: String? = null

   @ColumnInfo(name = "entrance_restriction_other")
   var entranceRestrictionOther: String? = null

   @ColumnInfo(name = "overhead_limits")
   var overheadLimits: String? = null

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
   var goodHoldingGround: String? = null

   @ColumnInfo(name = "turning_area")
   var turningArea: String? = null

   @ColumnInfo(name = "first_port_of_entry")
   var firstPortOfEntry: String? = null

   @ColumnInfo(name = "us_representative")
   var usRepresentative: String? = null

   @ColumnInfo(name = "pilotage_compulsory")
   var pilotageCompulsory: String? = null

   @ColumnInfo(name = "pilotage_available")
   var pilotageAvailable: String? = null

   @ColumnInfo(name = "pilotage_local_assist")
   var pilotageLocalAssist: String? = null

   @ColumnInfo(name = "pilotage_advisable")
   var pilotageAdvisable: String? = null

   @ColumnInfo(name = "tugs_salvage")
   var tugsSalvage: String? = null

   @ColumnInfo(name = "tugs_assist")
   var tugsAssist: String? = null

   @ColumnInfo(name = "quarantine_pratique")
   var quarantinePratique: String? = null

   @ColumnInfo(name = "quarantine_other")
   var quarantineOther: String? = null

   @ColumnInfo(name = "communications_telephone")
   var communicationsTelephone: String? = null

   @ColumnInfo(name = "communications_telegraph")
   var communicationsTelegraph: String? = null

   @ColumnInfo(name = "communications_radio")
   var communicationsRadio: String? = null

   @ColumnInfo(name = "communications_radio_telephone")
   var communicationsRadioTelephone: String? = null

   @ColumnInfo(name = "communications_air")
   var communicationsAir: String? = null

   @ColumnInfo(name = "communications_rail")
   var communicationsRail: String? = null

   @ColumnInfo(name = "facilities_wharves")
   var facilitiesWharves: String? = null

   @ColumnInfo(name = "facilities_anchor")
   var facilitiesAnchor: String? = null

   @ColumnInfo(name = "facilities_med_moor")
   var facilitiesMedMoor: String? = null

   @ColumnInfo(name = "facilities_beach_moor")
   var facilitiesBeachMoor: String? = null

   @ColumnInfo(name = "facilities_ice_moor")
   var facilitiesIceMoor: String? = null

   @ColumnInfo(name = "medical_facilities")
   var medicalFacilities: String? = null

   @ColumnInfo(name = "garbage_disposal")
   var garbageDisposal: String? = null

   @ColumnInfo(name = "degauss")
   var degauss: String? = null

   @ColumnInfo(name = "dirty_ballast")
   var dirtyBallast: String? = null

   @ColumnInfo(name = "cranes_fixed")
   var cranesFixed: String? = null

   @ColumnInfo(name = "cranes_mobile")
   var cranesMobile: String? = null

   @ColumnInfo(name = "cranes_floating")
   var cranesFloating: String? = null

   @ColumnInfo(name = "lifts_100")
   var lifts100: String? = null

   @ColumnInfo(name = "lifts_50")
   var lifts50: String? = null

   @ColumnInfo(name = "lifts_25")
   var lifts25: String? = null

   @ColumnInfo(name = "lifts_0")
   var lifts0: String? = null

   @ColumnInfo(name = "services_longshore")
   var servicesLongshore: String? = null

   @ColumnInfo(name = "services_electrical")
   var servicesElectrical: String? = null

   @ColumnInfo(name = "services_steam")
   var servicesSteam: String? = null

   @ColumnInfo(name = "services_navigational_equipment")
   var servicesNavigationalEquipment: String? = null

   @ColumnInfo(name = "services_electrical_repair")
   var servicesElectricalRepair: String? = null

   @ColumnInfo(name = "supplies_provisions")
   var suppliesProvisions: String? = null

   @ColumnInfo(name = "supplies_water")
   var suppliesWater: String? = null

   @ColumnInfo(name = "supplies_fuel")
   var suppliesFuel: String? = null

   @ColumnInfo(name = "supplies_diesel")
   var suppliesDiesel: String? = null

   @ColumnInfo(name = "supplies_deck")
   var suppliesDeck: String? = null

   @ColumnInfo(name = "supplies_engine")
   var suppliesEngine: String? = null

   @ColumnInfo(name = "repair_code")
   var repairCode: String? = null

   @ColumnInfo(name = "drydock")
   var drydock: String? = null

   @ColumnInfo(name = "railway")
   var railway: String? = null

   @ColumnInfo(name = "quarantine_sanitation")
   var quarantineSanitation: String? = null

   @ColumnInfo(name = "supplies_aviation_fuel")
   var suppliesAviationFuel: String? = null

   @ColumnInfo(name = "harbor_use")
   var harborUse: String? = null

   @ColumnInfo(name = "ukc_management_system")
   var ukcManagementSystem: String? = null

   @ColumnInfo(name = "port_security")
   var portSecurity: String? = null

   @ColumnInfo(name = "eta_message")
   var etaMessage: String? = null

   @ColumnInfo(name = "search_and_rescue")
   var searchAndRescue: String? = null

   @ColumnInfo(name = "traffic_separation_scheme")
   var trafficSeparationScheme: String? = null

   @ColumnInfo(name = "vessel_traffic_service")
   var vesselTrafficService: String? = null

   @ColumnInfo(name = "chemical_holding_tank_disposal")
   var chemicalHoldingTankDisposal: String? = null

   @ColumnInfo(name = "global_id")
   var globalId: String? = null

   @ColumnInfo(name = "facilities_roro")
   var facilitiesRoro: String? = null

   @ColumnInfo(name = "facilities_solid_bulk")
   var facilitiesSolidBulk: String? = null

   @ColumnInfo(name = "facilities_container")
   var facilitiesContainer: String? = null

   @ColumnInfo(name = "facilities_break_bulk")
   var facilitiesBreakBulk: String? = null

   @ColumnInfo(name = "facilities_oil_terminal")
   var facilitiesOilTerminal: String? = null

   @ColumnInfo(name = "facilities_long_terminal")
   var facilitiesLongTerminal: String? = null

   @ColumnInfo(name = "facilities_other")
   var facilitiesOther: String? = null

   @ColumnInfo(name = "facilities_dangerous_cargo")
   var facilitiesDangerousCargo: String? = null

   @ColumnInfo(name = "facilities_liquid_bulk")
   var facilitiesLiquidBulk: String? = null

   @ColumnInfo(name = "services_ice_breaking")
   var servicesIceBreaking: String? = null

   @ColumnInfo(name = "services_diving")
   var servicesDiving: String? = null

   @ColumnInfo(name = "cranes_container")
   var cranesContainer: String? = null

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
   var liquifiedNaturalGasTerminalDepth: String? = null

   @ColumnInfo(name = "offshore_max_vessel_length")
   var offshoreMaxVesselLength: Int? = null

   @ColumnInfo(name = "offshore_max_vessel_beam")
   var offshoreMaxVesselBeam: Int? = null

   @ColumnInfo(name = "offshore_max_vessel_draft")
   var offshoreMaxVesselDraft: Int? = null

//   override fun toString(): String {
//      val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
//      return "ASAM\n\n" +
//              "Reference: $reference\n" +
//              "Date: ${dateFormat.format(date)}\n" +
//              "Latitude: $latitude\n" +
//              "Longitude: $longitude\n" +
//              "Navigate Area: $navigationArea\n" +
//              "Subregion: $subregion\n" +
//              "Description: $description\n" +
//              "Hostility: $hostility\n" +
//              "Victim: $victim\n"
//   }

   companion object {

   }
}