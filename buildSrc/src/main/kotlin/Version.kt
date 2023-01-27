object Version {

    const val major = 0
    const val minor = 4
    const val patch = 7
    const val code = 58

    override fun toString(): String {
        return "$major.$minor.$patch-$code"
    }

}