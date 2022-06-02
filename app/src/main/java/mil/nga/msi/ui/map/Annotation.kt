package mil.nga.msi.ui.map

data class Annotation(val type: Type, val id: String) {
   enum class Type {ASAM, MODU}
}