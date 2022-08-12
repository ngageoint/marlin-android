package mil.nga.msi.ui.navigationalwarning

sealed class NavigationalWarningAction {
   class Share(val text: String) : NavigationalWarningAction()
}