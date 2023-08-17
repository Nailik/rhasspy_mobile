object Version {

    const val major = 0
    const val minor = 5
    const val patch = 4
    const val code = 139

    override fun toString(): String {
        return "$major.$minor.$patch-$code"
    }

}