package cli.parse

import com.break2bits.binary.toByteArray
import com.break2bits.message.header.StunHeader
import com.break2bits.message.header.StunMessageType
import com.break2bits.parse.StunHeaderParser
import com.break2bits.parse.StunParseException
import org.junit.jupiter.api.assertThrows
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class StunHeaderParserTest {
    @Test
    fun testParse_underflow() {
        val buff = ByteBuffer.wrap(byteArrayOf())
        val parser = StunHeaderParser()

        val exception = assertThrows<StunParseException> {
            parser.parse(buff)
        }

        assertContains(exception.message!!, "Message did not contain enough bytes to parse Stun header")
    }

    @Test
    fun testParse_invalidMessageType() {
        val buff = ByteBuffer.wrap(byteArrayOf(0xf, 0xf))
        val parser = StunHeaderParser()

        val exception = assertThrows<StunParseException> {
            parser.parse(buff)
        }

        assertContains(exception.message!!, "Failed to parse header message type")
    }

    @Test
    fun testParse_messageLengthTooShort() {
        val buff = ByteBuffer.wrap(byteArrayOf(0x00, 0x01, 0x00, 0x09))
        val parser = StunHeaderParser()

        val exception = assertThrows<StunParseException> {
            parser.parse(buff)
        }

        assertContains(exception.message!!, "Invalid message length")
    }

    @Test
    fun testParse_invalidMagicCookie() {
        val topFour = byteArrayOf(0x0, 0x1, 0xf, 0xf)
        val magicCookieData = 0xffffu
        val buff = ByteBuffer.wrap(topFour.plus(magicCookieData.toByteArray()))
        val parser = StunHeaderParser()

        val exception = assertThrows<StunParseException> {
            parser.parse(buff)
        }

        assertContains(exception.message!!, "Invalid magic cookie")
    }

    @Test
    fun testParse() {
        val topFour = byteArrayOf(0x0, 0x1, 0x0, 0x14)
        val transactionId = ByteArray(12)
        transactionId[transactionId.size - 1] = 0x01
        val buff = ByteBuffer.wrap(
            topFour
                .plus(StunHeader.MAGIC_COOKIE.toByteArray())
                .plus(transactionId)
        )
        val parser = StunHeaderParser()

        val header = parser.parse(buff)

        assertEquals(StunMessageType.BINDING_REQUEST, header.messageType)
        assertEquals(20u, header.messageLength)
        assertEquals(StunHeader.MAGIC_COOKIE, header.magicCookie)
        assertContentEquals(transactionId, header.transactionId)
    }
}