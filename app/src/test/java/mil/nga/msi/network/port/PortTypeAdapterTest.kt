package mil.nga.msi.network.port

import assertPortsEqual
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.types.Decision
import mil.nga.msi.datasource.port.types.HarborType
import mil.nga.msi.datasource.port.types.HarborUse
import mil.nga.msi.datasource.port.types.RepairCode
import mil.nga.msi.datasource.port.types.Shelter
import mil.nga.msi.datasource.port.types.Size
import mil.nga.msi.datasource.port.types.UnderkeelClearance
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.StringReader

class PortTypeAdapterTest {

    private lateinit var typeAdapter: PortsTypeAdapter

    @Before
    fun setup() {
        typeAdapter = PortsTypeAdapter()
    }

    @Test
    fun testTypeAdapter() {
        val jsonIn = JsonReader(StringReader(portsJson))
        val read = typeAdapter.read(jsonIn).ports

        assertEquals(1, read.size)
        assertPortsEqual(port, read[0])
        assertEquals(jsonIn.peek(), JsonToken.END_DOCUMENT)

        jsonIn.close()
    }

    companion object {
        const val portsJson = """
            {
                "ports": [
                    {
                        "portNumber": 760,
                        "portName": "Port Name",
                        "regionNumber": 545,
                        "regionName": "GREENLAND  WEST COAST",
                        "countryCode": "GL",
                        "countryName": "Greenland",
                        "latitude": "68°42'00\"N",
                        "longitude": "52°52'00\"W",
                        "publicationNumber": "Sailing Directions Pub. 181 (Enroute) - Greenland and Iceland",
                        "chartNumber": null,
                        "navArea": "XVIII",
                        "harborSize": "S",
                        "harborType": "CN",
                        "shelter": "G",
                        "erTide": "N",
                        "erSwell": "N",
                        "erIce": "Y",
                        "erOther": "Y",
                        "overheadLimits": "U",
                        "chDepth": "23",
                        "anDepth": "23",
                        "cpDepth": "8",
                        "otDepth": null,
                        "tide": 3,
                        "maxVesselLength": null,
                        "maxVesselBeam": null,
                        "maxVesselDraft": null,
                        "goodHoldingGround": "N",
                        "turningArea": "U",
                        "firstPortOfEntry": "N",
                        "usRep": "N",
                        "ptCompulsory": "N",
                        "ptAvailable": null,
                        "ptLocalAssist": null,
                        "ptAdvisable": "Y",
                        "tugsSalvage": "N",
                        "tugsAssist": "N",
                        "qtPratique": "U",
                        "qtOther": "U",
                        "cmTelephone": "U",
                        "cmTelegraph": "U",
                        "cmRadio": "Y",
                        "cmRadioTel": "U",
                        "cmAir": "Y",
                        "cmRail": "U",
                        "loWharves": "Y",
                        "loAnchor": "U",
                        "loMedMoor": "U",
                        "loBeachMoor": "U",
                        "loIceMoor": "U",
                        "medFacilities": "Y",
                        "garbageDisposal": "N",
                        "degauss": "U",
                        "dirtyBallast": "N",
                        "crFixed": "U",
                        "crMobile": "Y",
                        "crFloating": "U",
                        "lifts100": "U",
                        "lifts50": "U",
                        "lifts25": "U",
                        "lifts0": "Y",
                        "srLongshore": "U",
                        "srElectrical": "U",
                        "srSteam": "U",
                        "srNavigEquip": "U",
                        "srElectRepair": "U",
                        "suProvisions": "Y",
                        "suWater": "Y",
                        "suFuel": "Y",
                        "suDiesel": "U",
                        "suDeck": "U",
                        "suEngine": "U",
                        "repairCode": "C",
                        "drydock": "U",
                        "railway": "S",
                        "qtSanitation": "U",
                        "suAviationFuel": "U",
                        "harborUse": "UNK",
                        "ukcMgmtSystem": "U",
                        "portSecurity": "U",
                        "etaMessage": "Y",
                        "searchAndRescue": "U",
                        "tss": "U",
                        "vts": "U",
                        "cht": "U",
                        "globalId": "{2C117765-0922-4542-A2B9-333253552952}",
                        "loRoro": "U",
                        "loSolidBulk": "U",
                        "loContainer": "U",
                        "loBreakBulk": "U",
                        "loOilTerm": "U",
                        "loLongTerm": "U",
                        "loOther": "U",
                        "loDangCargo": "U",
                        "loLiquidBulk": "U",
                        "srIceBreaking": "U",
                        "srDiving": "U",
                        "cranesContainer": "U",
                        "unloCode": "GL JEG",
                        "dnc": "a2800670, coa28e, gen28b, h2800670",
                        "s121WaterBody": "",
                        "s57Enc": null,
                        "s101Enc": "",
                        "dodWaterBody": "Baffin Bay; Arctic Ocean",
                        "alternateName": "Egedesminde",
                        "entranceWidth": null,
                        "lngTerminalDepth": null,
                        "offMaxVesselLength": null,
                        "offMaxVesselBeam": null,
                        "offMaxVesselDraft": null,
                        "ycoord": 68.70000000000005,
                        "xcoord": -52.86666699999995
                    }
                ]
            }"""

        val port = Port(
            portNumber = 760,
            portName ="Port Name",
            latitude = 68.70000000000005,
            longitude = -52.86666699999995
        ).apply {
            regionNumber = 545
            regionName = "GREENLAND  WEST COAST"
            countryCode = "GL"
            countryName = "Greenland"
            publicationNumber = "Sailing Directions Pub. 181 (Enroute) - Greenland and Iceland"
            chartNumber = null
            navigationArea = "XVIII"
            harborSize = Size.S
            harborType = HarborType.CN
            shelter = Shelter.G
            entranceRestrictionTide = Decision.N
            entranceRestrictionSwell = Decision.N
            entranceRestrictionIce = Decision.Y
            entranceRestrictionOther = Decision.Y
            overheadLimits = Decision.UNKNOWN
            channelDepth = 23
            anchorageDepth = 23
            cargoPierDepth = 8
            oilTerminalDepth = null
            tide = 3
            maxVesselLength = null
            maxVesselBeam = null
            maxVesselDraft = null
            goodHoldingGround = Decision.N
            turningArea = Decision.UNKNOWN
            firstPortOfEntry = Decision.N
            usRepresentative = "N"
            pilotageCompulsory = Decision.N
            pilotageAvailable = null
            pilotageLocalAssist = null
            pilotageAdvisable = Decision.Y
            tugsSalvage = Decision.N
            tugsAssist = Decision.N
            quarantinePratique = Decision.UNKNOWN
            quarantineOther = Decision.UNKNOWN
            communicationsTelephone = Decision.UNKNOWN
            communicationsTelegraph = Decision.UNKNOWN
            communicationsRadio = Decision.Y
            communicationsRadioTelephone = Decision.UNKNOWN
            communicationsAir = Decision.Y
            communicationsRail = Decision.UNKNOWN
            facilitiesWharves = Decision.Y
            facilitiesAnchor = Decision.UNKNOWN
            facilitiesMedMoor = Decision.UNKNOWN
            facilitiesBeachMoor = Decision.UNKNOWN
            facilitiesIceMoor = Decision.UNKNOWN
            medicalFacilities = Decision.Y
            garbageDisposal = Decision.N
            degauss = Decision.UNKNOWN
            dirtyBallast = Decision.N
            cranesFixed = Decision.UNKNOWN
            cranesMobile = Decision.Y
            cranesFloating = Decision.UNKNOWN
            lifts100 = Decision.UNKNOWN
            lifts50 = Decision.UNKNOWN
            lifts25 = Decision.UNKNOWN
            lifts0 = Decision.Y
            servicesLongshore = Decision.UNKNOWN
            servicesElectrical = Decision.UNKNOWN
            servicesElectricalRepair = Decision.UNKNOWN
            servicesSteam = Decision.UNKNOWN
            servicesNavigationalEquipment = Decision.UNKNOWN
            servicesElectrical = Decision.UNKNOWN
            suppliesProvisions = Decision.Y
            suppliesWater = Decision.Y
            suppliesFuel = Decision.Y
            suppliesDiesel = Decision.UNKNOWN
            suppliesDeck = Decision.UNKNOWN
            suppliesEngine = Decision.UNKNOWN
            repairCode = RepairCode.C
            dryDock = Size.UNKNOWN
            railway = Size.S
            quarantineSanitation = Decision.UNKNOWN
            suppliesAviationFuel = Decision.UNKNOWN
            harborUse = HarborUse.UNKNOWN
            ukcManagementSystem = UnderkeelClearance.UNKNOWN
            portSecurity = Decision.UNKNOWN
            etaMessage = Decision.Y
            searchAndRescue = Decision.UNKNOWN
            trafficSeparationScheme = Decision.UNKNOWN
            vesselTrafficService = Decision.UNKNOWN
            chemicalHoldingTankDisposal = Decision.UNKNOWN
            globalId = "{2C117765-0922-4542-A2B9-333253552952}"
            facilitiesRoro = Decision.UNKNOWN
            facilitiesSolidBulk = Decision.UNKNOWN
            facilitiesContainer = Decision.UNKNOWN
            facilitiesBreakBulk = Decision.UNKNOWN
            facilitiesOilTerminal = Decision.UNKNOWN
            facilitiesLongTerminal = Decision.UNKNOWN
            facilitiesOther = Decision.UNKNOWN
            facilitiesDangerousCargo = Decision.UNKNOWN
            facilitiesLiquidBulk = Decision.UNKNOWN
            servicesIceBreaking = Decision.UNKNOWN
            servicesDiving = Decision.UNKNOWN
            cranesContainer = Decision.UNKNOWN
            unloCode = "GL JEG"
            dnc = "a2800670, coa28e, gen28b, h2800670"
            s121WaterBody = ""
            s57Enc = null
            s101Enc = ""
            dodWaterBody = "Baffin Bay; Arctic Ocean"
            alternateName = "Egedesminde"
            entranceWidth = null
            liquifiedNaturalGasTerminalDepth = null
            maxVesselLength = null
            maxVesselBeam = null
            maxVesselDraft = null
        }
    }
}


