object Version {

    const val major = 0
    const val minor = 3
    const val patch = 1
    const val code = 16

    override fun toString(): String {
        return "$major.$minor.$patch-$code"
    }

}