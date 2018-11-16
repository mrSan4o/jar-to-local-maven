package jartolocalmaven

import java.io.File
import java.util.*

class SearchFileByDependency(private val path: String) {

    private lateinit var parent: File
    private lateinit var childs: List<File>

    fun find(dependency: Dependency): File {
        parent = File(path)
        childs = childsOfParent()

        val parts = parts(dependency.groupId)
        for (part in parts) {
            findNext(part)
        }

        val artifactId = dependency.artifactId
        findNext(artifactId)

        val version = dependency.version
        findNext(version)

        return parent
    }

    private fun findNext(part: String) {
        parent = findByName(childs, part)
        childs = childsOfParent()
    }

    private fun findByName(files: List<File>, name: String): File {
        if (files.isEmpty()){
            throw RuntimeException("Empty folder")
        }
        return files.find { it -> it.name == name }
                ?: throw RuntimeException("Not found $name in ${files.first().absolutePath}")
    }

    private fun childsOfParent() = parent.listFiles { it -> it.isDirectory }.toList()

    private fun parts(part: String): List<String> {
        val tokens = part.split(".")

        val list = ArrayList<String>()
        for (token in tokens) {
            list.add(token)
        }
        return list
    }
}