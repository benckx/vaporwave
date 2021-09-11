package be.encelade.vaporwave.utils

object CollectionUtils {

    fun <E> Collection<E>.exists(predicate: (E) -> Boolean): Boolean {
        return find { predicate(it) } != null
    }

}
