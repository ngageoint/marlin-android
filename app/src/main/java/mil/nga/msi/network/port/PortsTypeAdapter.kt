package mil.nga.msi.network.port

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.network.nextDoubleOrNull
import mil.nga.msi.network.nextIntOrNull
import mil.nga.msi.network.nextStringOrNull
import java.lang.UnsupportedOperationException

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
      var harborSize: String? = null
      var harborType: String? = null
      var shelter: String? = null
      var entranceRestrictionTide: String? = null
      var entranceRestrictionSwell: String? = null
      var entranceRestrictionIce: String? = null
      var entranceRestrictionOther: String? = null
      var overheadLimits: String? = null
      var channelDepth: Int? = null
      var anchorageDepth: Int? = null
      var cargoPierDepth: Int? = null
      var oilTerminalDepth: Int? = null
      var tide: Int? = null
      var maxVesselLength: Int? = null
      var maxVesselBeam: Int? = null
      var maxVesselDraft: Int? = null
      var goodHoldingGround: String? = null
      var turningArea: String? = null
      var firstPortOfEntry: String? = null
      var usRepresentative: String? = null
      var pilotageCompulsory: String? = null
      var pilotageAvailable: String? = null
      var pilotageLocalAssist: String? = null
      var pilotageAdvisable: String? = null
      var tugsSalvage: String? = null
      var tugsAssist: String? = null
      var quarantinePratique: String? = null
      var quarantineOther: String? = null
      var communicationsTelephone: String? = null
      var communicationsTelegraph: String? = null
      var communicationsRadio: String? = null
      var communicationsRadioTelephone: String? = null
      var communicationsAir: String? = null
      var communicationsRail: String? = null
      var facilitiesWharves: String? = null
      var facilitiesAnchor: String? = null
      var facilitiesMedMoor: String? = null
      var facilitiesBeachMoor: String? = null
      var facilitiesIceMoor: String? = null
      var medicalFacilities: String? = null
      var garbageDisposal: String? = null
      var degauss: String? = null
      var dirtyBallast: String? = null
      var cranesFixed: String? = null
      var cranesMobile: String? = null
      var cranesFloating: String? = null
      var lifts100: String? = null
      var lifts50: String? = null
      var lifts25: String? = null
      var lifts0: String? = null
      var servicesLongshore: String? = null
      var servicesElectrical: String? = null
      var servicesSteam: String? = null
      var servicesNavigationalEquipment: String? = null
      var servicesElectricalRepair: String? = null
      var suppliesProvisions: String? = null
      var suppliesWater: String? = null
      var suppliesFuel: String? = null
      var suppliesDiesel: String? = null
      var suppliesDeck: String? = null
      var suppliesEngine: String? = null
      var repairCode: String? = null
      var drydock: String? = null
      var railway: String? = null
      var quarantineSanitation: String? = null
      var suppliesAviationFuel: String? = null
      var harborUse: String? = null
      var ukcManagementSystem: String? = null
      var portSecurity: String? = null
      var etaMessage: String? = null
      var searchAndRescue: String? = null
      var trafficSeparationScheme: String? = null
      var vesselTrafficService: String? = null
      var chemicalHoldingTankDisposal: String? = null
      var globalId: String? = null
      var facilitiesRoro: String? = null
      var facilitiesSolidBulk: String? = null
      var facilitiesContainer: String? = null
      var facilitiesBreakBulk: String? = null
      var facilitiesOilTerminal: String? = null
      var facilitiesLongTerminal: String? = null
      var facilitiesOther: String? = null
      var facilitiesDangerousCargo: String? = null
      var facilitiesLiquidBulk: String? = null
      var servicesIceBreaking: String? = null
      var servicesDiving: String? = null
      var cranesContainer: String? = null
      var unloCode: String? = null
      var dnc: String? = null
      var s121WaterBody: String? = null
      var s57Enc: String? = null
      var s101Enc: String? = null
      var dodWaterBody: String? = null
      var alternateName: String? = null
      var entranceWidth: Int? = null
      var liquifiedNaturalGasTerminalDepth: String? = null
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
               harborSize = `in`.nextStringOrNull()
            }
            "harborType" -> {
               harborType = `in`.nextStringOrNull()
            }
            "shelter" -> {
               shelter = `in`.nextStringOrNull()
            }
            "erTide" -> {
               entranceRestrictionTide = `in`.nextStringOrNull()
            }
            "erSwell" -> {
               entranceRestrictionSwell = `in`.nextStringOrNull()
            }
            "erIce" -> {
               entranceRestrictionIce = `in`.nextStringOrNull()
            }
            "erOther" -> {
               entranceRestrictionOther = `in`.nextStringOrNull()
            }
            "overheadLimits" -> {
               overheadLimits = `in`.nextStringOrNull()
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
               goodHoldingGround = `in`.nextStringOrNull()
            }
            "turningArea" -> {
               turningArea = `in`.nextStringOrNull()
            }
            "firstPortOfEntry" -> {
               firstPortOfEntry = `in`.nextStringOrNull()
            }
            "usRep" -> {
               usRepresentative = `in`.nextStringOrNull()
            }
            "ptCompulsory" -> {
               pilotageCompulsory = `in`.nextStringOrNull()
            }
            "ptAvailable" -> {
               pilotageAvailable = `in`.nextStringOrNull()
            }
            "ptLocalAssist" -> {
               pilotageLocalAssist = `in`.nextStringOrNull()
            }
            "ptAdvisable" -> {
               pilotageAdvisable = `in`.nextStringOrNull()
            }
            "tugsSalvage" -> {
               tugsSalvage = `in`.nextStringOrNull()
            }
            "tugsAssist" -> {
               tugsAssist = `in`.nextStringOrNull()
            }
            "qtPratique" -> {
               quarantinePratique = `in`.nextStringOrNull()
            }
            "qtOther" -> {
               quarantineOther = `in`.nextStringOrNull()
            }
            "cmTelephone" -> {
               communicationsTelephone = `in`.nextStringOrNull()
            }
            "cmTelegraph" -> {
               communicationsTelegraph = `in`.nextStringOrNull()
            }
            "cmRadio" -> {
               communicationsRadio = `in`.nextStringOrNull()
            }
            "cmRadioTel" -> {
               communicationsRadioTelephone = `in`.nextStringOrNull()
            }
            "cmAir" -> {
               communicationsAir = `in`.nextStringOrNull()
            }
            "cmRail" -> {
               communicationsRail = `in`.nextStringOrNull()
            }
            "loWharves" -> {
               facilitiesWharves = `in`.nextStringOrNull()
            }
            "loAnchor" -> {
               facilitiesAnchor = `in`.nextStringOrNull()
            }
            "loMedMoor" -> {
               facilitiesMedMoor = `in`.nextStringOrNull()
            }
            "loBeachMoor" -> {
               facilitiesBeachMoor = `in`.nextStringOrNull()
            }
            "loIceMoor" -> {
               facilitiesIceMoor = `in`.nextStringOrNull()
            }
            "medFacilities" -> {
               medicalFacilities = `in`.nextStringOrNull()
            }
            "garbageDisposal" -> {
               garbageDisposal = `in`.nextStringOrNull()
            }
            "degauss" -> {
               degauss = `in`.nextStringOrNull()
            }
            "dirtyBallast" -> {
               dirtyBallast = `in`.nextStringOrNull()
            }
            "crFixed" -> {
               cranesFixed = `in`.nextStringOrNull()
            }
            "crMobile" -> {
               cranesMobile = `in`.nextStringOrNull()
            }
            "crFloating" -> {
               cranesFloating = `in`.nextStringOrNull()
            }
            "lifts100" -> {
               lifts100 = `in`.nextStringOrNull()
            }
            "lifts50" -> {
               lifts50 = `in`.nextStringOrNull()
            }
            "lifts25" -> {
               lifts25 = `in`.nextStringOrNull()
            }
            "lifts0" -> {
               lifts0 = `in`.nextStringOrNull()
            }
            "srLongshore" -> {
               servicesLongshore = `in`.nextStringOrNull()
            }
            "srElectrical" -> {
               servicesElectrical = `in`.nextStringOrNull()
            }
            "srSteam" -> {
               servicesSteam = `in`.nextStringOrNull()
            }
            "srNavigEquip" -> {
               servicesNavigationalEquipment = `in`.nextStringOrNull()
            }
            "srElectRepair" -> {
               servicesElectricalRepair = `in`.nextStringOrNull()
            }
            "suProvisions" -> {
               suppliesProvisions = `in`.nextStringOrNull()
            }
            "suWater" -> {
               suppliesWater = `in`.nextStringOrNull()
            }
            "suFuel" -> {
               suppliesFuel = `in`.nextStringOrNull()
            }
            "suDiesel" -> {
               suppliesDiesel = `in`.nextStringOrNull()
            }
            "suDeck" -> {
               suppliesDeck = `in`.nextStringOrNull()
            }
            "suEngine" -> {
               suppliesEngine = `in`.nextStringOrNull()
            }
            "repairCode" -> {
               repairCode = `in`.nextStringOrNull()
            }
            "drydock" -> {
               drydock = `in`.nextStringOrNull()
            }
            "railway" -> {
               railway = `in`.nextStringOrNull()
            }
            "qtSanitation" -> {
               quarantineSanitation = `in`.nextStringOrNull()
            }
            "suAviationFuel" -> {
               suppliesAviationFuel = `in`.nextStringOrNull()
            }
            "harborUse" -> {
               harborUse = `in`.nextStringOrNull()
            }
            "ukcMgmtSystem" -> {
               ukcManagementSystem = `in`.nextStringOrNull()
            }
            "portSecurity" -> {
               portSecurity = `in`.nextStringOrNull()
            }
            "etaMessage" -> {
               etaMessage = `in`.nextStringOrNull()
            }
            "searchAndRescue" -> {
               searchAndRescue = `in`.nextStringOrNull()
            }
            "tss" -> {
               trafficSeparationScheme = `in`.nextStringOrNull()
            }
            "vts" -> {
               vesselTrafficService = `in`.nextStringOrNull()
            }
            "cht" -> {
               chemicalHoldingTankDisposal = `in`.nextStringOrNull()
            }
            "globalId" -> {
               globalId = `in`.nextStringOrNull()
            }
            "loRoro" -> {
               facilitiesRoro = `in`.nextStringOrNull()
            }
            "loSolidBulk" -> {
               facilitiesSolidBulk = `in`.nextStringOrNull()
            }
            "loContainer" -> {
               facilitiesContainer = `in`.nextStringOrNull()
            }
            "loBreakBulk" -> {
               facilitiesBreakBulk = `in`.nextStringOrNull()
            }
            "loOilTerm" -> {
               facilitiesOilTerminal = `in`.nextStringOrNull()
            }
            "loLongTerm" -> {
               facilitiesLongTerminal = `in`.nextStringOrNull()
            }
            "loOther" -> {
               facilitiesOther = `in`.nextStringOrNull()
            }
            "loDangCargo" -> {
               facilitiesDangerousCargo = `in`.nextStringOrNull()
            }
            "loLiquidBulk" -> {
               facilitiesLiquidBulk = `in`.nextStringOrNull()
            }
            "srIceBreaking" -> {
               servicesIceBreaking = `in`.nextStringOrNull()
            }
            "srDiving" -> {
               servicesDiving = `in`.nextStringOrNull()
            }
            "cranesContainer" -> {
               cranesContainer = `in`.nextStringOrNull()
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
               liquifiedNaturalGasTerminalDepth = `in`.nextStringOrNull()
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
           this.drydock = drydock
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