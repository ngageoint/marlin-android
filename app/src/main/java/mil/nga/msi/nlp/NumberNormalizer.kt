package mil.nga.msi.nlp

import edu.stanford.nlp.ie.NumberNormalizer

class NumberNormalizer {
   companion object {
      fun wordToNumberOrNull(word: String): Number? {
         return try {
            NumberNormalizer.wordToNumber(word)
         } catch(e: NumberFormatException) { null }
      }
   }
}