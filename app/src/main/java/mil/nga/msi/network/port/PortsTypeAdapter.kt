package mil.nga.msi.network.port

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.types.*
import mil.nga.msi.network.nextDoubleOrNull
import mil.nga.msi.network.nextIntOrNull
import mil.nga.msi.network.nextStringOrNull

class PortsTypeAdapter: TypeAdapter<List<Port>>() {
   override fun read(`in`: JsonReader): List<Port> {
      val ports = mutableListOf<Port>()
      if (`in`.peek() == JsonToken.NULL) {
         `in`.nextNull()
         return ports
      }

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return ports
      }

      `in`.beginObject()
      while (`in`.hasNext()) {
         when(`in`.nextName()) {
            "ports" -> {
               ports.addAll(readPorts(`in`))
            }
            else -> `in`.skipValue()
         }
      }
      `in`.endObject()

      return ports
   }

   private fun readPorts(`in`: JsonReader): List<Port> {
      val ports = mutableListOf<Port>()

      if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
         `in`.skipValue()
         return ports
      }

      `in`.beginArray()

      while (`in`.hasNext()) {
         readPort(`in`)?.let { ports.add(it) }
      }

      `in`.endArray()

      return ports
   }

   private fun readPort(`in`: JsonReader): Port? {
      var portNumber: Int? = null
      var portName: String? = null
      var latitude: Double? = null
      var longitude: Double? = null
      var regionNumber: Int? = null
      var regionName: String? = null
      var countryCode: String? = null
      var countryName: String? = null
      var publicationNumber: String? = null
      var chartNumber: String? = null
      var navigationArea: String? = null
      var harborSize: Size? = null
      var harborType: HarborType? = null
      var shelter: Shelter? = null
      var entranceRestrictionTide: Decision? = null
      var entranceRestrictionSwell: Decision? = null
      var entranceRestrictionIce: Decision? = null
      var entranceRestrictionOther: Decision? = null
      var overheadLimits: Decision? = null
      var channelDepth: Int? = null
      var anchorageDepth: Int? = null
      var cargoPierDepth: Int? = null
      var oilTerminalDepth: Int? = null
      var tide: Int? = null
      var maxVesselLength: Int? = null
      var maxVesselBeam: Int? = null
      var maxVesselDraft: Int? = null
      var goodHoldingGround: Decision? = null
      var turningArea: Decision? = null
      var firstPortOfEntry: Decision? = null
      var usRepresentative: String? = null
      var pilotageCompulsory: Decision? = null
      var pilotageAvailable: Decision? = null
      var pilotageLocalAssist: Decision? = null
      var pilotageAdvisable: Decision? = null
      var tugsSalvage: Decision? = null
      var tugsAssist: Decision? = null
      var quarantinePratique: Decision? = null
      var quarantineOther: Decision? = null
      var communicationsTelephone: Decision? = null
      var communicationsTelegraph: Decision? = null
      var communicationsRadio: Decision? = null
      var communicationsRadioTelephone: Decision? = null
      var communicationsAir: Decision? = null
      var communicationsRail: Decision? = null
      var facilitiesWharves: Decision? = null
      var facilitiesAnchor: Decision? = null
      var facilitiesMedMoor: Decision? = null
      var facilitiesBeachMoor: Decision? = null
      var facilitiesIceMoor: Decision? = null
      var medicalFacilities: Decision? = null
      var garbageDisposal: Decision? = null
      var degauss: Decision? = null
      var dirtyBallast: Decision? = null
      var cranesFixed: Decision? = null
      var cranesMobile: Decision? = null
      var cranesFloating: Decision? = null
      var cranesContainer: Decision? = null
      var lifts100: Decision? = null
      var lifts50: Decision? = null
      var lifts25: Decision? = null
      var lifts0: Decision? = null
      var servicesLongshore: Decision? = null
      var servicesElectrical: Decision? = null
      var servicesSteam: Decision? = null
      var servicesNavigationalEquipment: Decision? = null
      var servicesElectricalRepair: Decision? = null
      var servicesIceBreaking: Decision? = null
      var servicesDiving: Decision? = null
      var suppliesProvisions: Decision? = null
      var suppliesWater: Decision? = null
      var suppliesFuel: Decision? = null
      var suppliesDiesel: Decision? = null
      var suppliesDeck: Decision? = null
      var suppliesEngine: Decision? = null
      var suppliesAviationFuel: Decision? = null
      var repairCode: RepairCode? = null
      var dryDock: Size? = null
      var railway: Size? = null
      var quarantineSanitation: Decision? = null
      var harborUse: HarborUse? = null
      var ukcManagementSystem: UnderkeelClearance? = null
      var portSecurity: Decision? = null
      var etaMessage: Decision? = null
      var searchAndRescue: Decision? = null
      var trafficSeparationScheme: Decision? = null
      var vesselTrafficService: Decision? = null
      var chemicalHoldingTankDisposal: Decision? = null
      var globalId: String? = null
      var facilitiesRoro: Decision? = null
      var facilitiesSolidBulk: Decision? = null
      var facilitiesContainer: Decision? = null
      var facilitiesBreakBulk: Decision? = null
      var facilitiesOilTerminal: Decision? = null
      var facilitiesLongTerminal: Decision? = null
      var facilitiesOther: Decision? = null
      var facilitiesDangerousCargo: Decision? = null
      var facilitiesLiquidBulk: Decision? = null
      var unloCode: String? = null
      var dnc: String? = null
      var s121WaterBody: String? = null
      var s57Enc: String? = null
      var s101Enc: String? = null
      var dodWaterBody: String? = null
      var alternateName: String? = null
      var entranceWidth: Int? = null
      var liquifiedNaturalGasTerminalDepth: Int? = null
      var offshoreMaxVesselLength: Int? = null
      var offshoreMaxVesselBeam: Int? = null
      var offshoreMaxVesselDraft: Int? = null

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return null
      }

      `in`.beginObject()

      while(`in`.hasNext()) {
         when(`in`.nextName()) {
            "portNumber" -> {
               portNumber = `in`.nextIntOrNull()
            }
            "portName" -> {
               portName = `in`.nextStringOrNull()
            }
            "xcoord" -> {
               longitude = `in`.nextDoubleOrNull()
            }
            "ycoord" -> {
               latitude = `in`.nextDoubleOrNull()
            }
            "regionNumber" -> {
               regionNumber = `in`.nextIntOrNull()
            }
            "regionName" -> {
               regionName = `in`.nextStringOrNull()
            }
            "countryCode" -> {
               countryCode = `in`.nextStringOrNull()
            }
            "countryName" -> {
               countryName = `in`.nextStringOrNull()
            }
            "publicationNumber" -> {
               publicationNumber = `in`.nextStringOrNull()
            }
            "chartNumber" -> {
               chartNumber = `in`.nextStringOrNull()
            }
            "navArea" -> {
               navigationArea = `in`.nextStringOrNull()
            }
            "harborSize" -> {
               harborSize = `in`.nextStringOrNull()?.let {
                  Size.fromValue(it)
               }
            }
            "harborType" -> {
               harborType = `in`.nextStringOrNull()?.let {
                  HarborType.fromValue(it)
               }
            }
            "shelter" -> {
               shelter = `in`.nextStringOrNull()?.let {
                  Shelter.fromValue(it)
               }
            }
            "erTide" -> {
               entranceRestrictionTide = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "erSwell" -> {
               entranceRestrictionSwell = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "erIce" -> {
               entranceRestrictionIce = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "erOther" -> {
               entranceRestrictionOther = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "overheadLimits" -> {
               overheadLimits = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "chDepth" -> {
               channelDepth = `in`.nextStringOrNull()?.toIntOrNull()
            }
            "anDepth" -> {
               anchorageDepth = `in`.nextStringOrNull()?.toIntOrNull()
            }
            "cpDepth" -> {
               cargoPierDepth = `in`.nextStringOrNull()?.toIntOrNull()
            }
            "otDepth" -> {
               oilTerminalDepth = `in`.nextStringOrNull()?.toIntOrNull()
            }
            "tide" -> {
               tide = `in`.nextStringOrNull()?.toIntOrNull()
            }
            "maxVesselLength" -> {
               maxVesselLength = `in`.nextStringOrNull()?.toIntOrNull()
            }
            "maxVesselBeam" -> {
               maxVesselBeam = `in`.nextStringOrNull()?.toIntOrNull()
            }
            "maxVesselDraft" -> {
               maxVesselDraft = `in`.nextStringOrNull()?.toIntOrNull()
            }
            "goodHoldingGround" -> {
               goodHoldingGround = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "turningArea" -> {
               turningArea = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "firstPortOfEntry" -> {
               firstPortOfEntry = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "usRep" -> {
               usRepresentative = `in`.nextStringOrNull()
            }
            "ptCompulsory" -> {
               pilotageCompulsory = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "ptAvailable" -> {
               pilotageAvailable = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "ptLocalAssist" -> {
               pilotageLocalAssist = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "ptAdvisable" -> {
               pilotageAdvisable = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "tugsSalvage" -> {
               tugsSalvage = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "tugsAssist" -> {
               tugsAssist = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "qtPratique" -> {
               quarantinePratique = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "qtOther" -> {
               quarantineOther = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "cmTelephone" -> {
               communicationsTelephone = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "cmTelegraph" -> {
               communicationsTelegraph = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "cmRadio" -> {
               communicationsRadio = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "cmRadioTel" -> {
               communicationsRadioTelephone = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "cmAir" -> {
               communicationsAir = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "cmRail" -> {
               communicationsRail = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loWharves" -> {
               facilitiesWharves = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loAnchor" -> {
               facilitiesAnchor = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loMedMoor" -> {
               facilitiesMedMoor = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loBeachMoor" -> {
               facilitiesBeachMoor = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loIceMoor" -> {
               facilitiesIceMoor = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "medFacilities" -> {
               medicalFacilities = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "garbageDisposal" -> {
               garbageDisposal = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "degauss" -> {
               degauss = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "dirtyBallast" -> {
               dirtyBallast = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "crFixed" -> {
               cranesFixed = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "crMobile" -> {
               cranesMobile = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "crFloating" -> {
               cranesFloating = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "lifts100" -> {
               lifts100 = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "lifts50" -> {
               lifts50 = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "lifts25" -> {
               lifts25 = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "lifts0" -> {
               lifts0 = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "srLongshore" -> {
               servicesLongshore = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "srElectrical" -> {
               servicesElectrical = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "srSteam" -> {
               servicesSteam = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "srNavigEquip" -> {
               servicesNavigationalEquipment = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "srElectRepair" -> {
               servicesElectricalRepair = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "suProvisions" -> {
               suppliesProvisions = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "suWater" -> {
               suppliesWater = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "suFuel" -> {
               suppliesFuel = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "suDiesel" -> {
               suppliesDiesel = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "suDeck" -> {
               suppliesDeck = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "suEngine" -> {
               suppliesEngine = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "repairCode" -> {
               repairCode = `in`.nextStringOrNull()?.let {
                  RepairCode.fromValue(it)
               }
            }
            "drydock" -> {
               dryDock = `in`.nextStringOrNull()?.let {
                  Size.fromValue(it)
               }
            }
            "railway" -> {
               railway = `in`.nextStringOrNull()?.let {
                  Size.fromValue(it)
               }
            }
            "qtSanitation" -> {
               quarantineSanitation = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "suAviationFuel" -> {
               suppliesAviationFuel = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "harborUse" -> {
               harborUse = `in`.nextStringOrNull()?.let {
                  HarborUse.fromValue(it)
               }
            }
            "ukcMgmtSystem" -> {
               ukcManagementSystem = `in`.nextStringOrNull()?.let {
                  UnderkeelClearance.fromValue(it)
               }
            }
            "portSecurity" -> {
               portSecurity = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "etaMessage" -> {
               etaMessage = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "searchAndRescue" -> {
               searchAndRescue = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "tss" -> {
               trafficSeparationScheme = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "vts" -> {
               vesselTrafficService = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "cht" -> {
               chemicalHoldingTankDisposal = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "globalId" -> {
               globalId = `in`.nextStringOrNull()
            }
            "loRoro" -> {
               facilitiesRoro = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loSolidBulk" -> {
               facilitiesSolidBulk = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loContainer" -> {
               facilitiesContainer = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loBreakBulk" -> {
               facilitiesBreakBulk = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loOilTerm" -> {
               facilitiesOilTerminal = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loLongTerm" -> {
               facilitiesLongTerminal = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loOther" -> {
               facilitiesOther = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loDangCargo" -> {
               facilitiesDangerousCargo = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "loLiquidBulk" -> {
               facilitiesLiquidBulk = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "srIceBreaking" -> {
               servicesIceBreaking = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "srDiving" -> {
               servicesDiving = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "cranesContainer" -> {
               cranesContainer = `in`.nextStringOrNull()?.let {
                  Decision.fromValue(it)
               }
            }
            "unloCode" -> {
               unloCode = `in`.nextStringOrNull()
            }
            "dnc" -> {
               dnc = `in`.nextStringOrNull()
            }
            "s121WaterBody" -> {
               s121WaterBody = `in`.nextStringOrNull()
            }
            "s57Enc" -> {
               s57Enc = `in`.nextStringOrNull()
            }
            "s101Enc" -> {
               s101Enc = `in`.nextStringOrNull()
            }
            "dodWaterBody" -> {
               dodWaterBody = `in`.nextStringOrNull()
            }
            "alternateName" -> {
               alternateName = `in`.nextStringOrNull()
            }
            "entranceWidth" -> {
               entranceWidth = `in`.nextStringOrNull()?.toIntOrNull()
            }
            "lngTerminalDepth" -> {
               liquifiedNaturalGasTerminalDepth = `in`.nextIntOrNull()
            }
            "offMaxVesselLength" -> {
               offshoreMaxVesselLength = `in`.nextStringOrNull()?.toIntOrNull()
            }
            "offMaxVesselBeam" -> {
               offshoreMaxVesselBeam = `in`.nextStringOrNull()?.toIntOrNull()
            }
            "offMaxVesselDraft" -> {
               offshoreMaxVesselDraft = `in`.nextStringOrNull()?.toIntOrNull()
            }
            else -> `in`.skipValue()
         }
      }

      `in`.endObject()

      return if (portNumber != null && portName != null && latitude != null && longitude != null) {
        Port(portNumber, portName, latitude, longitude).apply {
           this.regionNumber = regionNumber
           this.regionName = regionName
           this.countryCode = countryCode
           this.countryName = countryName
           this.publicationNumber = publicationNumber
           this.chartNumber = chartNumber
           this.navigationArea = navigationArea
           this.harborSize = harborSize
           this.harborType = harborType
           this.shelter = shelter
           this.entranceRestrictionTide = entranceRestrictionTide
           this.entranceRestrictionSwell = entranceRestrictionSwell
           this.entranceRestrictionIce = entranceRestrictionIce
           this.entranceRestrictionOther = entranceRestrictionOther
           this.overheadLimits = overheadLimits
           this.channelDepth = channelDepth
           this.anchorageDepth = anchorageDepth
           this.cargoPierDepth = cargoPierDepth
           this.oilTerminalDepth = oilTerminalDepth
           this.tide = tide
           this.maxVesselLength = maxVesselLength
           this.maxVesselBeam = maxVesselBeam
           this.maxVesselDraft = maxVesselDraft
           this.goodHoldingGround = goodHoldingGround
           this.turningArea = turningArea
           this.firstPortOfEntry = firstPortOfEntry
           this.usRepresentative = usRepresentative
           this.pilotageCompulsory = pilotageCompulsory
           this.pilotageAvailable = pilotageAvailable
           this.pilotageLocalAssist = pilotageLocalAssist
           this.pilotageAdvisable = pilotageAdvisable
           this.tugsSalvage = tugsSalvage
           this.tugsAssist = tugsAssist
           this.quarantinePratique = quarantinePratique
           this.quarantineOther = quarantineOther
           this.communicationsTelephone = communicationsTelephone
           this.communicationsTelegraph = communicationsTelegraph
           this.communicationsRadio = communicationsRadio
           this.communicationsRadioTelephone = communicationsRadioTelephone
           this.communicationsAir = communicationsAir
           this.communicationsRail = communicationsRail
           this.facilitiesWharves = facilitiesWharves
           this.facilitiesAnchor = facilitiesAnchor
           this.facilitiesMedMoor = facilitiesMedMoor
           this.facilitiesBeachMoor = facilitiesBeachMoor
           this.facilitiesIceMoor = facilitiesIceMoor
           this.medicalFacilities = medicalFacilities
           this.garbageDisposal = garbageDisposal
           this.degauss = degauss
           this.dirtyBallast = dirtyBallast
           this.cranesFixed = cranesFixed
           this.cranesMobile = cranesMobile
           this.cranesFloating = cranesFloating
           this.lifts100 = lifts100
           this.lifts50 = lifts50
           this.lifts25 = lifts25
           this.lifts0 = lifts0
           this.servicesLongshore = servicesLongshore
           this.servicesElectrical = servicesElectrical
           this.servicesSteam = servicesSteam
           this.servicesNavigationalEquipment = servicesNavigationalEquipment
           this.servicesElectricalRepair = servicesElectricalRepair
           this.suppliesProvisions = suppliesProvisions
           this.suppliesWater = suppliesWater
           this.suppliesFuel = suppliesFuel
           this.suppliesDiesel = suppliesDiesel
           this.suppliesDeck = suppliesDeck
           this.suppliesEngine = suppliesEngine
           this.repairCode = repairCode
           this.dryDock = dryDock
           this.railway = railway
           this.quarantineSanitation = quarantineSanitation
           this.suppliesAviationFuel = suppliesAviationFuel
           this.harborUse = harborUse
           this.ukcManagementSystem = ukcManagementSystem
           this.portSecurity = portSecurity
           this.etaMessage = etaMessage
           this.searchAndRescue = searchAndRescue
           this.trafficSeparationScheme = trafficSeparationScheme
           this.vesselTrafficService = vesselTrafficService
           this.chemicalHoldingTankDisposal = chemicalHoldingTankDisposal
           this.globalId = globalId
           this.facilitiesRoro = facilitiesRoro
           this.facilitiesSolidBulk = facilitiesSolidBulk
           this.facilitiesContainer = facilitiesContainer
           this.facilitiesBreakBulk = facilitiesBreakBulk
           this.facilitiesOilTerminal = facilitiesOilTerminal
           this.facilitiesLongTerminal = facilitiesLongTerminal
           this.facilitiesOther = facilitiesOther
           this.facilitiesDangerousCargo = facilitiesDangerousCargo
           this.facilitiesLiquidBulk = facilitiesLiquidBulk
           this.servicesIceBreaking = servicesIceBreaking
           this.servicesDiving = servicesDiving
           this.cranesContainer = cranesContainer
           this.unloCode = unloCode
           this.dnc = dnc
           this.s121WaterBody = s121WaterBody
           this.s57Enc = s57Enc
           this.s101Enc = s101Enc
           this.dodWaterBody = dodWaterBody
           this.alternateName = alternateName
           this.entranceWidth = entranceWidth
           this.liquifiedNaturalGasTerminalDepth = liquifiedNaturalGasTerminalDepth
           this.offshoreMaxVesselLength = offshoreMaxVesselLength
           this.offshoreMaxVesselBeam = offshoreMaxVesselBeam
           this.offshoreMaxVesselDraft = offshoreMaxVesselDraft
        }
      } else { null }
   }

   override fun write(out: JsonWriter, value: List<Port>) {
      throw UnsupportedOperationException()
   }
}