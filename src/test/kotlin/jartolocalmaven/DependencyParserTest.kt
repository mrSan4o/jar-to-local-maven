package jartolocalmaven

import org.junit.Assert.*
import org.junit.Test

class DependencyParserTest {

    @Test
    fun testParseFromString() {
        val dep = Dependency.parseFromString("com:test:1.2")

        assertEquals("com", dep.groupId)
        assertEquals("test", dep.artifactId)
        assertEquals("1.2", dep.version)
    }
    @Test
    fun testParseFromUrl() {

    }

}