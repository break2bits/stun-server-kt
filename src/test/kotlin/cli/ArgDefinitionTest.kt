package cli

import com.break2bits.cli.ArgDefinition
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ArgDefinitionTest {
    @Test
    fun testMatches_matchesShort() {
        val argDef = object : ArgDefinition("-k", "--key") {}

        assertTrue(argDef.matches("-k"));
    }

    @Test
    fun testMatches_matchesLong() {
        val argDef = object : ArgDefinition("-k", "--key") {}

        assertTrue(argDef.matches("--key"));
    }

    @Test
    fun testMatches_noMatch() {
        val argDef = object : ArgDefinition("-k", "--key") {}

        assertFalse(argDef.matches("key"));
    }

    @Test
    fun testMatches_noMatchEmpty() {
        val argDef = object : ArgDefinition("-k", "--key") {}

        assertFalse(argDef.matches("k"));
    }

    @Test
    fun testMatches_noMatchNull() {
        val argDef = object : ArgDefinition("-k", "--key") {}

        assertFalse(argDef.matches(null));
    }
}