object Version {

    const val major = 0
    const val minor = 5
    const val patch = 5
    const val code = 142

    override fun toString(): String {
        return "$major.$minor.$patch-$code"
    }

}