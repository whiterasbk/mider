package whiter.music.mider.annotation

/**
 * 表示已经写好测试的方法, 属性
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class Tested
