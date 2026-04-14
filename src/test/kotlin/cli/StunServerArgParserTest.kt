package cli

import com.break2bits.cli.BoolArgDefinition
import com.break2bits.cli.IntArgDefinition
import com.break2bits.cli.StunServerArgParser
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StunServerArgParserTest {
    @Test
    fun testGetInt_requiredFound() {
        val parser = StunServerArgParser(arrayOf("--arg", "123"))

        val intValue = parser.getInt(IntArgDefinition("-a", "--arg"))

        assertEquals(123, intValue);
    }

    @Test
    fun testGetInt_optionalDefault() {
        val parser = StunServerArgParser(arrayOf())

        val intValue = parser.getInt(IntArgDefinition("-a", "--arg", default = 111))

        assertEquals(111, intValue);
    }

    @Test
    fun testGetInt_requiredNotFound() {
        val parser = StunServerArgParser(arrayOf("-f", "100"))

        assertThrows<IllegalArgumentException> {
            parser.getInt(IntArgDefinition("-a", "--arg"))
        }
    }

    @Test
    fun testGetBool_requiredFound() {
        val parser = StunServerArgParser(arrayOf("-e"))

        val enabled = parser.getBool(BoolArgDefinition("-e", "--enabled"))

        assertTrue(enabled);
    }

    @Test
    fun testGetBool_requiredNotFound() {
        val parser = StunServerArgParser(arrayOf("-f", "100"))

        assertThrows<IllegalArgumentException> {
            parser.getBool(BoolArgDefinition("-e", "--enabled"))
        }
    }

    @Test
    fun testGetBool_optionalDefault() {
        val parser = StunServerArgParser(arrayOf())


        val enabled = parser.getBool(BoolArgDefinition("-e", "--enabled", true))

        assertTrue(enabled);
    }
}