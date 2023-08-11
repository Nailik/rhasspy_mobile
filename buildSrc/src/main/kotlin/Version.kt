object Version {

    const val major = 0
    const val minor = 5
    const val patch = 2
    const val code = 127

    override fun toString(): String {
        return "$major.$minor.$patch-$code"
    }

}