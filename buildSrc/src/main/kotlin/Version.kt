object Version {

    const val major = 0
    const val minor = 2
    const val patch = 0
    const val code = 10

    override fun toString(): String {
        return "$major.$minor.$patch-$code"
    }

}