package mil.nga.msi.ui.port.list

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.liveData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.filter.Filter
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.sort.Sort
import mil.nga.msi.sort.SortParameter
import javax.inject.Inject

sealed class PortListItem {
   class PortItem(val port: Port) : PortListItem()
   class HeaderItem(val header: String) : PortListItem()
}

@HiltViewModel
class PortsViewModel @Inject constructor(
   locationPolicy: LocationPolicy,
   filterRepository: FilterRepository,
   sortRepository: SortRepository,
   private val portRepository: PortRepository
): ViewModel() {
   val locationProvider = locationPolicy.bestLocationProvider

   private val queryParameters = MediatorLiveData<Pair<List<Filter>, Sort?>>().apply {
      addSource(filterRepository.filters.asLiveData()) { entry ->
         val filters = entry[DataSource.PORT] ?: emptyList()
         value = Pair(filters, value?.second)
      }

      addSource(sortRepository.sort.asLiveData()) { entry ->
         val filters = value?.first ?: emptyList()
         value = Pair(filters, entry[DataSource.PORT])
      }
   }

   val ports: Flow<PagingData<PortListItem>> = queryParameters.switchMap { pair ->
      val filters = pair.first
      val sort = pair.second
      Pager(PagingConfig(pageSize = 20), null) {
         portRepository.observePortListItems(filters, sort?.parameters ?: emptyList())
      }.liveData
   }.asFlow().map { pagingData ->
      pagingData
         .map { PortListItem.PortItem(it) }
         .insertSeparators { item1: PortListItem.PortItem?, item2: PortListItem.PortItem? ->
            val section = queryParameters.value?.second?.section ?: false
            val primarySortParameter = queryParameters.value?.second?.parameters?.firstOrNull()

            if (section && primarySortParameter != null) {
               header(primarySortParameter, item1, item2)
            } else null
         }
   }.cachedIn(viewModelScope)

   val portFilters = filterRepository.filters.map { entry ->
      entry[DataSource.PORT] ?: emptyList()
   }.asLiveData()

   suspend fun getPort(portNumber: Int) = portRepository.getPort(portNumber)

   private fun header(sort: SortParameter, item1: PortListItem.PortItem?, item2: PortListItem.PortItem?): PortListItem.HeaderItem? {
      val item1String = parameterToName(sort.parameter.parameter, item1?.port)
      val item2String = parameterToName(sort.parameter.parameter, item2?.port)

      return if (item1String == null && item2String != null) {
         PortListItem.HeaderItem(item2String)
      } else if (item1String != null && item2String != null && item1String != item2String) {
         PortListItem.HeaderItem(item2String)
      } else null
   }

   private fun parameterToName(parameter: String, port: Port?): String? {
      if (port == null) return null

      return when(parameter) {
         "port_name" -> port.portName
         "alternate_name" -> "${port.alternateName}"
         "location" -> "${port.latitude} ${port.longitude}"
         "latitude" -> "${port.latitude}"
         "longitude" -> "${port.longitude}"
         "port_number" -> "${port.portNumber}"
         "region_name" -> "${port.regionName}"
         "region_number" -> "${port.regionNumber}"
         "unlo_code" -> "${port.unloCode}"
         "country_name" -> "${port.countryName}"
         "country_Code" -> "${port.countryCode}"
         "dod_water_body" -> "${port.dodWaterBody}"
         "publication_number" -> "${port.publicationNumber}"
         "chart_number" -> "${port.chartNumber}"
         "s_57_enc" -> "${port.s57Enc}"
         "dnc" -> "${port.dnc}"
         "tide" -> "${port.tide}"
         "entrance_width" -> "${port.entranceWidth}"
         "channel_depth" -> "${port.channelDepth}"
         "anchorage_depth" -> "${port.anchorageDepth}"
         "cargo_pier_depth" -> "${port.cargoPierDepth}"
         "oil_terminal_depth" -> "${port.oilTerminalDepth}"
         "liquified_natural_gas_terminal_depth" -> "${port.liquifiedNaturalGasTerminalDepth}"
         "max_vessel_length" -> "${port.maxVesselLength}"
         "max_vessel_beam" -> "${port.maxVesselBeam}"
         "max_vessel_draft" -> "${port.maxVesselDraft}"
         "offshore_max_vessel_length" -> "${port.offshoreMaxVesselLength}"
         "offshore_max_vessel_beam" -> "${port.offshoreMaxVesselBeam}"
         "offshore_max_vessel_draft" -> "${port.offshoreMaxVesselDraft}"
         "harbor_size" -> "${port.harborSize}"
         "harbor_type" -> "${port.harborType}"
         "harbor_use" -> "${port.harborUse}"
         "shelter" -> "${port.shelter}"
         "entrance_restriction_tide" -> "${port.entranceRestrictionTide}"
         "entrance_restriction_swell" -> "${port.entranceRestrictionSwell}"
         "entrance_restriction_ice" -> "${port.entranceRestrictionIce}"
         "entrance_restriction_other" -> "${port.entranceRestrictionOther}"
         "overhead_limits" -> "${port.overheadLimits}"
         "ukc_management_system" -> "${port.ukcManagementSystem}"
         "good_holding_ground" -> "${port.goodHoldingGround}"
         "turning_area" -> "${port.turningArea}"
         "port_security" -> "${port.portSecurity}"
         "eta_message" -> "${port.etaMessage}"
         "quarantine_pratique" -> "${port.quarantinePratique}"
         "quarantine_sanitation" -> "${port.quarantineSanitation}"
         "quarantine_other" -> "${port.quarantineOther}"
         "traffic_separation_scheme" -> "${port.trafficSeparationScheme}"
         "vessel_traffic_service" -> "${port.vesselTrafficService}"
         "first_port_of_entry" -> "${port.firstPortOfEntry}"
         "pilotage_compulsory" -> "${port.pilotageCompulsory}"
         "pilotage_available" -> "${port.pilotageAvailable}"
         "pilotage_local_assist" -> "${port.pilotageLocalAssist}"
         "pilotage_advisable" -> "${port.pilotageAdvisable}"
         "tugs_salvage" -> "${port.tugsSalvage}"
         "tugs_assist" -> "${port.tugsAssist}"
         "communications_telephone" -> "${port.communicationsTelephone}"
         "communications_telegraph" -> "${port.communicationsTelegraph}"
         "communications_radio" -> "${port.communicationsRadio}"
         "communications_radio_telephone" -> "${port.communicationsRadioTelephone}"
         "communications_air" -> "${port.communicationsAir}"
         "communications_rail" -> "${port.communicationsRail}"
         "search_and_rescue" -> "${port.searchAndRescue}"
         "navigation_area" -> "${port.navigationArea}"
         "facilities_wharves" -> "${port.facilitiesWharves}"
         "facilities_anchor" -> "${port.facilitiesAnchor}"
         "facilities_dangerous_cargo" -> "${port.facilitiesDangerousCargo}"
         "facilities_med_moor" -> "${port.facilitiesMedMoor}"
         "facilities_beach_moor" -> "${port.facilitiesBeachMoor}"
         "facilities_ice_moor" -> "${port.facilitiesIceMoor}"
         "facilities_roro" -> "${port.facilitiesRoro}"
         "facilities_solid_bulk" -> "${port.facilitiesSolidBulk}"
         "facilities_liquid_bulk" -> "${port.facilitiesLiquidBulk}"
         "facilities_container" -> "${port.facilitiesContainer}"
         "facilities_break_bulk" -> "${port.facilitiesBreakBulk}"
         "facilities_oil_terminal" -> "${port.facilitiesOilTerminal}"
         "facilities_long_terminal" -> "${port.facilitiesLongTerminal}"
         "facilities_other" -> "${port.facilitiesOther}"
         "medical_facilities" -> "${port.medicalFacilities}"
         "garbage_disposal" -> "${port.garbageDisposal}"
         "chemical_holding_tank_disposal" -> "${port.chemicalHoldingTankDisposal}"
         "degauss" -> "${port.degauss}"
         "dirty_ballast" -> "${port.dirtyBallast}"
         "cranes_fixed" -> "${port.cranesFixed}"
         "cranes_mobile" -> "${port.cranesMobile}"
         "cranes_floating" -> "${port.cranesFloating}"
         "cranes_container" -> "${port.cranesContainer}"
         "lifts_100" -> "${port.lifts100}"
         "lifts_50" -> "${port.lifts50}"
         "lifts_25" -> "${port.lifts25}"
         "lifts_0" -> "${port.lifts0}"
         "services_longshore" -> "${port.servicesLongshore}"
         "services_electrical" -> "${port.servicesElectrical}"
         "services_steam" -> "${port.servicesSteam}"
         "services_navigational_equipment" -> "${port.servicesNavigationalEquipment}"
         "services_electrical_repair" -> "${port.servicesElectricalRepair}"
         "services_ice_breaking" -> "${port.servicesIceBreaking}"
         "services_diving" -> "${port.servicesDiving}"
         "supplies_provisions" -> "${port.suppliesProvisions}"
         "supplies_water" -> "${port.suppliesWater}"
         "supplies_fuel" -> "${port.suppliesFuel}"
         "supplies_diesel" -> "${port.suppliesDiesel}"
         "supplies_aviation_fuel" -> "${port.suppliesAviationFuel}"
         "supplies_deck" -> "${port.suppliesDeck}"
         "supplies_engine" -> "${port.suppliesEngine}"
         "repair_code" -> "${port.repairCode}"
         "dry_dock" -> "${port.dryDock}"
         "railway" -> "${port.railway}"
         "us_representative" -> "${port.usRepresentative}"
         "global_id" -> "${port.globalId}"
         "s_121_water_body" -> "${port.s121WaterBody}"
         else -> null
      }
   }
}