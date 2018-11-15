package jartolocalmaven

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


fun main(args: Array<String>) {

    val deps: MutableList<DependencyUpload> = ArrayList()

    println("Parse arguments...")
    for (i in 0 until args.size) {
        val a = args[i]
        println("$i : $a")
        val split = a.split("=")
        val name = split[0]
        val value = split[1]

        var dep: Dependency = Dependency.empty()

        var file = ""

        when (name) {
            "dep" -> {
                if (dep.valid()) {
                    throw IllegalArgumentException("dependency already specified")
                }
                dep = parseFromString(value)
            }
            "file" -> {
                file = value
            }
            "config" -> {
                val f = File(value)
                if (!f.exists()){
                    throw IllegalStateException("File config $f NOT EXIST")
                }
                f.forEachLine{ it -> run {
                    val parts = it.split('=')
                    val depPath = parts[0]
                    val jarPath = parts[1]

                    deps.add(DependencyUpload(parseFromString(depPath), jarPath))

                }}
            }
        }

        if (dep.valid() && file.isNotBlank()) {
            deps.add(DependencyUpload(dep, file))
        }
    }

    println("detect $deps")

    for (depUpload in deps) {
        val dep = depUpload.dep
        val file = depUpload.file


        if (!dep.valid()){
            throw IllegalArgumentException("No [dep] in arguments")
        }
        if (file.isBlank()){
            throw IllegalArgumentException("No [file] in arguments")
        }

        runCommand("mvn install:install-file " +
                "-Dfile=\"${file}\" " +
                "-DgroupId=${dep.groupId} " +
                "-DartifactId=${dep.artifactId} " +
                "-Dversion=${dep.version} " +
                "-Dpackaging=${dep.packaging} " +
                "-DgeneratePom=true")
    }


}


fun parseFromString(value: String): Dependency {
    return DependencyParser.parseFromString(value)
}

class DependencyUpload(
        val dep: Dependency,
        val file: String

)

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
    }

    fun valid(): Boolean {
        return groupId.isNotEmpty() && artifactId.isNotEmpty() && version.isNotEmpty()
    }

    override fun toString(): String {
        if (valid()) {
            return "$groupId:$artifactId:$version"
        }else{
            return "NONE"
        }
    }
}

private fun runCommand(command: String) {
    println("run command '$command'...")

    val builder = ProcessBuilder(
            "cmd.exe", "/c", command)
    builder.redirectErrorStream(true)
    val p = builder.start()
    val r = BufferedReader(InputStreamReader(p.inputStream))
    var line: String?
    while (true) {
        line = r.readLine()
        if (line == null) {
            break
        }
        println(line)
    }
}


