object Version {

    const val major = 0
    const val minor = 5
    const val patch = 1
    const val code = 124

    override fun toString(): String {
        return "$major.$minor.$patch-$code"
    }

}