package jartolocalmaven

import org.junit.Test

class RunnerTest {
    @Test
    fun testSearch() {
        val searchFileByDependency = SearchFileByDependency("C:\\Users\\AlekNazarov\\.m2\\repository\\")

        val path = "io.realm:realm-gradle-plugin:5.8.0"

        val dependency = Dependency.parseFromString(path)

        val file = searchFileByDependency.find(dependency)

        println(file)

        val get = System.getenv().get("M2_REPOSITORY")
        println(get)

    }
}
