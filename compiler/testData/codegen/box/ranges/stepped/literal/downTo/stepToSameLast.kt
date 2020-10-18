// DONT_TARGET_EXACT_BACKEND: WASM
// WASM_MUTE_REASON: STDLIB_COLLECTIONS
// Auto-generated by GenerateSteppedRangesCodegenTestData. Do not edit!
// KJS_WITH_FULL_RUNTIME
// WITH_RUNTIME
import kotlin.test.*

fun box(): String {
    val intList = mutableListOf<Int>()
    for (i in 7 downTo 1 step 2) {
        intList += i
    }
    assertEquals(listOf(7, 5, 3, 1), intList)

    val longList = mutableListOf<Long>()
    for (i in 7L downTo 1L step 2L) {
        longList += i
    }
    assertEquals(listOf(7L, 5L, 3L, 1L), longList)

    val charList = mutableListOf<Char>()
    for (i in 'g' downTo 'a' step 2) {
        charList += i
    }
    assertEquals(listOf('g', 'e', 'c', 'a'), charList)

    return "OK"
}