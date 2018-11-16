package jartolocalmaven

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*


fun main(args: Array<String>) {

    val action = input("""Enter action:
        1 - add
        2 - remove
        """)
    when(action){
        "1" -> importDependency()
        "2" -> removeDependency()
        else -> System.exit(-1)
    }



}

fun removeDependency() {
    val dep = input("Input dependency <groupId>:<artifactId>:<version>:(optional:<packaging>)")
    val dependency = Dependency.parseFromString(dep)


    val searchFileByDependency = SearchFileByDependency(mavenRepository())

    val file = searchFileByDependency.find(dependency)

    if (!file.deleteRecursively()){
        throw RuntimeException("File NOT DELETED $file")
    }
    println("File DELETED $file")
}

fun mavenRepository(): String {
    val rep = System.getenv().get("M2_REPOSITORY")
    if (rep!=null){
        return rep
    }
    println("System enviroment M2_REPOSITORY not specified")

    val path = input("Input maven repository path")
    if (path.isBlank()){
        val defaultPath = "C:\\Users\\AlekNazarov\\.m2\\repository"
        println("search in default : $defaultPath")
        return defaultPath
    }
    return path
}

private fun importDependency() {
    val dep = input("Input dependency <groupId>:<artifactId>:<version>:(optional:<packaging>)")
    val dependency = Dependency.parseFromString(dep)
    checkDependency(dependency)

    val path = input("Input file path")
    checkFile(path)
    val dependencyUpload = DependencyUpload(dependency, path)


    println("detect $dependencyUpload")

    runCommand("mvn install:install-file " +
            "-Dfile=\"${dependencyUpload.file}\" " +
            "-DgroupId=${dependencyUpload.dep.groupId} " +
            "-DartifactId=${dependencyUpload.dep.artifactId} " +
            "-Dversion=${dependencyUpload.dep.version} " +
            "-Dpackaging=${dependencyUpload.dep.packaging} " +
            "-DgeneratePom=true")
}

private fun checkFile(path: String) {
    if (!File(path).exists()) {
        throw RuntimeException("File NOT EXIST $path")
    }
}

private fun checkDependency(dependency: Dependency) {
    if (!dependency.valid()) {
        throw RuntimeException("Error dependency $dependency")
    }
}

fun input(text: String): String {
    val input = Scanner(System.`in`)
    print("$text: ")

    return input.nextLine()
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


