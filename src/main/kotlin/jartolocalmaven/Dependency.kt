package jartolocalmaven

class Dependency(
        val groupId: String,
        val artifactId: String,
        val version: String,
        val packaging: String = "jar"
) {
    companion object {
        fun empty(): Dependency {
            return Dependency("", "", "")
        }

        fun parseFromString(value: String): Dependency {
            val split = value.split(':')
            if (split.size < 3) {
                throw IllegalArgumentException("Error path value : $value")
            }
            if (split.size == 4) {
                return Dependency(split[0], split[1], split[2], split[3])
            }
            return Dependency(split[0], split[1], split[2])
        }

    }

    fun valid(): Boolean {
        return groupId.isNotEmpty() && artifactId.isNotEmpty() && version.isNotEmpty()
    }

    override fun toString(): String {
        return if (valid()) {
            "$groupId:$artifactId:$version"
        } else {
            "NONE"
        }
    }
}