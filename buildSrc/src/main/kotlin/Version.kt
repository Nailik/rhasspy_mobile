object Version {

    const val major = 1
    const val minor = 3
    const val patch = 2
    const val code = 23

    override fun toString(): String {
        return "$major.$minor.$patch-$code"
    }

}