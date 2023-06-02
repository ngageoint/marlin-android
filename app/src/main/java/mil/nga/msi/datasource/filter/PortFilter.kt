package mil.nga.msi.datasource.filter

import mil.nga.msi.datasource.port.types.Decision
import mil.nga.msi.datasource.port.types.HarborType
import mil.nga.msi.datasource.port.types.HarborUse
import mil.nga.msi.datasource.port.types.RepairCode
import mil.nga.msi.datasource.port.types.Shelter
import mil.nga.msi.datasource.port.types.Size
import mil.nga.msi.datasource.port.types.UnderkeelClearance
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType

class PortFilter {
   companion object {
      val parameters = listOf(
         FilterParameter(
            title = "Main Port Name",
            parameter = "port_name",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "Alternate Port Name",
            parameter = "alternate_name",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "Location",
            parameter = "location",
            type = FilterParameterType.LOCATION
         ),
         FilterParameter(
            title = "Latitude",
            parameter = "latitude",
            type = FilterParameterType.DOUBLE
         ),
         FilterParameter(
            title = "Longitude",
            parameter = "longitude",
            type = FilterParameterType.DOUBLE
         ),
         FilterParameter(
            title = "World Port Index Number",
            parameter = "port_number",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Region Name",
            parameter = "region_name",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "Region Number",
            parameter = "region_number",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "UN/LOCODE",
            parameter = "unlo_code",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "Country",
            parameter = "country_name",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "Country Code",
            parameter = "country_Code",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "World Water Body",
            parameter = "dod_water_body",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "Sailing Directions of Publication",
            parameter = "publication_number",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "Standard Nautical Chart",
            parameter = "chart_number",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "IHO S-57 Electronic Navigational Chart",
            parameter = "s_57_enc",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "IHO S-101 Electronic Navigational Chart",
            parameter = "s_101_enc",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "Digital Nautical Chart",
            parameter = "dnc",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "Tidal Range (m)",
            parameter = "tide",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Entrance Width (m)",
            parameter = "entrance_width",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Channel Depth (m)",
            parameter = "channel_depth",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Anchorage Depth (m)",
            parameter = "anchorage_depth",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Cargo Pier Depth (m)",
            parameter = "cargo_pier_depth",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Oil Terminal Depth (m)",
            parameter = "oil_terminal_depth",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Liquified Natural Gas Terminal Depth (m)",
            parameter = "liquified_natural_gas_terminal_depth",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Maximum Vessel Length Depth (m)",
            parameter = "max_vessel_length",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Maximum Vessel Beam (m)",
            parameter = "max_vessel_beam",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Maximum Vessel Draft (m)",
            parameter = "max_vessel_draft",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Offshore Maximum Vessel Length (m)",
            parameter = "offshore_max_vessel_length",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Offshore Maximum Vessel Beam (m)",
            parameter = "offshore_max_vessel_beam",
            type = FilterParameterType.INT
         ),
         FilterParameter(
            title = "Offshore Maximum Vessel Draft (m)",
            parameter = "offshore_max_vessel_draft",
            type = FilterParameterType.INT
         ),

         FilterParameter(
            title = "Harbor Size",
            parameter = "harbor_size",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Size.values().toList()
         ),

         FilterParameter(
            title = "Harbor Type",
            parameter = "harbor_type",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = HarborType.values().toList()
         ),
         FilterParameter(
            title = "Harbor Use",
            parameter = "harbor_use",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = HarborUse.values().toList()
         ),
         FilterParameter(
            title = "Shelter",
            parameter = "shelter",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Shelter.values().toList()
         ),
         FilterParameter(
            title = "Entrance Restriction - Tide",
            parameter = "entrance_restriction_tide",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Entrance Restriction - Heavy Swell",
            parameter = "entrance_restriction_swell",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Entrance Restriction - Ice",
            parameter = "entrance_restriction_ice",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Entrance Restriction - Other",
            parameter = "entrance_restriction_other",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Overhead Limits",
            parameter = "overhead_limits",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Underkeel Clearance Management System",
            parameter = "ukc_management_system",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = UnderkeelClearance.values().toList()
         ),
         FilterParameter(
            title = "Good Holding Ground",
            parameter = "good_holding_ground",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Turning Area",
            parameter = "turning_area",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Port Security",
            parameter = "port_security",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Estimated Time Of Arrival Message",
            parameter = "eta_message",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Quarantine - Pratique",
            parameter = "quarantine_pratique",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Quarantine - Sanitation",
            parameter = "quarantine_sanitation",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Quarantine - Other",
            parameter = "quarantine_other",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Traffic Separation Scheme",
            parameter = "traffic_separation_scheme",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Vessel Traffic Service",
            parameter = "vessel_traffic_service",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "First Port Of Entry",
            parameter = "first_port_of_entry",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Pilotage - Compulsory",
            parameter = "pilotage_compulsory",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Pilotage - Available",
            parameter = "pilotage_available",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Pilotage - Local Assistance",
            parameter = "pilotage_local_assist",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Pilotage - Advisable",
            parameter = "pilotage_advisable",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Tugs - Salvage",
            parameter = "tugs_salvage",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Tugs - Assistance",
            parameter = "tugs_assist",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Communications - Telephone",
            parameter = "communications_telephone",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Communications - Telefax",
            parameter = "communications_telegraph",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Communications - Radio",
            parameter = "communications_radio",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Communications - Radiotelephone",
            parameter = "communications_radio_telephone",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Communications - Airport",
            parameter = "communications_air",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Communications - Rail",
            parameter = "communications_rail",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Search and Rescue",
            parameter = "search_and_rescue",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "NAVAREA",
            parameter = "navigation_area",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "Facilities - Wharves",
            parameter = "facilities_wharves",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - Anchorage",
            parameter = "facilities_anchor",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - Dangerous Cargo Anchorage",
            parameter = "facilities_dangerous_cargo",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - Med Mooring",
            parameter = "facilities_med_moor",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - Beach Mooring",
            parameter = "facilities_beach_moor",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - Ice Mooring",
            parameter = "facilities_ice_moor",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - RoRo",
            parameter = "facilities_roro",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - Solid Bulk",
            parameter = "facilities_solid_bulk",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - Liquid Bulk",
            parameter = "facilities_liquid_bulk",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - Container",
            parameter = "facilities_container",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - Breakbulk",
            parameter = "facilities_break_bulk",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - Oil Terminal",
            parameter = "facilities_oil_terminal",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - LNG Terminal",
            parameter = "facilities_long_terminal",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Facilities - Other",
            parameter = "facilities_other",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Medical Facilities",
            parameter = "medical_facilities",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Garbage Disposal",
            parameter = "garbage_disposal",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Chemical Holding Tank",
            parameter = "chemical_holding_tank_disposal",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Degaussing",
            parameter = "degauss",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Dirty Ballast Disposal",
            parameter = "dirty_ballast",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Cranes - Fixed",
            parameter = "cranes_fixed",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Cranes - Mobile",
            parameter = "cranes_mobile",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Cranes - Floating",
            parameter = "cranes_floating",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Cranes - Container",
            parameter = "cranes_container",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Lifts - 100+ Tons",
            parameter = "lifts_100",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Lifts - 50-100 Tons",
            parameter = "lifts_50",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Lifts - 25-49 Tons",
            parameter = "lifts_25",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Lifts - 0-24 Tons",
            parameter = "lifts_0",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Services - Longshoremen",
            parameter = "services_longshore",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Services - Electricity",
            parameter = "services_electrical",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Services - Steam",
            parameter = "services_steam",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Services - Navigational Equipment",
            parameter = "services_navigational_equipment",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Services - Electrical Repair",
            parameter = "services_electrical_repair",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Services - Ice Breaking",
            parameter = "services_ice_breaking",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Services - Diving",
            parameter = "services_diving",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Supplies - Provisions",
            parameter = "supplies_provisions",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Supplies - Potable Water",
            parameter = "supplies_water",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Supplies - Fuel Oil",
            parameter = "supplies_fuel",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Supplies - Diesel Oil",
            parameter = "supplies_diesel",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Supplies - Aviation Fuel",
            parameter = "supplies_aviation_fuel",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Supplies - Deck",
            parameter = "supplies_deck",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Supplies - Engine",
            parameter = "supplies_engine",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Repair Code",
            parameter = "repair_code",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = RepairCode.values().toList()
         ),
         FilterParameter(
            title = "Dry Dock",
            parameter = "dry_dock",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "Railway",
            parameter = "railway",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = Decision.values().toList()
         ),
         FilterParameter(
            title = "US Representative",
            parameter = "us_representative",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "Global Id",
            parameter = "global_id",
            type = FilterParameterType.STRING
         ),
         FilterParameter(
            title = "121 Water Body",
            parameter = "s_121_water_body",
            type = FilterParameterType.STRING
         ),
      )
   }
}