package day5

import junit.framework.Assert.assertEquals
import org.junit.Test


class CostamTest {
    @Test
     fun `test cos tam`() {
        val program = "1101,100,-1,4,0".split(",").toMutableList()
        runProgram(program)
        assertEquals("99", program.last())
    }
}