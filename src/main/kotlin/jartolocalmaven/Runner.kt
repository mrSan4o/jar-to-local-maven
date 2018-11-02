package jartolocalmaven

import java.io.BufferedReader
import java.io.InputStreamReader


fun main(args: Array<String>) {
    var dep: Dependency = Dependency.empty()
    var packaging = "jar"
    var file = ""

    println("Parse arguments...")
    for (i in 0 until args.size) {
        val a = args.get(i)
        println("$i : $a")
        val split = a.split("=")
        val name = split[0]
        val value = split[1]

        if ("dep" == name){
            dep = parseFromString(value)
        }
        if ("packaging" == name){
            packaging = value
        }
        if ("file" == name){
            file = value
        }
    }

    println("detect $dep")

    if (!dep.valid()){
        throw IllegalArgumentException("No [dep] in arguments")
    }
    if (file.isBlank()){
        throw IllegalArgumentException("No [file] in arguments")
    }

    runCommand("mvn install:install-file " +
            "-Dfile=${file} " +
            "-DgroupId=${dep.groupId} " +
            "-DartifactId=${dep.artifactId} " +
            "-Dversion=${dep.version} " +
            "-Dpackaging=$packaging " +
            "-DgeneratePom=true")
}

fun parseFromString(value: String): Dependency {
    val split = value.split(':')
    if (split.size != 3) {
        throw IllegalArgumentException("Error path value : $value")
    }
    return Dependency(split[0], split[1], split[2])
}

class Dependency(
        val groupId: String,
        val artifactId: String,
        val version: String
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


