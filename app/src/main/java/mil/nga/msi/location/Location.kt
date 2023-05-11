package mil.nga.msi.location

import com.google.android.gms.maps.model.LatLng

class Point {
   companion object {
//      fun asPoint(text: String): LatLng {
//         val initialSplit = text.trim().split(" ,")
//         if (initialSplit.size == 2) {
//            val latitude = initialSplit.first().toDouble()
//            val longitude = initialSplit.last().toDouble()
//            return LatLng(latitude, longitude)
//         }
//      }
//
//      fun parse(coordinate: String): LatLng {
//         val split =
//      }

      fun splitCoordinates(coordinates: String) {

      }
   }
}

//static func parse(coordinates: String?) -> CLLocationCoordinate2D? {
//   var latitude: CLLocationDegrees?
//   var longitude: CLLocationDegrees?
//
//   let split = CLLocationCoordinate2D.splitCoordinates(coordinates: coordinates)
//   if split.count == 2 {
//      latitude = CLLocationCoordinate2D.parse(coordinate: split[0], enforceLatitude: true)
//      longitude = CLLocationCoordinate2D.parse(coordinate: split[1], enforceLatitude: false)
//   }
//   if let latitude = latitude, let longitude = longitude {
//      return CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
//   }
//   return nil
//}

//init?(coordinateString: String) {
//   if initialSplit.count == 2, let latitude = Double(initialSplit[0]), let longitude = Double(initialSplit[1]) {
//      self.init(latitude: latitude, longitude: longitude)
//      return
//   }
//
//   if let coordinate = CLLocationCoordinate2D.parse(coordinates: coordinateString) {
//      self.init(latitude: coordinate.latitude, longitude: coordinate.longitude)
//      return
//   }
//
//   if initialSplit.count == 1, let _ = Double(initialSplit[0]) {
//      // this is not a valid coordinate, just bail
//      return nil
//   }
//
//   let p = #"(?<latdeg>-?[0-9]*\.?\d+)[\s°-]*(?<latminutes>\d{1,2}\.?\d+)?[\s\`'-]*(?<latseconds>\d{1,2}\.?\d+)?[\s\" ]?(?<latdirection>([NOEWS])?)[\s,]*(?<londeg>-?[0-9]*\.?\d+)[\s°-]*(?<lonminutes>\d{1,2}\.?\d+)?[\s\`'-]*(?<lonseconds>\d{1,2}\.?\d+)?[\s\" ]*(?<londirection>([NOEWS])?)"#
//
//   var foundLat: Bool = false
//   var foundLon: Bool = false
//   var latlon = [Double]()
//   do {
//      let regex = try NSRegularExpression(pattern: p)
//         let matches = regex.matches(in: coordinateString, range: NSRange(coordinateString.startIndex..., in: coordinateString))
//         var latdegrees: Double = 0.0
//         var latmultiplier: Double = 1.0
//         var londegrees: Double = 0.0
//         var lonmultiplier: Double = 1.0
//
//         for match in matches {
//            for component in ["latdirection", "latdeg", "latminutes", "latseconds", "londirection", "londeg", "lonminutes", "lonseconds"] {
//               let nsrange = match.range(withName: component)
//               if nsrange.location != NSNotFound,
//               let range = Range(nsrange, in: coordinateString),
//               !range.isEmpty
//               {
//                  if component == "latdirection" {
//                     latmultiplier = "NEO".contains(coordinateString[range]) ? 1.0 : -1.0
//                  } else if component == "latdeg" {
//                     foundLat = true
//                     latdegrees += Double(coordinateString[range]) ?? 0.0
//                  } else if component == "latminutes" {
//                     latdegrees += (Double(coordinateString[range]) ?? 0.0) / 60
//                  } else if component == "latseconds" {
//                     latdegrees += (Double(coordinateString[range]) ?? 0.0) / 3600
//                  } else if component == "londirection" {
//                     lonmultiplier = "NEO".contains(coordinateString[range]) ? 1.0 : -1.0
//                  } else if component == "londeg" {
//                     foundLon = true
//                     londegrees += Double(coordinateString[range]) ?? 0.0
//                  } else if component == "lonminutes" {
//                     londegrees += (Double(coordinateString[range]) ?? 0.0) / 60
//                  } else if component == "lonseconds" {
//                     londegrees += (Double(coordinateString[range]) ?? 0.0) / 3600
//                  }
//               }
//            }
//
//            if !foundLat || !foundLon {
//               return nil
//            }
//            latlon.append(latmultiplier * latdegrees)
//            latlon.append(lonmultiplier * londegrees)
//         }
//      } catch {
//         print(error)
//         return nil
//      }
//      if !foundLat || !foundLon {
//         return nil
//      }
//      self.init(latitude: latlon[0], longitude: latlon[1])
//   }
//}