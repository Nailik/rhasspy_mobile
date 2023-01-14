object Version {

    const val major = 0
    const val minor = 4
    const val patch = 1
    const val code = 30

    override fun toString(): String {
        return "$major.$minor.$patch-$code"
    }

}