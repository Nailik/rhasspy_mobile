import de.fayard.refreshVersions.core.DependencyGroup

object CashApp : DependencyGroup(group = "app.cash") {

    object Sqldelight : DependencyGroup(group = "app.cash.sqldelight") {
        val android = module("android-driver")
        val ios = module("native-driver")
        val coroutines = module("coroutines-extensions")
    }

}