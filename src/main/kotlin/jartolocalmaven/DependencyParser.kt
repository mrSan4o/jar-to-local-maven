package jartolocalmaven

class DependencyParser {
    companion object {
        fun parseFromString(value: String): Dependency {
            val split = value.split(':')
            if (split.size != 3) {
                throw IllegalArgumentException("Error path value : $value")
            }
            return Dependency(split[0], split[1], split[2])
        }
    }
}