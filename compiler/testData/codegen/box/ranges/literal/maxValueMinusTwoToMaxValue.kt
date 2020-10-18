// DONT_TARGET_EXACT_BACKEND: WASM
// WASM_MUTE_REASON: STDLIB_COLLECTIONS
// KJS_WITH_FULL_RUNTIME
// Auto-generated by org.jetbrains.kotlin.generators.tests.GenerateRangesCodegenTestData. DO NOT EDIT!
// WITH_RUNTIME


val MaxI = Int.MAX_VALUE
val MaxB = Byte.MAX_VALUE
val MaxS = Short.MAX_VALUE
val MaxL = Long.MAX_VALUE
val MaxC = Char.MAX_VALUE

fun box(): String {
    val list1 = ArrayList<Int>()
    for (i in (MaxI - 2)..MaxI) {
        list1.add(i)
        if (list1.size > 23) break
    }
    if (list1 != listOf<Int>(MaxI - 2, MaxI - 1, MaxI)) {
        return "Wrong elements for (MaxI - 2)..MaxI: $list1"
    }

    val list2 = ArrayList<Int>()
    for (i in (MaxB - 2).toByte()..MaxB) {
        list2.add(i)
        if (list2.size > 23) break
    }
    if (list2 != listOf<Int>((MaxB - 2).toInt(), (MaxB - 1).toInt(), MaxB.toInt())) {
        return "Wrong elements for (MaxB - 2).toByte()..MaxB: $list2"
    }

    val list3 = ArrayList<Int>()
    for (i in (MaxS - 2).toShort()..MaxS) {
        list3.add(i)
        if (list3.size > 23) break
    }
    if (list3 != listOf<Int>((MaxS - 2).toInt(), (MaxS - 1).toInt(), MaxS.toInt())) {
        return "Wrong elements for (MaxS - 2).toShort()..MaxS: $list3"
    }

    val list4 = ArrayList<Long>()
    for (i in (MaxL - 2).toLong()..MaxL) {
        list4.add(i)
        if (list4.size > 23) break
    }
    if (list4 != listOf<Long>((MaxL - 2).toLong(), (MaxL - 1).toLong(), MaxL)) {
        return "Wrong elements for (MaxL - 2).toLong()..MaxL: $list4"
    }

    val list5 = ArrayList<Char>()
    for (i in (MaxC - 2)..MaxC) {
        list5.add(i)
        if (list5.size > 23) break
    }
    if (list5 != listOf<Char>((MaxC - 2), (MaxC - 1), MaxC)) {
        return "Wrong elements for (MaxC - 2)..MaxC: $list5"
    }

    return "OK"
}
