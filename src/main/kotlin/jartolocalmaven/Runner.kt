package jartolocalmaven

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*


fun main(args: Array<String>) {


    println("Parse arguments : ${args.toList().joinToString()}")

    val deps: MutableList<DependencyUpload> = ArrayList()
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

                    val dependency = parseFromString(depPath)
                    check(dependency)
                    checkFile(jarPath)
                    deps.add(DependencyUpload(dependency, jarPath))

                }}
            }
        }

        if (dep.valid() && file.isNotBlank()) {
            deps.add(DependencyUpload(dep, file))
        }
    }

    if (deps.isEmpty()) {
        val dep = input("Input dependency <groupId>:<artifactId>:<version>:(optional:<packaging>)")
        val dependency = parseFromString(dep)
        check(dependency)

        val path = input("Input file path")
        checkFile(path)
        deps.add(DependencyUpload(dependency, path))
    }

    println("detect $deps")

    for (depUpload in deps) {
        val dep = depUpload.dep
        val file = depUpload.file




        runCommand("mvn install:install-file " +
                "-Dfile=\"${file}\" " +
                "-DgroupId=${dep.groupId} " +
                "-DartifactId=${dep.artifactId} " +
                "-Dversion=${dep.version} " +
                "-Dpackaging=${dep.packaging} " +
                "-DgeneratePom=true")
    }


}

private fun checkFile(path: String) {
    if (!File(path).exists()) {
        throw RuntimeException("File NOT EXIST $path")
    }
}

private fun check(dependency: Dependency) {
    if (!dependency.valid()) {
        throw RuntimeException("Error dependency $dependency")
    }
}

fun input(text: String): String {
    val input = Scanner(System.`in`)
    print("$text: ")

    return input.nextLine()
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


