object Version {

    const val major = 0
    const val minor = 3
    const val patch = 0
    const val code = 15

    override fun toString(): String {
        return "$major.$minor.$patch-$code"
    }

}