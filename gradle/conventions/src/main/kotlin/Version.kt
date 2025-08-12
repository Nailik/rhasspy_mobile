object Version {

    const val MAJOR = 0
    const val MINOR = 6
    const val PATCH = 1
    const val CODE = 149

    override fun toString(): String {
        return "$MAJOR.$MINOR.$PATCH-$CODE"
    }

}